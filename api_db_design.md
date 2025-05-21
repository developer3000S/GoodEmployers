# GoodEmployers - API and Database Design

## API Design

### Authentication API

#### 1. Register Client
- **Endpoint**: `POST /api/auth/register`
- **Description**: Register a new client device
- **Request Body**:
  ```json
  {
    "name": "string",
    "device_info": {
      "model": "string",
      "os_version": "string",
      "app_version": "string"
    }
  }
  ```
- **Response**:
  ```json
  {
    "client_id": "uuid",
    "access_token": "string",
    "refresh_token": "string",
    "expires_in": "integer"
  }
  ```
- **Status Codes**:
  - 201: Created
  - 400: Bad Request
  - 409: Conflict (if client already exists)

#### 2. Login Client
- **Endpoint**: `POST /api/auth/login`
- **Description**: Authenticate existing client
- **Request Body**:
  ```json
  {
    "client_id": "uuid"
  }
  ```
- **Response**:
  ```json
  {
    "access_token": "string",
    "refresh_token": "string",
    "expires_in": "integer"
  }
  ```
- **Status Codes**:
  - 200: OK
  - 401: Unauthorized
  - 404: Not Found

#### 3. Refresh Token
- **Endpoint**: `POST /api/auth/refresh`
- **Description**: Refresh authentication token
- **Request Body**:
  ```json
  {
    "refresh_token": "string"
  }
  ```
- **Response**:
  ```json
  {
    "access_token": "string",
    "refresh_token": "string",
    "expires_in": "integer"
  }
  ```
- **Status Codes**:
  - 200: OK
  - 401: Unauthorized

### Location API

#### 1. Submit Location
- **Endpoint**: `POST /api/locations`
- **Description**: Send GPS coordinates to server
- **Authentication**: JWT Bearer Token
- **Request Body**:
  ```json
  {
    "latitude": "float",
    "longitude": "float",
    "accuracy": "float",
    "altitude": "float (optional)",
    "speed": "float (optional)",
    "timestamp": "string (ISO 8601)"
  }
  ```
- **Response**:
  ```json
  {
    "id": "integer",
    "received_at": "string (ISO 8601)"
  }
  ```
- **Status Codes**:
  - 201: Created
  - 400: Bad Request
  - 401: Unauthorized

#### 2. Submit Batch Locations
- **Endpoint**: `POST /api/locations/batch`
- **Description**: Send multiple GPS coordinates in a single request
- **Authentication**: JWT Bearer Token
- **Request Body**:
  ```json
  {
    "locations": [
      {
        "latitude": "float",
        "longitude": "float",
        "accuracy": "float",
        "altitude": "float (optional)",
        "speed": "float (optional)",
        "timestamp": "string (ISO 8601)"
      }
    ]
  }
  ```
- **Response**:
  ```json
  {
    "received_count": "integer",
    "received_at": "string (ISO 8601)"
  }
  ```
- **Status Codes**:
  - 201: Created
  - 400: Bad Request
  - 401: Unauthorized

#### 3. Get Client Locations
- **Endpoint**: `GET /api/locations/{client_id}`
- **Description**: Retrieve locations for specific client
- **Authentication**: JWT Bearer Token
- **Query Parameters**:
  - `start_time`: ISO 8601 timestamp
  - `end_time`: ISO 8601 timestamp
  - `limit`: integer (default: 100)
  - `offset`: integer (default: 0)
- **Response**:
  ```json
  {
    "total_count": "integer",
    "locations": [
      {
        "id": "integer",
        "latitude": "float",
        "longitude": "float",
        "accuracy": "float",
        "altitude": "float (optional)",
        "speed": "float (optional)",
        "timestamp": "string (ISO 8601)",
        "created_at": "string (ISO 8601)"
      }
    ]
  }
  ```
- **Status Codes**:
  - 200: OK
  - 401: Unauthorized
  - 403: Forbidden
  - 404: Not Found

#### 4. Get Latest Location
- **Endpoint**: `GET /api/locations/{client_id}/latest`
- **Description**: Get latest location for client
- **Authentication**: JWT Bearer Token
- **Response**:
  ```json
  {
    "id": "integer",
    "latitude": "float",
    "longitude": "float",
    "accuracy": "float",
    "altitude": "float (optional)",
    "speed": "float (optional)",
    "timestamp": "string (ISO 8601)",
    "created_at": "string (ISO 8601)"
  }
  ```
