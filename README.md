# CSE Chess

A simple chess game written in Kotlin Multiplatform Compose.

## Instructions for Build and Use

Steps to build and/or run the software:

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:

- on macOS/Linux
    ```shell
    ./gradlew :composeApp:assembleDebug
    ```
- on Windows
    ```shell
    .\gradlew.bat :composeApp:assembleDebug
    ```

### Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:

- on macOS/Linux
    ```shell
    ./gradlew :composeApp:run
    ```
- on Windows
    ```shell
    .\gradlew.bat :composeApp:run
    ```

### Build and Run Web Application

To build and run the development version of the web app, use the run configuration from the run widget
in your IDE's toolbar or run it directly from the terminal:

- for the Wasm target (faster, modern browsers):
    - on macOS/Linux
        ```shell
        ./gradlew :composeApp:wasmJsBrowserDevelopmentRun
        ```
    - on Windows
        ```shell
        .\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun
        ```
- for the JS target (slower, supports older browsers):
    - on macOS/Linux
        ```shell
        ./gradlew :composeApp:jsBrowserDevelopmentRun
        ```
    - on Windows
        ```shell
        .\gradlew.bat :composeApp:jsBrowserDevelopmentRun
        ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

Instructions for using the software:

1. Run the software in whatever way you prefer.
2. Start playing chess!

## Development Environment

To recreate the development environment, you need the following software and/or libraries with the specified versions:

- Android Studio
- JDK ~21

## Useful Websites to Learn More

I found these websites useful in developing this software:

- [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
- [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
- [@PhilippLackner](https://www.youtube.com/@PhilippLackner/playlists)

## Future Work

The following items I plan to fix, improve, and/or add to this project in the future:

- [ ] AI Bot
- [ ] Play online with friends
- [ ] Ranking system
