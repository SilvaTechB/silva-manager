#!/usr/bin/env bash
set -e

export ANDROID_HOME=/home/runner/workspace/android-sdk
export ANDROID_SDK_ROOT=/home/runner/workspace/android-sdk

# Use the available JDK 17 from nix store
if [ -d "/nix/store/7h8xkvyyz4sgxm61rj1s64ncml582qyv-zulu-ca-jdk-17.0.12" ]; then
    export JAVA_HOME=/nix/store/7h8xkvyyz4sgxm61rj1s64ncml582qyv-zulu-ca-jdk-17.0.12
else
    # Fallback to system java
    JAVA_BIN=$(which java 2>/dev/null)
    if [ -n "$JAVA_BIN" ]; then
        export JAVA_HOME=$(dirname $(dirname $(readlink -f $JAVA_BIN)))
    fi
fi
export PATH=$JAVA_HOME/bin:$PATH

# Use workspace for all temp files (sandbox restricts /tmp writes)
WORK_TMPDIR=/home/runner/workspace/.tmp
mkdir -p $WORK_TMPDIR

export JAVA_TOOL_OPTIONS="-XX:-UsePerfData -Xms128m -Xmx1536m -Djava.io.tmpdir=$WORK_TMPDIR -Dorg.sqlite.tmpdir=$WORK_TMPDIR"
export GRADLE_USER_HOME=/home/runner/workspace/.gradle-home
export TMPDIR=$WORK_TMPDIR
export TMP=$WORK_TMPDIR
export TEMP=$WORK_TMPDIR

APP_VERSION="1.12.8"

echo "=== Silva Manager Build v$APP_VERSION ==="
echo "Android SDK: $ANDROID_HOME"
echo "Java: $(java -version 2>&1 | head -1)"
echo "Temp dir: $WORK_TMPDIR"
echo ""

./gradlew --no-daemon -Pversion=$APP_VERSION assembleDebug

# Rename APK to include version
APK_SRC=$(find app/build/outputs/apk/debug/ -name "*.apk" 2>/dev/null | head -1)
APK_DEST="app/build/outputs/apk/debug/silva-manager-${APP_VERSION}.apk"

if [ -n "$APK_SRC" ] && [ "$APK_SRC" != "$APK_DEST" ]; then
    mv "$APK_SRC" "$APK_DEST"
fi

echo ""
echo "=== Build complete! APK location: ==="
find app/build/outputs/apk/debug/ -name "*.apk" 2>/dev/null || echo "APK not found"
