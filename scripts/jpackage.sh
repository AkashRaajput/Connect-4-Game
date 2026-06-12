#!/usr/bin/env sh
set -e
ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
cd "$ROOT"

APP_NAME="Connect4"
APP_VERSION="1.0.0"
VENDOR="Akash Raajput"
MAIN_JAR="connect-4-game-1.0.0.jar"
MAIN_CLASS="com.akash.connectfour.Main"
INPUT_DIR="target/dist"
OUTPUT_DIR="dist-native"
ICON="src/main/resources/com/akash/connectfour/icon/app-icon.png"
MODULES="javafx.controls,javafx.fxml,javafx.graphics,javafx.base"

if [ ! -f "$INPUT_DIR/$MAIN_JAR" ]; then
  echo "Distribution not found. Running build first..."
  sh scripts/build.sh
fi

if ! command -v jpackage >/dev/null 2>&1; then
  echo "jpackage not found. Install JDK 17+ and ensure bin is on PATH."
  exit 1
fi

PKG_TYPE="app-image"
if [ "$(uname -s)" = "Linux" ]; then
  PKG_TYPE="deb"
elif [ "$(uname -s)" = "Darwin" ]; then
  PKG_TYPE="dmg"
fi

rm -rf "$OUTPUT_DIR"
mkdir -p "$OUTPUT_DIR"

echo "Creating native package with jpackage..."
jpackage \
  --name "$APP_NAME" \
  --app-version "$APP_VERSION" \
  --vendor "$VENDOR" \
  --description "Connect Four desktop game" \
  --input "$INPUT_DIR" \
  --dest "$OUTPUT_DIR" \
  --main-jar "$MAIN_JAR" \
  --main-class "$MAIN_CLASS" \
  --module-path "$INPUT_DIR/lib" \
  --add-modules "$MODULES" \
  --icon "$ICON" \
  --type "$PKG_TYPE"

echo
echo "Native package created in $OUTPUT_DIR/"
ls -la "$OUTPUT_DIR"
