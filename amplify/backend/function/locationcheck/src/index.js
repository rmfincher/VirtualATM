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
        
        // Retrieve location data for user1
        const user1Params = {
          TableName: 'User-7pfb2gxhujdghgkp3eotjdjuym-dev',
          Key: { userId: newValues.userId.S } // Assuming userId is stored as a string
        };
        const user1Data = await dynamoDB.get(user1Params).promise();
        const user1Location = user1Data.Item; // Location data for user1
        
        // Retrieve location data for user2
        const user2Params = {
          TableName: 'User-7pfb2gxhujdghgkp3eotjdjuym-dev',
          Key: { userId: newValues.userId.S } // Assuming userId is stored as a string
        };
        const user2Data = await dynamoDB.get(user2Params).promise();
        const user2Location = user2Data.Item; // Location data for user2
        
        // Check if latitude and longitude values of user2 are within the specified radius of user1
        const isWithinRadius = checkLocationWithinRadius(user1Location.latitude, user1Location.longitude, user2Location.latitude, user2Location.longitude, radiusInKm);
        
        // Return boolean result indicating whether funds should be updated or not
        return { shouldUpdateFunds: isWithinRadius };
      }
    }
    
    // Default to not updating funds if no MODIFY event found
    return { shouldUpdateFunds: false };
  } catch (error) {
    console.error('Error processing DynamoDB record:', error);
    throw error;
  }
};

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

