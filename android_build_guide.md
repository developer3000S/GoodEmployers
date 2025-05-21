# GoodEmployers Android Client - Build and Installation Guide

This comprehensive guide will walk you through the process of building and installing the GoodEmployers Android client application.

## Prerequisites

Before you begin, ensure you have the following installed:

- **Android Studio** (latest version recommended)
- **JDK 11** or higher
- **Git** (optional, for cloning the repository)
- **Android device or emulator** with Android 8.0 (API level 26) or higher

## Project Structure

The Android client follows the MVVM architecture pattern and uses Jetpack Compose for the UI. Here's an overview of the key components:

```
app/
├── src/main/
│   ├── java/com/goodemployers/app/
│   │   ├── data/
│   │   │   ├── api/           # API service interfaces
│   │   │   ├── model/         # Data models
│   │   │   └── repository/    # Data repositories
│   │   ├── di/                # Dependency injection
│   │   ├── service/           # Background services
│   │   ├── ui/
│   │   │   ├── navigation/    # Navigation components
│   │   │   ├── screens/       # UI screens
│   │   │   ├── theme/         # UI theme
│   │   │   └── viewmodel/     # ViewModels
│   │   └── util/              # Utility classes
│   └── res/                   # Resources
└── build.gradle               # App-level build configuration
```

## Building the APK

### Step 1: Clone or Download the Project

If you have the project files, extract them to a directory of your choice. If you're using Git:

```bash
git clone [repository-url] GoodEmployers
cd GoodEmployers/client
```

### Step 2: Open the Project in Android Studio

1. Launch Android Studio
2. Select "Open an existing Android Studio project"
3. Navigate to the `GoodEmployers/client` directory and click "Open"
4. Wait for the project to sync and index

### Step 3: Configure the Server URL

The client is configured to connect to the temporary server URL. If you need to change it:

1. Open `app/src/main/java/com/goodemployers/app/util/Constants.kt`
2. Update the `BASE_URL` constant:

```kotlin
const val BASE_URL = "https://8000-ia6lw3bxw9bcimqwscreo-6fe44999.manusvm.computer/"
```

### Step 4: Build the Debug APK

For quick testing:

1. Select `Build > Build Bundle(s) / APK(s) > Build APK(s)` from the menu
2. Wait for the build to complete
3. Click on the notification that appears or navigate to:
   `app/build/outputs/apk/debug/app-debug.apk`

### Step 5: Build a Release APK (Optional)

For a production-ready APK:

1. Select `Build > Generate Signed Bundle / APK` from the menu
2. Select "APK" and click "Next"
3. Create a new keystore or use an existing one:
   - To create a new keystore, click "Create new..."
   - Fill in the required fields (Key store path, passwords, key details)
   - Click "OK"
4. Select the destination folder and build type (release)
5. Click "Finish" to generate the signed APK

The signed APK will be available at the location you specified.

## Installing the APK

### Method 1: Direct Installation from Android Studio

1. Connect your Android device to your computer via USB
2. Enable USB debugging on your device:
   - Go to `Settings > About phone`
   - Tap "Build number" seven times to enable developer options
   - Go to `Settings > System > Developer options`
   - Enable "USB debugging"
3. In Android Studio, select your device from the dropdown menu in the toolbar
4. Click the "Run" button (green triangle)

### Method 2: Manual Installation

1. Transfer the APK file to your Android device (via USB, email, cloud storage, etc.)
2. On your device, navigate to the APK file using a file manager
3. Tap the APK file to start installation
4. If prompted about installing from unknown sources:
   - Go to Settings > Security
   - Enable "Unknown sources" or follow the prompts to allow installation
5. Complete the installation process

## Required Permissions

The app will request the following permissions:

- **Location** (Fine and Background): Required for GPS tracking
- **Internet**: Required for sending data to the server
- **Foreground Service**: Required for background location updates

Grant these permissions when prompted for the app to function correctly.

## Using the Application

### First Launch and Registration

1. Launch the GoodEmployers app
2. On first launch, you'll be prompted to register:
   - Enter your name
   - Tap "Register"
3. The app will request location permissions:
   - Grant "Allow all the time" for background tracking
   - If you only select "Allow while using the app," background tracking won't work

### Main Interface

The main screen displays:

- Your client information
- Location tracking status
- Last update time
- A button to manually send cached locations

### Background Operation

The app will:

- Collect GPS coordinates every 60 seconds
- Send them to the server automatically
- Continue tracking even when the app is closed
- Cache locations when offline and send them when connectivity is restored

### Viewing Your Routes

To view your location history and routes:

1. Open a web browser
2. Navigate to: `https://8000-ia6lw3bxw9bcimqwscreo-6fe44999.manusvm.computer/web/map`
3. Your routes will be displayed on an interactive map

## Troubleshooting

### Location Updates Not Working

1. Check that location permissions are set to "Allow all the time"
2. Ensure GPS is enabled on your device
3. Verify that battery optimization is disabled for the app:
   - Go to `Settings > Apps > GoodEmployers > Battery`
   - Select "Don't optimize" or "Unrestricted"

### Connection Issues

1. Verify that your device has internet connectivity
2. Check that the server URL is correct in Constants.kt
3. Try manually sending cached locations using the button on the home screen

### App Crashes on Launch

1. Ensure you're using a compatible Android version (8.0+)
2. Check that all permissions are granted
3. Try uninstalling and reinstalling the app

## Advanced Configuration

### Adjusting Location Update Frequency

To change how often the app collects GPS coordinates:

1. Open `app/src/main/java/com/goodemployers/app/util/Constants.kt`
2. Modify the following constants:
   ```kotlin
   const val LOCATION_UPDATE_INTERVAL = 60000L // 60 seconds in milliseconds
   const val LOCATION_FASTEST_INTERVAL = 30000L // 30 seconds
   const val LOCATION_DISPLACEMENT = 10f // 10 meters
   ```
3. Rebuild the APK

### Battery Optimization

For better battery life with slightly less accurate tracking:

1. Increase the update interval
2. Increase the displacement threshold
3. Consider using a lower accuracy setting in the LocationRequest

## Support and Feedback

If you encounter any issues or have suggestions for improvement, please contact the development team or submit an issue through the project repository.

---

This guide should help you successfully build, install, and use the GoodEmployers Android client application. The app is designed to work seamlessly with the server to provide reliable GPS tracking and route visualization.
