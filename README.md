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

```bat
target\dist\run.bat
```

Or use the jlink runtime (after `mvn javafx:jlink`):

```bat
target\connect4-runtime\bin\connect4.bat
```

### Windows installer

```bat
build-installer.bat
```

## Modular JavaFX packaging

This project includes `src/main/java/module-info.java`, which declares the module `com.akash.connectfour`.

| Declaration | Purpose |
|-------------|---------|
| `requires javafx.*` | Declares JavaFX module dependencies at compile time |
| `opens ... to javafx.fxml` | Allows FXML to inject `@FXML` fields into `Controller` |
| `exports com.akash.connectfour` | Exposes application classes to the module system |

Because the app is modular:

- **`mvn javafx:jlink`** can build `target/connect4-runtime` with Java + JavaFX bundled
- **`jpackage --runtime-image target/connect4-runtime --module com.akash.connectfour/com.akash.connectfour.Launcher`** produces a working Windows installer
- **`java -jar`** alone will **not** work — use `run.bat`, `connect4.bat`, or the installer
- Fat JAR shading is **not** used (it breaks JavaFX native libraries)

## Project structure

```
Connect-4-Game/
├── pom.xml
├── scripts/
│   ├── build.bat / build.sh       # mvn clean package
│   ├── jpackage.bat / jpackage.sh # native installer
│   └── create-icon.ps1            # PNG → ICO for Windows
├── build-installer.bat            # package + jlink + jpackage
├── src/main/java/
│   ├── module-info.java           # Java module descriptor (required for jlink)
│   └── com/akash/connectfour/
│       ├── Launcher.java          # packaged entry point
│       ├── Main.java
│       ├── Controller.java
│       └── ConnectFourBoard.java
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
| `target/connect-4-game-1.0.0.jar` | Modular application JAR |
| `target/connect4-runtime/` | Custom JRE + JavaFX (after `mvn javafx:jlink`) |
| `target/dist/` | Portable folder with `run.bat` and `lib/` |
| `target/connect-4-game-1.0.0-windows.zip` | Portable release ZIP |
| `installer/Connect4-1.0.0.exe` | Windows setup installer (after `build-installer.bat`) |

## Commands reference

### Build

```bash
mvn clean package
```

### Build everything (installer)

```bat
build-installer.bat
```

### Run — portable distribution

```powershell
target\dist\run.bat
```

### Run — manual (modular)

```powershell
java --module-path "target/dist/lib;target/connect-4-game-1.0.0.jar" `
  --module com.akash.connectfour/com.akash.connectfour.Launcher
```

### Windows EXE installer

```bat
build-installer.bat
```

Or manually:

```bat
mvn clean package
mvn javafx:jlink

jpackage ^
  --type exe ^
  --name Connect4 ^
  --app-version 1.0.0 ^
  --vendor "Akash Raajput" ^
  --dest installer ^
  --runtime-image target\connect4-runtime ^
  --module com.akash.connectfour/com.akash.connectfour.Launcher ^
  --icon src\main\resources\com\akash\connectfour\icon\app-icon.ico ^
  --win-menu ^
  --win-shortcut ^
  --win-dir-chooser
```

Output: `installer\Connect4-1.0.0.exe`

> Requires **WiX Toolset** for `--type exe`. Install with: `winget install WiXToolset.WiXToolset`

> Do **not** use `java -jar` alone. This is a modular JavaFX app — use `run.bat`, `connect4.bat`, or the installer.

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
| JavaFX errors at runtime | Use `run.bat` or `connect4.bat`, not `java -jar` |
| `jlink requires a module descriptor` | Ensure `src/main/java/module-info.java` exists |
| jpackage icon error | Run `powershell -File scripts\create-icon.ps1` |
| Linux/macOS ZIP from Windows build | Rebuild on the target OS (platform-specific JavaFX natives) |

## License

Provided as-is for learning and personal use.