- **Status Codes**:
  - 200: OK
  - 401: Unauthorized
  - 403: Forbidden
  - 404: Not Found

### Client Management API

#### 1. List Clients
- **Endpoint**: `GET /api/clients`
- **Description**: List all registered clients
- **Authentication**: JWT Bearer Token (Admin)
- **Query Parameters**:
  - `active_only`: boolean (default: true)
  - `limit`: integer (default: 100)
  - `offset`: integer (default: 0)
- **Response**:
  ```json
  {
    "total_count": "integer",
    "clients": [
      {
        "id": "uuid",
        "name": "string",
        "created_at": "string (ISO 8601)",
        "last_active": "string (ISO 8601)",
        "is_active": "boolean"
      }
    ]
  }
  ```
- **Status Codes**:
  - 200: OK
  - 401: Unauthorized
  - 403: Forbidden

#### 2. Get Client Details
- **Endpoint**: `GET /api/clients/{client_id}`
- **Description**: Get client details
- **Authentication**: JWT Bearer Token
- **Response**:
  ```json
  {
    "id": "uuid",
    "name": "string",
    "device_info": {
      "model": "string",
      "os_version": "string",
      "app_version": "string"
    },
    "created_at": "string (ISO 8601)",
    "last_active": "string (ISO 8601)",
    "is_active": "boolean",
    "location_count": "integer"
  }
  ```
- **Status Codes**:
  - 200: OK
  - 401: Unauthorized
  - 403: Forbidden
  - 404: Not Found

#### 3. Update Client
- **Endpoint**: `PUT /api/clients/{client_id}`
- **Description**: Update client information
- **Authentication**: JWT Bearer Token
- **Request Body**:
  ```json
  {
    "name": "string",
    "device_info": {
      "model": "string",
      "os_version": "string",
      "app_version": "string"
    }
  }
  ```
- **Response**:
  ```json
  {
    "id": "uuid",
    "name": "string",
    "updated_at": "string (ISO 8601)"
  }
  ```
- **Status Codes**:
  - 200: OK
  - 400: Bad Request
  - 401: Unauthorized
  - 403: Forbidden
  - 404: Not Found

#### 4. Deactivate Client
- **Endpoint**: `DELETE /api/clients/{client_id}`
- **Description**: Deactivate client (soft delete)
- **Authentication**: JWT Bearer Token
- **Response**:
  ```json
  {
    "id": "uuid",
    "deactivated_at": "string (ISO 8601)"
  }
  ```
- **Status Codes**:
  - 200: OK
  - 401: Unauthorized
  - 403: Forbidden
  - 404: Not Found

### Route Visualization API

#### 1. Get Route Data
- **Endpoint**: `GET /api/routes/{client_id}`
- **Description**: Get processed route data for visualization
- **Authentication**: JWT Bearer Token
- **Query Parameters**:
  - `start_time`: ISO 8601 timestamp
  - `end_time`: ISO 8601 timestamp
  - `simplify`: boolean (default: true)
  - `format`: string (default: "geojson", options: "geojson", "json")
- **Response (GeoJSON)**:
  ```json
  {
    "type": "FeatureCollection",
    "features": [
      {
        "type": "Feature",
        "geometry": {
          "type": "LineString",
          "coordinates": [
            [longitude, latitude],
            [longitude, latitude]
          ]
        },
        "properties": {
          "client_id": "uuid",
          "start_time": "string (ISO 8601)",
          "end_time": "string (ISO 8601)",
          "point_count": "integer"
        }
      }
    ]
  }
  ```
- **Status Codes**:
  - 200: OK
  - 401: Unauthorized
  - 403: Forbidden
  - 404: Not Found

#### 2. Web Map Interface
- **Endpoint**: `GET /web/map`
- **Description**: Web interface for map visualization
- **Authentication**: Optional JWT Bearer Token
- **Query Parameters**:
  - `client_id`: uuid (optional)
  - `start_time`: ISO 8601 timestamp (optional)
  - `end_time`: ISO 8601 timestamp (optional)
