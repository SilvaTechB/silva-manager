# Morphe Manager

Android APK patcher application built with Kotlin/Jetpack Compose.

## About

Morphe Manager is an Android application based on URV and ReVanced Manager. It patches Android apps using the Morphe patching framework.

## Build System

- **Language**: Kotlin + Java (JVM target 17)
- **Build System**: Gradle 8.14 with Android Gradle Plugin 8.9.1
- **UI Framework**: Jetpack Compose with Material3
- **Min SDK**: 26, Target SDK: 35

## Environment Setup (Replit)

Due to sandbox restrictions, specific configurations are required:

### Android SDK
The Android SDK is stored in `/home/runner/workspace/android-sdk/` (must be in workspace, not home, due to filesystem write restrictions).

Components installed:
- Android Platform 35
- Build Tools 35.0.1
- NDK 27.0.12077973 (folder contains NDK 27.2 binaries, source.properties set to 27.0)
- NDK 27.2.12479018 (actual binaries)
- CMake 3.22.1

### Java
Uses Zulu JDK 17 from the Nix store:
`/nix/store/7h8xkvyyz4sgxm61rj1s64ncml582qyv-zulu-ca-jdk-17.0.12`

### Required Environment Variables (Secrets)
- `GITHUB_ACTOR` - GitHub username for Maven package authentication
- `GITHUB_TOKEN` - GitHub Personal Access Token with `read:packages` scope (needed for morphe-patcher and morphe-library)

### Gradle Configuration
- `GRADLE_USER_HOME=/home/runner/workspace/.gradle-home` (workspace location for cache)
- `JAVA_TOOL_OPTIONS="-XX:-UsePerfData -Xms256m -Xmx2g -Djava.io.tmpdir=/home/runner/workspace/.tmp -Dorg.sqlite.tmpdir=/home/runner/workspace/.tmp"`
  - `-XX:-UsePerfData` prevents JVM SIGBUS crashes in the sandbox
  - `java.io.tmpdir` redirects temp files from restricted `/tmp` to workspace
  - `org.sqlite.tmpdir` redirects SQLite JDBC native lib extraction (Room KSP)

### local.properties
```
sdk.dir=/home/runner/workspace/android-sdk
```

## Building

### Debug Build
```bash
bash build-debug.sh
```

The workflow "Build Debug APK" runs the debug build automatically.

Output APK: `app/build/outputs/apk/debug/morphe-manager-{version}.apk`

## Dependencies

Key dependencies:
- `app.morphe:morphe-patcher` - from GitHub Packages (requires auth)
- `app.morphe:morphe-library` - from GitHub Packages (requires auth)
- Jetpack Compose BOM
- Firebase Cloud Messaging
- Koin (DI)
- Ktor (HTTP)
- Room (Database)
- Shizuku/LibSU (root/privileged access)
