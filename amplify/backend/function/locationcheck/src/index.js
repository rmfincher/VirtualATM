const { DynamoDBClient } = require("@aws-sdk/client-dynamodb");
const { DynamoDBDocumentClient, QueryCommand, GetCommand, UpdateCommand } = require("@aws-sdk/lib-dynamodb");

const dynamoDBClient = new DynamoDBClient({});
const docClient = DynamoDBDocumentClient.from(dynamoDBClient);

const radiusInMeters = 30 //maximum distance between users

exports.handler = async (event) => {
    try {
        console.log(`EVENT: ${JSON.stringify(event)}`);

        for (const record of event.Records) {
            if (record.eventName === 'INSERT' || record.eventName === 'MODIFY') {
                const transactionData = record.dynamodb.NewImage;

                const recipientUsername = transactionData.recipientUsername.S;
                
                const senderUsername = transactionData.senderUsername.S;
            
                const amountToSend = transactionData.funds.N;
                
                console.log(amountToSend);

                const senderData = await getUserData(senderUsername);

                let recipientData;

                // Check if recipient has provided location data
                do {
                    recipientData = await getUserData(recipientUsername);
                    console.log('Waiting for recipient location data...');
                    await new Promise(resolve => setTimeout(resolve, 1000)); // Wait for 1 second
                } while (recipientData.latitude === 0 || recipientData.longitude === 0);

                console.log('Recipient location data is available.');
                
                console.log('Recipient Long:' + recipientData.longitude)
                console.log('Recipient Lat:' + recipientData.latitude)
                console.log('Sender Long:' + recipientData.longitude)
                console.log('Sender Lat:' + recipientData.latitude)

                // Check if recipient is within radius
                const isWithinRadius = checkLocationWithinRadius(senderData.latitude, senderData.longitude, recipientData.latitude, recipientData.longitude, radiusInMeters);
                if (isWithinRadius) {
                    console.log('Recipient is within the specified radius. Proceeding with transfer.');
                    await updateFunds(senderData, recipientData, amountToSend);
                } else {
                    console.log('Recipient is not within the specified radius. Transfer canceled.');
                    // Optionally, you can add rollback logic here if needed
                }
            }
        }
    } catch (error) {
        console.error('Error processing DynamoDB record:', error);
        throw error;
    }
};

async function fetchTransactionData(recipient) {
    const command = new QueryCommand({
        TableName: 'Transaction-7pfb2gxhujdghgkp3eotjdjuym-dev',
        IndexName: 'recipientUsername-index',
        KeyConditionExpression: "recipientUsername = :recipient",
        ExpressionAttributeValues: {
            ":recipient": recipient
        }
    });

    try {
        const response = await docClient.send(command);
        return response.Items[0]; // Assuming you want to return the first item
    } catch (error) {
        console.error("Error fetching transaction data:", error);
        throw error;
    }
}

async function getUserData(user) {
    const command = new QueryCommand({
        TableName: 'User-7pfb2gxhujdghgkp3eotjdjuym-dev',
        IndexName: 'username-index',
        KeyConditionExpression: "username = :user",
        ExpressionAttributeValues: {
            ":user": user
        }
    });

    try {
        const response = await docClient.send(command);
        return response.Items[0];
    } catch (error) {
        console.error("Error fetching user data:", error);
        throw error;
    }
}

async function updateFunds(senderData, recipientData, amountToSend) {
    const senderParams = {
        TableName: 'User-7pfb2gxhujdghgkp3eotjdjuym-dev',
        Key: { id: senderData.id },
        UpdateExpression: 'SET funds = funds - :amount',
        ExpressionAttributeValues: {
            ':amount': Number(amountToSend)
        }
    };

    const recipientParams = {
        TableName: 'User-7pfb2gxhujdghgkp3eotjdjuym-dev',
        Key: { id: recipientData.id },
        UpdateExpression: 'SET funds = funds + :amount',
        ExpressionAttributeValues: {
            ':amount': Number(amountToSend)
        }
    };

    try {
        await Promise.all([
            docClient.send(new UpdateCommand(senderParams)),
            docClient.send(new UpdateCommand(recipientParams))
        ]);
        console.log("Funds updated successfully.");
    } catch (error) {
        console.error("Error updating funds:", error);
        throw error;
    }
}

function checkLocationWithinRadius(user1Latitude, user1Longitude, user2Latitude, user2Longitude, radiusInMeters) {
    const earthRadiusInMeters = 6371000; // Radius of the Earth in meters
    const dLat = deg2rad(user2Latitude - user1Latitude);
    const dLon = deg2rad(user2Longitude - user1Longitude);

    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(deg2rad(user1Latitude)) * Math.cos(deg2rad(user2Latitude)) *
        Math.sin(dLon / 2) * Math.sin(dLon / 2);

    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    const distanceInMeters = earthRadiusInMeters * c;
    
    console.log(distanceInMeters);

    return distanceInMeters <= radiusInMeters;
}

function deg2rad(deg) {
    return deg * (Math.PI / 180);
}