- **Response**: HTML page with map visualization
- **Status Codes**:
  - 200: OK
  - 401: Unauthorized (if authentication required)

### Logging API

#### 1. Get Client Logs
- **Endpoint**: `GET /api/logs/{client_id}`
- **Description**: Get logs for specific client
- **Authentication**: JWT Bearer Token (Admin)
- **Query Parameters**:
  - `start_time`: ISO 8601 timestamp
  - `end_time`: ISO 8601 timestamp
  - `action`: string (filter by action type)
  - `limit`: integer (default: 100)
  - `offset`: integer (default: 0)
- **Response**:
  ```json
  {
    "total_count": "integer",
    "logs": [
      {
        "id": "integer",
        "client_id": "uuid",
        "action": "string",
        "details": "object",
        "timestamp": "string (ISO 8601)"
      }
    ]
  }
  ```
- **Status Codes**:
  - 200: OK
  - 401: Unauthorized
  - 403: Forbidden
  - 404: Not Found

## Database Schema

### 1. Clients Table

```sql
CREATE TABLE clients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    device_info JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    last_active TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    is_active BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_clients_last_active ON clients(last_active);
CREATE INDEX idx_clients_is_active ON clients(is_active);
```

### 2. Locations Table

```sql
-- Enable PostGIS extension for geographic data types
CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE locations (
    id BIGSERIAL PRIMARY KEY,
    client_id UUID NOT NULL REFERENCES clients(id),
    coordinates GEOGRAPHY(POINT, 4326) NOT NULL,
    accuracy FLOAT NOT NULL,
    altitude FLOAT,
    speed FLOAT,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    CONSTRAINT fk_client
        FOREIGN KEY(client_id)
        REFERENCES clients(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_locations_client_id ON locations(client_id);
CREATE INDEX idx_locations_timestamp ON locations(timestamp);
CREATE INDEX idx_locations_client_timestamp ON locations(client_id, timestamp);
CREATE INDEX idx_locations_coordinates ON locations USING GIST(coordinates);
```

### 3. Client Logs Table

```sql
CREATE TABLE client_logs (
    id BIGSERIAL PRIMARY KEY,
    client_id UUID NOT NULL REFERENCES clients(id),
    action VARCHAR(50) NOT NULL,
    details JSONB,
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    CONSTRAINT fk_client
        FOREIGN KEY(client_id)
        REFERENCES clients(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_client_logs_client_id ON client_logs(client_id);
CREATE INDEX idx_client_logs_action ON client_logs(action);
CREATE INDEX idx_client_logs_timestamp ON client_logs(timestamp);
CREATE INDEX idx_client_logs_client_timestamp ON client_logs(client_id, timestamp);
```

### 4. Authentication Table

```sql
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    client_id UUID NOT NULL REFERENCES clients(id),
    token VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    CONSTRAINT fk_client
        FOREIGN KEY(client_id)
        REFERENCES clients(id)
        ON DELETE CASCADE
);

CREATE UNIQUE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_client_id ON refresh_tokens(client_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);
```

## Database Functions and Triggers

### 1. Update Last Active Timestamp

```sql
CREATE OR REPLACE FUNCTION update_client_last_active()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE clients
    SET last_active = NOW()
    WHERE id = NEW.client_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_client_last_active
AFTER INSERT ON locations
FOR EACH ROW
EXECUTE FUNCTION update_client_last_active();
```

### 2. Log Client Actions

```sql
CREATE OR REPLACE FUNCTION log_client_action(
    p_client_id UUID,
    p_action VARCHAR,
    p_details JSONB
)
RETURNS VOID AS $$
BEGIN
    INSERT INTO client_logs (client_id, action, details)
    VALUES (p_client_id, p_action, p_details);
END;
$$ LANGUAGE plpgsql;
```

### 3. Clean Expired Tokens

```sql
CREATE OR REPLACE FUNCTION clean_expired_tokens()
RETURNS VOID AS $$
BEGIN
    DELETE FROM refresh_tokens
    WHERE expires_at < NOW();
END;
$$ LANGUAGE plpgsql;
```
