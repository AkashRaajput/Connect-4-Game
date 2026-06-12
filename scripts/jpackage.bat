@echo off
setlocal
cd /d "%~dp0.."

set APP_NAME=Connect4
set APP_VERSION=1.0.0
set VENDOR=Akash Raajput
set MAIN_JAR=connect-4-game-1.0.0.jar
set MODULE_LAUNCHER=com.akash.connectfour/com.akash.connectfour.Launcher
set INPUT_DIR=target\jpackage-input
set RUNTIME_DIR=target\connect4-runtime
set OUTPUT_DIR=installer
set ICON=src\main\resources\com\akash\connectfour\icon\app-icon.ico
set PKG_TYPE=%~1
set "WIX_BIN=C:\Program Files (x86)\WiX Toolset v3.14\bin"

if "%PKG_TYPE%"=="" set PKG_TYPE=exe

if not exist "%INPUT_DIR%\%MAIN_JAR%" (
    echo Distribution not found. Running build first...
    call "%~dp0build.bat"
    call mvn javafx:jlink
    if errorlevel 1 exit /b 1
)

if not exist "%RUNTIME_DIR%\bin\java.exe" (
    echo Runtime image missing. Running jlink...
    call mvn javafx:jlink
    if errorlevel 1 exit /b 1
)

if not exist "%ICON%" (
    echo Creating Windows icon from PNG...
    powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0create-icon.ps1"
    if errorlevel 1 exit /b 1
)

where jpackage >nul 2>&1
if errorlevel 1 (
    echo jpackage not found. Install JDK 17+ and ensure bin is on PATH.
    exit /b 1
)

where light.exe >nul 2>&1
if errorlevel 1 (
    if exist "%WIX_BIN%\light.exe" (
        set "PATH=%WIX_BIN%;%PATH%"
        echo Added WiX to PATH: %WIX_BIN%
    )
)

where light.exe >nul 2>&1
if errorlevel 1 (
    if /I "%PKG_TYPE%"=="exe" (
        echo WiX Toolset not found. Falling back to app-image.
        set PKG_TYPE=app-image
    )
)

set WIN_OPTS=
if /I "%PKG_TYPE%"=="exe" set WIN_OPTS=--win-menu --win-shortcut --win-dir-chooser
if /I "%PKG_TYPE%"=="msi" set WIN_OPTS=--win-menu --win-shortcut --win-dir-chooser

if exist "%OUTPUT_DIR%" rmdir /s /q "%OUTPUT_DIR%"
mkdir "%OUTPUT_DIR%"

echo Creating Windows package with jpackage ^(%PKG_TYPE%^)...
jpackage ^
  --name "%APP_NAME%" ^
  --app-version "%APP_VERSION%" ^
  --vendor "%VENDOR%" ^
  --description "Connect Four desktop game" ^
  --dest "%OUTPUT_DIR%" ^
  --runtime-image "%RUNTIME_DIR%" ^
  --module "%MODULE_LAUNCHER%" ^
  --icon "%ICON%" ^
  --type %PKG_TYPE% ^
  %WIN_OPTS%

if errorlevel 1 (
    echo jpackage failed.
    exit /b 1
)

echo.
echo Package created in %OUTPUT_DIR%\
dir /b "%OUTPUT_DIR%\*.exe" 2>nul
dir /s /b "%OUTPUT_DIR%\Connect4.exe" 2>nul
