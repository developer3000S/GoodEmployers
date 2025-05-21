# Java Environment Setup for Android Development

## Important Note

The GoodEmployers Android client requires a properly configured Java Development Kit (JDK) to build successfully. During our testing, we identified that a Java environment is required but not available in the current sandbox environment.

## Requirements

To build the Android client, you will need:

1. **Java Development Kit (JDK) 17** or newer
   - The project is configured to use Java 17 compatibility
   - Download from: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)

2. **Android Studio**
   - Latest stable version recommended
   - Download from: [Android Developer website](https://developer.android.com/studio)

3. **Android SDK**
   - Android SDK Platform 34 (Android 14)
   - Android Build Tools 34.0.0 or newer
   - These can be installed through Android Studio's SDK Manager

## Setting Up JAVA_HOME

Before building the project, ensure your JAVA_HOME environment variable is properly set:

### On Windows:
```
set JAVA_HOME=C:\Program Files\Java\jdk-17
```

### On macOS/Linux:
```
export JAVA_HOME=/path/to/jdk-17
```

## Verifying Your Setup

To verify your Java setup is correct, run:
```
java -version
```

This should display Java version 17 or newer.

## Building the Project

Once your environment is properly set up, you can build the project using:
```
./gradlew build
```

Or open the project in Android Studio and use the built-in build tools.
