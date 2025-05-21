# GoodEmployers - System Architecture Document

## Project Overview
GoodEmployers is a client-server application designed to track and record GPS coordinates from multiple Android clients. The system consists of two main components: an Android client application and a server backend. The client application collects GPS coordinates at regular intervals and sends them to the server, which stores the data in a PostgreSQL database and provides APIs for data retrieval and visualization.

## Technology Stack

### Client (Android)
- **Programming Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture Pattern**: MVVM (Model-View-ViewModel)
- **Key Libraries**:
  - Android Location Services for GPS tracking
  - Retrofit for API communication
  - OkHttp for HTTP client
  - Kotlin Coroutines for asynchronous operations
  - Room (optional) for local caching
  - Dagger Hilt for dependency injection

### Server
- **Programming Language**: Python
- **Web Framework**: FastAPI
- **Database**: PostgreSQL
- **Key Libraries**:
  - SQLAlchemy for ORM
  - Pydantic for data validation
  - Alembic for database migrations
  - Uvicorn as ASGI server
  - GeoAlchemy2 for geographic data types
  - PyJWT for authentication

## System Architecture

### Client Architecture
1. **Location Service**:
   - Background service that collects GPS coordinates every 60 seconds
   - Handles Android location permissions
   - Manages battery optimization considerations

2. **Data Repository**:
   - Manages data flow between UI and remote API
   - Handles local caching if needed
   - Implements retry mechanisms for failed API calls

3. **Network Layer**:
   - Implements REST API client using Retrofit
   - Handles authentication and request/response interceptors
   - Manages connection states and error handling

4. **UI Layer**:
   - Implements MVVM pattern with Jetpack Compose
   - Provides user interface for authentication and status monitoring
   - Displays service status and connection information

### Server Architecture
1. **API Layer**:
   - FastAPI application with RESTful endpoints
   - JWT-based authentication system
   - Rate limiting and request validation
   - Swagger/OpenAPI documentation

2. **Service Layer**:
   - Business logic implementation
   - Coordinate processing and validation
   - Client management and logging services

3. **Data Access Layer**:
   - SQLAlchemy ORM models
   - Database connection management
   - Query optimization for geospatial data

4. **Web Interface**:
   - Map visualization using Leaflet.js
   - Route display and filtering options
   - Admin dashboard for system monitoring

## Data Flow

1. **GPS Data Collection**:
   - Android client collects GPS coordinates every 60 seconds
   - Coordinates are bundled with client identifier and timestamp
   - Data is queued for transmission to server

2. **Data Transmission**:
   - Client authenticates with server using JWT
   - GPS data is sent to server via REST API
   - Server acknowledges receipt of data

3. **Data Storage**:
   - Server validates incoming data
   - Coordinates are stored in PostgreSQL with PostGIS extension
   - Client actions are logged for auditing purposes

4. **Data Retrieval and Visualization**:
   - Web interface or API clients request route data
   - Server retrieves and processes coordinate data
   - Routes are displayed on interactive maps

## Database Schema

### Tables
1. **clients**:
   - id (UUID, PK)
   - name (VARCHAR)
   - created_at (TIMESTAMP)
   - last_active (TIMESTAMP)
   - is_active (BOOLEAN)

2. **locations**:
   - id (BIGSERIAL, PK)
   - client_id (UUID, FK to clients.id)
   - coordinates (GEOGRAPHY POINT)
   - accuracy (FLOAT)
   - altitude (FLOAT, nullable)
   - speed (FLOAT, nullable)
   - timestamp (TIMESTAMP)
   - created_at (TIMESTAMP)

3. **client_logs**:
   - id (BIGSERIAL, PK)
   - client_id (UUID, FK to clients.id)
   - action (VARCHAR)
   - details (JSONB)
   - timestamp (TIMESTAMP)

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new client
- `POST /api/auth/login` - Authenticate client and receive JWT
- `POST /api/auth/refresh` - Refresh authentication token

### Location Data
- `POST /api/locations` - Send GPS coordinates to server
- `GET /api/locations/{client_id}` - Retrieve locations for specific client
- `GET /api/locations/{client_id}/latest` - Get latest location for client
- `GET /api/locations/{client_id}/timerange` - Get locations within time range

### Client Management
- `GET /api/clients` - List all registered clients
- `GET /api/clients/{client_id}` - Get client details
- `PUT /api/clients/{client_id}` - Update client information
- `DELETE /api/clients/{client_id}` - Deactivate client

### Visualization
- `GET /api/routes/{client_id}` - Get processed route data for visualization
- `GET /web/map` - Web interface for map visualization

## Scalability Considerations

1. **Horizontal Scaling**:
   - Stateless API design allows for multiple server instances
   - Database connection pooling for efficient resource utilization
   - Load balancing across multiple server instances

2. **Database Optimization**:
   - Indexing on frequently queried fields (client_id, timestamp)
   - Partitioning of location data by time periods
   - Regular database maintenance and optimization

3. **Performance Considerations**:
   - Batch processing for high-volume data ingestion
   - Caching of frequently accessed routes
   - Asynchronous processing for non-critical operations

## Security Measures

1. **Authentication and Authorization**:
   - JWT-based authentication with short expiration times
   - Role-based access control for administrative functions
   - HTTPS for all communications

2. **Data Protection**:
   - Input validation and sanitization
   - Protection against SQL injection via ORM
   - Rate limiting to prevent abuse

3. **Privacy Considerations**:
   - Data retention policies
   - Client consent management
   - Data anonymization options

## Logging and Monitoring

1. **System Logging**:
   - Application logs for debugging and auditing
   - Performance metrics collection
   - Error tracking and alerting

2. **Client Action Logging**:
   - Tracking of client connections and disconnections
   - Recording of location submission events
   - Audit trail for administrative actions

## Testing Strategy

1. **Unit Testing**:
   - Testing individual components in isolation
   - Mocking external dependencies

2. **Integration Testing**:
   - Testing interactions between components
   - API endpoint testing with mock clients

3. **Load Testing**:
   - Simulating multiple simultaneous clients
   - Measuring system performance under load
   - Identifying bottlenecks and optimization opportunities

4. **End-to-End Testing**:
   - Testing complete workflows from client to server
   - Validating data integrity throughout the system

## Deployment Considerations

1. **Server Deployment**:
   - Containerization with Docker
   - Environment configuration via environment variables
   - Database migration strategy

2. **Client Deployment**:
   - Google Play Store distribution
   - CI/CD pipeline for automated builds
   - Version management and updates

## Future Enhancements

1. **Real-time Updates**:
   - WebSocket implementation for live tracking
   - Push notifications for important events

2. **Advanced Analytics**:
   - Route optimization suggestions
   - Pattern recognition in movement data
   - Geofencing capabilities

3. **Offline Support**:
   - Enhanced client-side caching
   - Synchronization when connectivity is restored
