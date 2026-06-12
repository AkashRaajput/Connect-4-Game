#!/usr/bin/env sh
set -e
cd "$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"

echo "Building Connect 4..."
mvn clean package

echo
echo "Build complete."
echo "  App JAR:  target/connect-4-game-1.0.0.jar"
echo "  Fat JAR:  target/connect-4-game-1.0.0-fat.jar"
echo "  Dist:     target/dist/"
echo "  Release:  target/connect-4-game-1.0.0-windows.zip"
