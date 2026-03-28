# Silva Manager (Morphe Manager)

Android APK patcher application built with Kotlin/Jetpack Compose.

## About

Silva Manager is an Android application based on URV and ReVanced Manager. It patches Android apps using the Morphe patching framework.

## Build System

- **Language**: Kotlin + Java (JVM target 17)
- **Build System**: Gradle 8.14 with Android Gradle Plugin 8.9.1
- **UI Framework**: Jetpack Compose with Material3
- **Min SDK**: 26, Target SDK: 35

## Environment Setup (Replit)

### Android SDK
The Android SDK is installed at `/home/runner/workspace/android-sdk/` (must be in workspace, not home, due to filesystem write restrictions).

Components installed (from Nix store zips):
- Android Platform 35 (`platforms/android-35`)
- Build Tools 35.0.1 (`build-tools/35.0.1`)
- Platform Tools (`platform-tools/`)
- NDK 27.0.12077973 (`ndk/27.0.12077973`) — r27, matches `ndkVersion` in `app/build.gradle.kts`
- NDK 27.2.12479018 (`ndk/27.2.12479018`) — r27c, also available
- CMake 3.22.1 (`cmake/3.22.1`) — required by the native build
- CMake 3.31.6 (`cmake/3.31.6`) — from Nix system cmake package

### Java
Uses Zulu JDK 17 from the Nix store:
`/nix/store/7h8xkvyyz4sgxm61rj1s64ncml582qyv-zulu-ca-jdk-17.0.12`

(Falls back to system OpenJDK 17 if Zulu not found)

### SDK Licenses
Licenses are accepted in `/home/runner/workspace/android-sdk/licenses/`:
- `android-sdk-license`
- `android-sdk-preview-license`
- `android-googletv-license`
- `google-gdk-license`
- `intel-android-extra-license`
- `mips-android-sysimage-license`

### Required Environment Variables (Secrets)
- `GITHUB_ACTOR` - GitHub username for Maven package authentication
- `GITHUB_TOKEN` - GitHub Personal Access Token with `read:packages` scope (needed for morphe-patcher and morphe-library)

### Gradle Configuration
- `GRADLE_USER_HOME=/home/runner/workspace/.gradle-home` (workspace location for cache)
- `JAVA_TOOL_OPTIONS="-XX:-UsePerfData -Xms128m -Xmx1536m -Djava.io.tmpdir=/home/runner/workspace/.tmp -Dorg.sqlite.tmpdir=/home/runner/workspace/.tmp"`
  - `-XX:-UsePerfData` prevents JVM SIGBUS crashes in the sandbox
  - `java.io.tmpdir` redirects temp files from restricted `/tmp` to workspace
  - `org.sqlite.tmpdir` redirects SQLite JDBC native lib extraction (Room KSP)
  - Max heap reduced to 1536m to avoid OOM kills

### local.properties
```
sdk.dir=/home/runner/workspace/android-sdk
```
(NDK version is set in `app/build.gradle.kts` as `ndkVersion = "27.0.12077973"`)

## Building

### Debug Build
```bash
bash build-debug.sh
```

The workflow "Build Debug APK" runs the debug build automatically.

Output APK: `app/build/outputs/apk/debug/silva-manager-{version}.apk`

**Note**: First build takes ~5 minutes to download dependencies. Subsequent builds use Gradle cache and take ~1 minute.

## Current Version: 1.12.5

### Recent Changes (v1.12.5)
- **Bug fix**: Corrected wrong repository reference (`SilvaApp/morphe-patches` → `SilvaTechB/silva-patches`) in the changelog URL derivation documentation in `SilvaAPI.kt`
- **UI**: Brand label in home screen greeting now uses a styled pill badge with accent background
- **UI**: App cards have deeper frosted glass gradients, stronger border contrast, taller card height (84dp)
- **UI**: "Other apps" button redesigned with glassmorphism gradient border and `Apps` icon
- **UI**: Bottom action bar buttons now show text labels below icons for better clarity

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
