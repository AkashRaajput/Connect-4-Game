@echo off
setlocal
cd /d "%~dp0.."

echo Building Connect 4...
call mvn clean package
if errorlevel 1 (
    echo Build failed.
    exit /b 1
)

echo.
echo Build complete.
echo   App JAR:  target\connect-4-game-1.0.0.jar
echo   Dist:     target\dist\
echo   Runtime:  target\connect4-runtime\  (after mvn javafx:jlink)
echo   Release:  target\connect-4-game-1.0.0-windows.zip
