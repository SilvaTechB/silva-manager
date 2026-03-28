#!/usr/bin/env bash
set -e

export ANDROID_HOME=/home/runner/workspace/android-sdk
export ANDROID_SDK_ROOT=/home/runner/workspace/android-sdk
export JAVA_HOME=/nix/store/7h8xkvyyz4sgxm61rj1s64ncml582qyv-zulu-ca-jdk-17.0.12
export PATH=$JAVA_HOME/bin:$PATH

# Use workspace for all temp files (sandbox restricts /tmp writes)
WORK_TMPDIR=/home/runner/workspace/.tmp
mkdir -p $WORK_TMPDIR

export JAVA_TOOL_OPTIONS="-XX:-UsePerfData -Xms256m -Xmx2g -Djava.io.tmpdir=$WORK_TMPDIR -Dorg.sqlite.tmpdir=$WORK_TMPDIR"
export GRADLE_USER_HOME=/home/runner/workspace/.gradle-home
export TMPDIR=$WORK_TMPDIR
export TMP=$WORK_TMPDIR
export TEMP=$WORK_TMPDIR

echo "=== Morphe Manager Build ==="
echo "Android SDK: $ANDROID_HOME"
echo "Java: $(java -version 2>&1 | head -1)"
echo "Temp dir: $WORK_TMPDIR"
echo ""

./gradlew --no-daemon assembleDebug

echo ""
echo "=== Build complete! APK location: ==="
find app/build/outputs/apk/debug/ -name "*.apk" 2>/dev/null || echo "APK not found"
