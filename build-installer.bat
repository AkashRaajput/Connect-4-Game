@echo off
setlocal EnableExtensions
cd /d "%~dp0"

rem Modular JavaFX app: jpackage MUST use --module (not --main-jar).
rem The jlink runtime already contains module com.akash.connectfour + JavaFX.

set APP_NAME=Connect4
set APP_VERSION=1.0.0
set VENDOR=Akash Raajput
set MODULE_LAUNCHER=com.akash.connectfour/com.akash.connectfour.Launcher
set RUNTIME_DIR=target\connect4-runtime
set OUTPUT_DIR=installer
set ICON=src\main\resources\com\akash\connectfour\icon\app-icon.ico
set "WIX_BIN=C:\Program Files (x86)\WiX Toolset v3.14\bin"

echo [1/4] Building modular application JAR...
call mvn clean package
if errorlevel 1 (
    echo Maven build failed.
    exit /b 1
)

echo [2/4] Creating custom runtime with JavaFX (jlink)...
call mvn javafx:jlink
if errorlevel 1 (
    echo jlink failed.
    exit /b 1
)

if not exist "%RUNTIME_DIR%\bin\java.exe" (
    echo Runtime image not found: %RUNTIME_DIR%
    exit /b 1
)

if not exist "%ICON%" (
    echo Creating Windows icon...
    powershell -NoProfile -ExecutionPolicy Bypass -File "scripts\create-icon.ps1"
    if errorlevel 1 exit /b 1
)

where jpackage >nul 2>&1
if errorlevel 1 goto :no_jpackage

if exist "%WIX_BIN%\light.exe" set "PATH=%WIX_BIN%;%PATH%"
if not exist "%WIX_BIN%\light.exe" goto :no_wix

echo [3/4] Building Windows installer with jpackage...
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"
if exist "%OUTPUT_DIR%\%APP_NAME%-%APP_VERSION%.exe" (
    del /f /q "%OUTPUT_DIR%\%APP_NAME%-%APP_VERSION%.exe" >nul 2>&1
    if exist "%OUTPUT_DIR%\%APP_NAME%-%APP_VERSION%.exe" (
        echo Previous installer is in use; renaming to .old ...
        ren "%OUTPUT_DIR%\%APP_NAME%-%APP_VERSION%.exe" "%APP_NAME%-%APP_VERSION%.exe.old"
    )
)

jpackage ^
  --type exe ^
  --name "%APP_NAME%" ^
  --app-version "%APP_VERSION%" ^
  --vendor "%VENDOR%" ^
  --description "Connect Four desktop game" ^
  --dest "%OUTPUT_DIR%" ^
  --runtime-image "%RUNTIME_DIR%" ^
  --module "%MODULE_LAUNCHER%" ^
  --icon "%ICON%" ^
  --win-menu ^
  --win-shortcut ^
  --win-dir-chooser

if errorlevel 1 (
    echo jpackage failed.
    exit /b 1
)

echo [4/4] Verifying jlink runtime launcher...
if exist "%RUNTIME_DIR%\bin\connect4.bat" (
    echo Runtime launcher: %RUNTIME_DIR%\bin\connect4.bat
) else (
    echo Warning: connect4.bat not found in runtime image.
)

echo.
echo Build complete.
echo   Installer: %OUTPUT_DIR%\%APP_NAME%-%APP_VERSION%.exe
echo   Runtime:   %RUNTIME_DIR%\bin\connect4.bat
dir /b "%OUTPUT_DIR%\*.exe"
goto :eof

:no_jpackage
echo jpackage not found. Install JDK 17+ and add it to PATH.
exit /b 1

:no_wix
echo WiX not found. Install WiX Toolset: winget install WiXToolset.WiXToolset
exit /b 1
