# Connect 4 Game

A two-player Connect Four desktop game built with **Java 17** and **JavaFX 21**.

## Requirements

| Purpose | Requirement |
|---------|-------------|
| Build from source | JDK 17+, Apache Maven 3.9+ |
| Run portable ZIP | JDK 17+ |
| Run Windows installer | None (bundled runtime) |

```bash
java -version
mvn -version
```

## Quick start

### Build

```bash
mvn clean package
```

Or use the helper script:

```bat
scripts\build.bat
```

### Run (development)

```bash
mvn clean javafx:run
```

### Run (after packaging)

**Standard distribution (recommended for portable use)**

```bat
target\dist\run.bat
```

**Fat JAR**

```bat
target\dist\run-fat.bat
```

## Project structure

```
Connect-4-Game/
├── pom.xml
├── scripts/
│   ├── build.bat / build.sh       # mvn clean package
│   ├── jpackage.bat / jpackage.sh # native installer
│   └── create-icon.ps1            # PNG → ICO for Windows
├── src/main/java/com/akash/connectfour/
│   ├── Main.java
│   ├── Controller.java
│   └── ConnectFourBoard.java
├── src/main/resources/com/akash/connectfour/
│   ├── game.fxml
│   └── icon/
│       ├── app-icon.png           # window icon
│       └── app-icon.ico           # Windows installer icon
└── DEPLOYMENT.md                  # GitHub Releases guide
```

## Build outputs

| File | Description |
|------|-------------|
| `target/connect-4-game-1.0.0.jar` | Application JAR (classes + FXML + icon) |
| `target/connect-4-game-1.0.0-fat.jar` | Fat JAR (all Java dependencies merged) |
| `target/dist/` | Runnable folder with launchers and `lib/` |
| `target/connect-4-game-1.0.0-windows.zip` | Portable release ZIP |
| `dist-native/` | Windows `.exe` installer (after jpackage) |

## Commands reference

### Build

```bash
mvn clean package
```

### Run — portable distribution

```powershell
target\dist\run.bat
```

### Run — fat JAR

```powershell
target\dist\run-fat.bat
```

### Run — manual (module path)

```powershell
java --module-path "target/dist/lib" `
  --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base `
  -cp "target/connect-4-game-1.0.0.jar" com.akash.connectfour.Main
```

### Windows EXE installer

```bat
scripts\jpackage.bat
```

Or manually:

```bat
powershell -File scripts\create-icon.ps1

jpackage ^
  --name Connect4 ^
  --app-version 1.0.0 ^
  --vendor "Akash Raajput" ^
  --input target\dist ^
  --dest dist-native ^
  --main-jar connect-4-game-1.0.0.jar ^
  --main-class com.akash.connectfour.Main ^
  --module-path target\dist\lib ^
  --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base ^
  --icon src\main\resources\com\akash\connectfour\icon\app-icon.ico ^
  --type exe ^
  --win-menu ^
  --win-shortcut ^
  --win-dir-chooser
```

Output: `dist-native\Connect4-1.0.0.exe` (with WiX installed) or `dist-native\Connect4\Connect4.exe` (portable app-image fallback).

> **WiX Toolset** is required for a full Windows setup `.exe`. Without it, the script builds a portable app-image instead. Install WiX: `winget install WiXToolset.WiXToolset`

> Do **not** run the standard JAR with `java -jar` alone. JavaFX requires `--module-path` and `--add-modules`, or use the provided launcher scripts / Windows installer.

## Installation for end users

### Option A — Windows installer (recommended)

1. Download `Connect4-1.0.0.exe` from [GitHub Releases](https://github.com/AkashRaajput/Connect-4-Game/releases).
2. Run the installer.
3. Launch **Connect4** from the Start Menu.

No Java installation required.

### Option B — Portable ZIP

1. Download `connect-4-game-1.0.0-windows.zip`.
2. Extract the archive.
3. Run `run.bat` (Windows) or `run.sh` (Linux/macOS).

Requires **JDK 17+** installed.

## How to play

- **Player One** (red) goes first, then **Player Two** (yellow).
- Click a column to drop a disc.
- Connect four discs in a row — horizontal, vertical, or diagonal — to win.
- Click **New Game** to reset the board.

## Publishing releases

See [DEPLOYMENT.md](DEPLOYMENT.md) for the full GitHub Releases workflow.

## Troubleshooting

| Issue | Fix |
|-------|-----|
| `FXML resource not found` | Run `mvn clean package`; confirm `game.fxml` is under `src/main/resources/com/akash/connectfour/` |
| `java` / `mvn` not found | Install JDK 17+ and Maven; add them to `PATH` |
| JavaFX errors at runtime | Use `run.bat`, not `java -jar` |
| Fat JAR won't start | Use `run-fat.bat` or the standard `run.bat` / Windows installer |
| jpackage icon error | Run `powershell -File scripts\create-icon.ps1` |
| Linux/macOS ZIP from Windows build | Rebuild on the target OS (platform-specific JavaFX natives) |

## License

Provided as-is for learning and personal use.
