#!/bin/bash

# Test script for simulating multiple clients connecting to the GoodEmployers server
# This script creates test data for three different clients and sends location updates

# Server URL
SERVER_URL="http://localhost:8000"

# Function to register a new client
register_client() {
    local name=$1
    local device_model=$2
    local os_version=$3
    local app_version=$4
    
    echo "Registering client: $name"
    
    response=$(curl -s -X POST "$SERVER_URL/api/auth/register" \
        -H "Content-Type: application/json" \
        -d "{
            \"name\": \"$name\",
            \"device_info\": {
                \"model\": \"$device_model\",
                \"os_version\": \"$os_version\",
                \"app_version\": \"$app_version\"
            }
        }")
    
    # Extract client_id and access_token from response
    client_id=$(echo $response | grep -o '"client_id":"[^"]*' | cut -d'"' -f4)
    access_token=$(echo $response | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)
    
    echo "Client registered with ID: $client_id"
    echo "Access token: $access_token"
    
    echo "$client_id,$access_token" > "client_${name}.txt"
}

# Function to send location update
send_location() {
    local client_name=$1
    local latitude=$2
    local longitude=$3
    local accuracy=$4
    
    # Read client credentials
    IFS=',' read -r client_id access_token < "client_${client_name}.txt"
    
    echo "Sending location for $client_name: $latitude, $longitude"
    
    timestamp=$(date -u +"%Y-%m-%dT%H:%M:%S.000Z")
    
    response=$(curl -s -X POST "$SERVER_URL/api/locations" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $access_token" \
        -d "{
            \"latitude\": $latitude,
            \"longitude\": $longitude,
            \"accuracy\": $accuracy,
            \"timestamp\": \"$timestamp\"
        }")
    
    echo "Response: $response"
}

# Register three test clients
register_client "TestClient1" "Samsung Galaxy S21" "Android 12" "1.0.0"
register_client "TestClient2" "Google Pixel 6" "Android 13" "1.0.0"
register_client "TestClient3" "OnePlus 9" "Android 12" "1.0.0"

# Wait for registration to complete
sleep 2

# Send initial locations for each client
send_location "TestClient1" "37.7749" "-122.4194" "10.0"  # San Francisco
send_location "TestClient2" "40.7128" "-74.0060" "15.0"   # New York
send_location "TestClient3" "34.0522" "-118.2437" "8.0"   # Los Angeles

# Wait between updates
sleep 5

# Send updated locations (simulating movement)
send_location "TestClient1" "37.7750" "-122.4195" "8.0"
send_location "TestClient2" "40.7130" "-74.0062" "12.0"
send_location "TestClient3" "34.0525" "-118.2439" "7.5"

# Wait between updates
sleep 5

# Send more updated locations
send_location "TestClient1" "37.7752" "-122.4198" "9.0"
send_location "TestClient2" "40.7135" "-74.0065" "11.0"
send_location "TestClient3" "34.0530" "-118.2445" "8.2"

echo "Test completed with three simultaneous clients"
