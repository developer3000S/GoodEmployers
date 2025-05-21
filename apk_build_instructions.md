# Android APK Build Instructions

This document provides instructions for building the GoodEmployers Android client APK.

## Prerequisites

- Android Studio (latest version)
- JDK 11 or higher
- Gradle 7.0+
- Android SDK with API level 33 (Android 13)

## Building the APK

1. Open the project in Android Studio:
   ```
   File > Open > Select the GoodEmployers/client directory
   ```

2. Update the server URL in Constants.kt:
   ```kotlin
   // Already updated to point to the deployed server
   const val BASE_URL = "https://8000-ia6lw3bxw9bcimqwscreo-6fe44999.manusvm.computer/"
   ```

3. Build the APK:
   ```
   Build > Build Bundle(s) / APK(s) > Build APK(s)
   ```

4. The APK will be generated at:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

5. For a release build:
   ```
   Build > Generate Signed Bundle / APK > APK > Create new keystore
   ```

## Installing on Android Device

1. Enable "Install from Unknown Sources" in your device settings
2. Transfer the APK to your device
3. Tap on the APK file to install
4. Grant the required permissions when prompted

## Required Permissions

- Location (Fine and Background)
- Internet
- Network State

## Testing the Application

1. Register a new account on first launch
2. The app will automatically start tracking location in the background
3. View your location history on the web interface at:
   ```
   https://8000-ia6lw3bxw9bcimqwscreo-6fe44999.manusvm.computer/web/map
   ```

## Troubleshooting

- If location updates aren't working, check that location permissions are granted
- Ensure the device has an active internet connection
- Verify the server is accessible from the device
