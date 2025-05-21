#!/bin/bash

# Scalability test script for GoodEmployers server
# This script simulates a larger number of clients and higher request volume

# Server URL
SERVER_URL="http://localhost:8000"

# Number of simulated clients
NUM_CLIENTS=10

# Number of location updates per client
UPDATES_PER_CLIENT=20

# Delay between updates (in seconds)
UPDATE_DELAY=2

# Function to register a new client
register_client() {
    local client_num=$1
    local name="ScaleClient${client_num}"
    local device_models=("Samsung Galaxy S21" "Google Pixel 6" "OnePlus 9" "iPhone 13" "Xiaomi Mi 11")
    local os_versions=("Android 12" "Android 13" "iOS 15" "Android 11")
    
    # Select random device model and OS version
    local device_model=${device_models[$((RANDOM % ${#device_models[@]}))]}
    local os_version=${os_versions[$((RANDOM % ${#os_versions[@]}))]}
    
    echo "Registering client: $name"
    
    response=$(curl -s -X POST "$SERVER_URL/api/auth/register" \
        -H "Content-Type: application/json" \
        -d "{
            \"name\": \"$name\",
            \"device_info\": {
                \"model\": \"$device_model\",
                \"os_version\": \"$os_version\",
                \"app_version\": \"1.0.0\"
            }
        }")
    
    # Extract client_id and access_token from response
    client_id=$(echo $response | grep -o '"client_id":"[^"]*' | cut -d'"' -f4)
    access_token=$(echo $response | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)
    
    echo "Client registered with ID: $client_id"
    
    echo "$client_id,$access_token" > "scale_client_${client_num}.txt"
}

# Function to send location update
send_location() {
    local client_num=$1
    local update_num=$2
    
    # Read client credentials
    IFS=',' read -r client_id access_token < "scale_client_${client_num}.txt"
    
    # Generate random location near San Francisco with some movement
    # Base coordinates: San Francisco (37.7749, -122.4194)
    local base_lat=37.7749
    local base_lng=-122.4194
    
    # Add some random movement (±0.01 degrees, roughly 1km)
    local lat=$(echo "$base_lat + ($RANDOM / 32767 * 0.02 - 0.01) + ($update_num * 0.0001)" | bc -l)
    local lng=$(echo "$base_lng + ($RANDOM / 32767 * 0.02 - 0.01) + ($update_num * 0.0001)" | bc -l)
    
    # Random accuracy between 5 and 20 meters
    local accuracy=$(echo "5 + ($RANDOM % 16)" | bc -l)
    
    echo "Sending location for client $client_num (update $update_num): $lat, $lng"
    
    timestamp=$(date -u +"%Y-%m-%dT%H:%M:%S.000Z")
    
    response=$(curl -s -X POST "$SERVER_URL/api/locations" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $access_token" \
        -d "{
            \"latitude\": $lat,
            \"longitude\": $lng,
            \"accuracy\": $accuracy,
            \"timestamp\": \"$timestamp\"
        }")
    
    # Check if response contains an error
    if [[ $response == *"error"* ]]; then
        echo "Error response: $response"
    else
        echo "Location update successful"
    fi
}

echo "Starting scalability test with $NUM_CLIENTS clients, $UPDATES_PER_CLIENT updates each"

# Register all clients
for ((i=1; i<=NUM_CLIENTS; i++)); do
    register_client $i
    sleep 1  # Small delay between registrations
done

echo "All clients registered. Starting location updates..."

# Send location updates for all clients
for ((update=1; update<=UPDATES_PER_CLIENT; update++)); do
    echo "===== Update round $update of $UPDATES_PER_CLIENT ====="
    
    for ((client=1; client<=NUM_CLIENTS; client++)); do
        send_location $client $update
        
        # Small random delay between client updates (0-1 seconds)
        sleep $(echo "scale=3; $RANDOM/32767" | bc)
    done
    
    echo "Completed update round $update"
    sleep $UPDATE_DELAY
done

echo "Scalability test completed"
echo "Total updates sent: $((NUM_CLIENTS * UPDATES_PER_CLIENT))"
