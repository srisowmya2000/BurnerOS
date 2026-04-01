#!/usr/bin/env sh
##############################################################################
# Gradle wrapper script (lightweight copy). Make executable: `chmod +x gradlew`
# This script will delegate to the Gradle wrapper jar in gradle/wrapper/gradle-wrapper.jar.
##############################################################################

WRAPPER_DIR="${0%/*}/gradle/wrapper"
if [ -z "$WRAPPER_DIR" ]; then
  WRAPPER_DIR="gradle/wrapper"
fi

JAVA_CMD=java

if [ -z "$JAVA_HOME" ]; then
  JAVA_CMD=java
else
  JAVA_CMD="$JAVA_HOME/bin/java"
fi

JAR="$WRAPPER_DIR/gradle-wrapper.jar"

if [ ! -f "$JAR" ]; then
  echo "Warning: gradle-wrapper.jar not found at $JAR"
  echo "You should run 'gradle wrapper' on a machine with Gradle installed to generate the real wrapper jar, or download it from Gradle distribution." >&2
fi

exec "$JAVA_CMD" -jar "$JAR" "$@"
