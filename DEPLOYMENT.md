# GitHub Releases Deployment Guide

This guide explains how to publish Connect 4 Game releases on GitHub.

## Prerequisites

- JDK 17+ with `java`, `jpackage`, and `jar` on your `PATH`
- Apache Maven 3.9+
- Windows build machine (for the Windows `.exe` installer)
- Git tag matching the version in `pom.xml` (currently `1.0.0`)

## Release checklist

1. Update version in `pom.xml` if needed (`<version>1.0.0</version>`).
2. Build all artifacts.
3. Smoke-test the packaged app.
4. Create and push a Git tag.
5. Upload release assets to GitHub.
6. Publish release notes.

## Step 1 — Build artifacts

From the project root:

```bat
scripts\build.bat
```

Or manually:

```bash
mvn clean package
```

Generated outputs:

| Artifact | Path | Audience |
|----------|------|----------|
| Portable ZIP | `target/connect-4-game-1.0.0-windows.zip` | Users with JDK 17+ installed |
| Distribution folder | `target/dist/` | Manual testing |
| Fat JAR | `target/connect-4-game-1.0.0-fat.jar` | Advanced users / scripting |
| App JAR | `target/connect-4-game-1.0.0.jar` | Used by launchers and jpackage |

## Step 2 — Build Windows installer (recommended for end users)

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
  --description "Connect Four desktop game" ^
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

Output: `dist-native\Connect4-1.0.0.exe` (exact filename may vary by jpackage version).

> **WiX required for setup installer:** `--type exe` needs [WiX Toolset 3+](https://wixtoolset.org/) on your PATH (`light.exe`, `candle.exe`). Install with `winget install WiXToolset.WiXToolset`. Without WiX, `scripts\jpackage.bat` automatically falls back to `--type app-image`, producing a portable `dist-native\Connect4\Connect4.exe` with bundled JRE.

### Smoke test before publishing

1. Run `target\dist\run.bat` — game window opens, FXML loads, icon appears in title bar.
2. Install `dist-native\Connect4-1.0.0.exe` — launches from Start Menu without a separate Java install.
3. Play one full game — win/draw dialogs appear, **New Game** resets the board.

## Step 3 — Tag the release

```bash
git add .
git commit -m "Release v1.0.0"
git tag v1.0.0
git push origin main
git push origin v1.0.0
```

Use semantic versioning: `vMAJOR.MINOR.PATCH`.

## Step 4 — Create a GitHub Release

### Using GitHub CLI

```bash
gh release create v1.0.0 ^
  dist-native/Connect4-1.0.0.exe ^
  target/connect-4-game-1.0.0-windows.zip ^
  --title "Connect 4 v1.0.0" ^
  --notes-file RELEASE_NOTES.md
```

### Using the GitHub web UI

1. Open **Releases → Draft a new release**.
2. Choose tag `v1.0.0`.
3. Title: `Connect 4 v1.0.0`.
4. Upload these files:
   - `dist-native/Connect4-1.0.0.exe` — **recommended for Windows users**
   - `target/connect-4-game-1.0.0-windows.zip` — portable bundle (requires JDK 17+)
5. Publish the release.

## Suggested release notes template

```markdown
## Connect 4 v1.0.0

Two-player Connect Four desktop game built with JavaFX.

### Downloads

- **Windows installer (recommended):** `Connect4-1.0.0.exe` — includes bundled Java runtime
- **Portable ZIP:** `connect-4-game-1.0.0-windows.zip` — requires JDK 17+ on the target machine

### Installation

#### Windows installer
1. Download `Connect4-1.0.0.exe`.
2. Run the installer and follow the prompts.
3. Launch **Connect4** from the Start Menu.

#### Portable ZIP
1. Download and extract `connect-4-game-1.0.0-windows.zip`.
2. Run `run.bat` (Windows) or `run.sh` (Linux/macOS).
3. Requires JDK 17+ installed and on your PATH.

### Requirements

| Package | Java required? |
|---------|----------------|
| `.exe` installer | No (JRE bundled) |
| Portable ZIP | Yes — JDK 17+ |

### How to play

- Player One (red) goes first.
- Click a column to drop a disc.
- Connect four in a row to win.
- Click **New Game** to reset.
```

## CI/CD (optional)

Example GitHub Actions workflow outline:

```yaml
name: Release
on:
  push:
    tags: ['v*']
jobs:
  build-windows:
    runs-on: windows-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'
      - uses: actions/setup-java@v4  # or apache/maven action
      - run: choco install maven -y  # or use maven wrapper
      - run: scripts\build.bat
      - run: scripts\jpackage.bat
      - uses: softprops/action-gh-release@v2
        with:
          files: |
            dist-native/*.exe
            target/connect-4-game-*-windows.zip
```

## Versioning policy

- **Patch** (`1.0.x`): bug fixes, packaging tweaks
- **Minor** (`1.x.0`): new features, UI improvements
- **Major** (`x.0.0`): breaking changes

After bumping the version in `pom.xml`, rebuild all artifacts before tagging.

## Troubleshooting releases

| Problem | Solution |
|---------|----------|
| `jpackage` not found | Use full JDK 17+, not a JRE-only install |
| Icon rejected | Run `scripts\create-icon.ps1` to regenerate `app-icon.ico` |
| FXML not found after install | Rebuild with `mvn clean package`; verify `game.fxml` is inside the JAR |
| Fat JAR fails to start | Use `target\dist\run-fat.bat` or the Windows installer instead |
| ZIP built on Windows won't run on Linux | Rebuild on the target OS; JavaFX native libraries are platform-specific |
