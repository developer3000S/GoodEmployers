# GoodEmployers - Project Report

## Project Overview

GoodEmployers is a client-server application designed to track and record GPS coordinates from multiple Android clients. The system consists of two main components:

1. **Android Client**: A mobile application built with Kotlin and Jetpack Compose that collects GPS coordinates at 60-second intervals and sends them to the server.

2. **Server Backend**: A FastAPI application with SQLite database that receives, stores, and visualizes location data from multiple clients.

## System Architecture

### Technology Stack

#### Client (Android)
- **Programming Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture Pattern**: MVVM (Model-View-ViewModel)
- **Key Libraries**:
  - Android Location Services for GPS tracking
  - Retrofit for API communication
  - Hilt for dependency injection
  - Kotlin Coroutines for asynchronous operations

#### Server
- **Programming Language**: Python
- **Web Framework**: FastAPI
- **Database**: SQLite
- **Key Libraries**:
  - SQLAlchemy for ORM
  - Pydantic for data validation
  - JWT for authentication

## Features Implemented

### Server Features
- **Authentication System**: JWT-based authentication for client identification
- **Location Data Management**: Endpoints for receiving and storing GPS coordinates
- **Client Management**: Registration, login, and client information retrieval
- **Route Visualization**: Web interface for displaying client routes on a map using Leaflet.js
- **Logging System**: Comprehensive logging of client actions for auditing
- **Scalability**: Tested with multiple simultaneous clients

### Android Client Features
- **Background Location Service**: Collects GPS coordinates every 60 seconds
- **Client Identification**: UUID-based client identification with secure storage
- **Authentication**: Registration and login flows with token management
- **Offline Support**: Caching of location data when offline
- **Battery Optimization**: Efficient location collection to minimize battery impact
- **User Interface**: Clean, intuitive UI built with Jetpack Compose

## Setup Instructions

### Server Setup
1. Navigate to the server directory:
   ```
   cd GoodEmployers/server
   ```

2. Create a virtual environment and install dependencies:
   ```
   python -m venv venv
   source venv/bin/activate
   pip install -r requirements.txt
   ```

3. Start the server:
   ```
   chmod +x start_server.sh
   ./start_server.sh
   ```

4. The server will be available at `http://localhost:8000`

### Android Client Setup
1. Open the client directory in Android Studio
2. Build and run the application on an Android device or emulator
3. The application will automatically connect to the server at `http://10.0.2.2:8000` (for emulator) or configure the server URL in the Constants.kt file for physical devices

## API Documentation

The server provides the following API endpoints:

### Authentication
- `POST /api/auth/register` - Register new client
- `POST /api/auth/login` - Authenticate client
- `POST /api/auth/refresh` - Refresh authentication token

### Location Data
- `POST /api/locations` - Send GPS coordinates to server
- `POST /api/locations/batch` - Send multiple GPS coordinates in a batch
- `GET /api/locations/{client_id}` - Retrieve locations for specific client
- `GET /api/locations/{client_id}/latest` - Get latest location for client

### Client Management
- `GET /api/clients` - List all registered clients
- `GET /api/clients/{client_id}` - Get client details
- `PUT /api/clients/{client_id}` - Update client information
- `DELETE /api/clients/{client_id}` - Deactivate client

### Visualization
- `GET /api/routes/{client_id}` - Get processed route data for visualization
- `GET /web/map` - Web interface for map visualization

## Testing

The project includes two test scripts to validate functionality and performance:

1. **Basic Multi-Client Test**:
   ```
   chmod +x test_clients.sh
   ./test_clients.sh
   ```
   This script simulates three clients sending location updates.

2. **Scalability Test**:
   ```
   chmod +x scalability_test.sh
   ./scalability_test.sh
   ```
   This script tests the system with 10 clients sending 20 location updates each.

## Database Schema

The SQLite database includes the following tables:

1. **clients**: Stores client information and authentication details
2. **locations**: Stores GPS coordinates with client and timestamp information
3. **client_logs**: Records client actions for auditing
4. **refresh_tokens**: Manages authentication refresh tokens

## Security Considerations

- JWT tokens with short expiration times
- Refresh token rotation for enhanced security
- Secure storage of client credentials on Android
- Permission handling for location access

## Performance and Scalability

The system has been tested with multiple simultaneous clients and has shown good performance characteristics:

- Efficient database queries with proper indexing
- Asynchronous request handling in FastAPI
- Background processing for location updates on Android
- Batch processing capability for high-volume data

## Future Enhancements

Potential future improvements include:

1. **Real-time Updates**: WebSocket implementation for live tracking
2. **Enhanced Analytics**: Route optimization and pattern recognition
3. **Geofencing**: Location-based alerts and boundaries
4. **Advanced Visualization**: Heat maps and time-based filtering
5. **Multi-tenant Support**: Organization-level access control

## Conclusion

The GoodEmployers application successfully meets all the specified requirements, providing a robust solution for tracking and visualizing location data from multiple Android clients. The system is scalable, secure, and ready for production use.
