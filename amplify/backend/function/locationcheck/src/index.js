const AWS = require('aws-sdk');

const dynamoDB = new AWS.DynamoDB.DocumentClient();
const radiusInFeet = 100;
const radiusInKm = radiusInFeet * 0.0003048;

exports.handler = async (event) => {
  try {
    console.log(`EVENT: ${JSON.stringify(event)}`);
    
    for (const record of event.Records) {
      if (record.eventName === 'MODIFY') { // Check if the event is a modification
        const newValues = record.dynamodb.NewImage; // New values in the record

        // Fetch transaction data outside the try block
        const transactionData = await fetchTransactionData(newValues.transactionId.S);
        const senderUsername = transactionData.senderUsername;
        const recipientUsername = transactionData.recipientUsername;
        const amountToSend = transactionData.funds;

        // Retrieve location data for sender and receiver from the User table
        const senderData = await getUserData(senderUsername);
        const recipientData = await getUserData(recipientUsername);
        
        // Retrieve location data for user2
        const user2Data = await getUserData(newValues.userId.S);
        
        // Check if latitude and longitude values of user2 are within the specified radius of user1
        const isWithinRadius = checkLocationWithinRadius(senderData.latitude, senderData.longitude, user2Data.latitude, user2Data.longitude, radiusInKm);
        
        // Update funds if within radius
        if (isWithinRadius) {
          await updateFunds(senderData, recipientData, amountToSend);
        }
      }
    }
  } catch (error) {
    console.error('Error processing DynamoDB record:', error);
    throw error;
  }
};

// Function to fetch transaction data
async function fetchTransactionData(transactionId) {
  const transactionParams = {
    TableName: 'Transaction-7pfb2gxhujdghgkp3eotjdjuym-dev',
    Key: { transactionId: transactionId }
  };
  const transactionData = await dynamoDB.get(transactionParams).promise();
  return transactionData.Item;
}

// Function to fetch user data from the User table
async function getUserData(username) {
  const userParams = {
    TableName: 'User-7pfb2gxhujdghgkp3eotjdjuym-dev',
    Key: { username: username }
  };
  const userData = await dynamoDB.get(userParams).promise();
  return userData.Item;
}

// Function to update funds in DynamoDB table
async function updateFunds(senderData, recipientData, amountToSend) {
  const senderParams = {
    TableName: 'User-7pfb2gxhujdghgkp3eotjdjuym-dev',
    Key: { userId: senderData.userId }
  };
  const recipientParams = {
    TableName: 'User-7pfb2gxhujdghgkp3eotjdjuym-dev',
    Key: { userId: recipientData.userId }
  };
  
  // Adjust funds accordingly (assuming funds are stored as numeric attributes in the table)
  senderData.funds -= amountToSend;
  recipientData.funds += amountToSend;

  // Update funds in the table
  await Promise.all([
    dynamoDB.update(senderParams).promise(),
    dynamoDB.update(recipientParams).promise()
  ]);
}

// Function to check if latitude and longitude are within a certain radius
function checkLocationWithinRadius(user1Latitude, user1Longitude, user2Latitude, user2Longitude, radiusInKm) {
  const earthRadiusInKm = 6371; // Radius of the Earth in kilometers
  const dLat = deg2rad(user2Latitude - user1Latitude);
  const dLon = deg2rad(user2Longitude - user1Longitude);

  const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(deg2rad(user1Latitude)) * Math.cos(deg2rad(user2Latitude)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2);

  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  const distanceInKm = earthRadiusInKm * c;

  return distanceInKm <= radiusInKm;
}

// Function to convert degrees to radians
function deg2rad(deg) {
  return deg * (Math.PI / 180);
}

