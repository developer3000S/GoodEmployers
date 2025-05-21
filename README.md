# GoodEmployers

A client-server application for tracking GPS coordinates from multiple clients.

## Project Overview

GoodEmployers is a comprehensive system designed to track GPS coordinates from multiple clients. The system consists of:

1. **Android Client**: Built with Kotlin and Jetpack Compose
2. **Server Component**: Implemented with Python and FastAPI

## Features

- GPS coordinate tracking with 60-second frequency
- Client identification and authentication
- Data storage with SQLite
- Web interface for route visualization
- Comprehensive logging system
- Scalability for multiple clients

## Project Structure

```
GoodEmployers/
├── client/                  # Android client application
│   ├── app/                 # Main application module
│   ├── gradle/              # Gradle configuration
│   ├── build.gradle         # Root build configuration
│   ├── gradle.properties    # Gradle properties
│   ├── gradlew              # Gradle wrapper script
│   └── settings.gradle      # Gradle settings
├── server/                  # FastAPI server application
│   ├── src/                 # Source code
│   │   ├── main.py          # Main application entry point
│   │   ├── database.py      # Database configuration
│   │   ├── models/          # Data models
│   │   ├── routes/          # API routes
│   │   ├── services/        # Business logic
│   │   └── templates/       # Web templates
│   ├── requirements.txt     # Python dependencies
│   ├── start_server.sh      # Server startup script
│   ├── test_clients.sh      # Client testing script
│   └── scalability_test.sh  # Load testing script
├── clean_server/            # Simplified server version
├── architecture.md          # System architecture documentation
├── api_db_design.md         # API and database design
├── project_report.md        # Comprehensive project report
├── project_report_update.md # Latest project updates
├── java_environment_note.md # Java setup instructions
└── todo.md                  # Project task list
```

## Setup Instructions

### Server Setup

1. Navigate to the server directory:
   ```
   cd server
   ```

2. Install dependencies:
   ```
   pip install -r requirements.txt
   ```

3. Start the server:
   ```
   chmod +x start_server.sh
   ./start_server.sh
   ```

### Android Client Setup

The Android client requires a proper Java development environment:

1. Install JDK 17 or newer
2. Set up Android Studio with SDK Platform 34 and Build Tools 34.0.0+
3. Configure JAVA_HOME environment variable

For detailed instructions, see [Java Environment Setup](java_environment_note.md).

To build the Android client:
1. Open the client directory in Android Studio
2. Sync the project with Gradle files
3. Build the project using the Build menu or Gradle tasks

## Testing

The system includes scripts for testing:

- `test_clients.sh`: Simulates multiple clients
- `scalability_test.sh`: Tests system under load

## Documentation

- `architecture.md`: System architecture overview
- `api_db_design.md`: API endpoints and database schema
- `project_report.md`: Comprehensive project documentation
- `project_report_update.md`: Latest updates and improvements

## License

This project is provided as-is without any warranty. All rights reserved.
