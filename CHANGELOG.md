# Changelog

## v0.1.0 — Initial Release (Feb 10, 2026)

First playable build of **FightofRngRage**, a 2D fighting game built with LibGDX.

### Title Screen
- Custom title screen with a light blue (100, 100, 200) background
- "You will regret playing this." displayed at the top in DM Serif Display font (90px, black)
- "Click here to begin ur suffering :)" at the bottom in red (50px) — tap/click to enter the game
- Tap detection is precise to the text bounding box only

### Game Arena
- 800x480 virtual world with `OrthographicCamera` and `FitViewport` for consistent scaling across devices
- **Player 1** (red sphere) starts on the left side
- **Player 2** (blue sphere) starts on the right side

### Backgrounds
- Three procedurally generated backgrounds, randomly selected each match:
  - **Green Hills** — blue sky, layered rolling hills, flat grass ground
  - **Desert Flatland** — warm sky gradient, sand dunes, cacti silhouettes, sun
  - **Valley Dip** — distant purple mountains, green slopes on both sides, valley floor with grass tufts

### Movement & Physics
- Gravity system — players fall and land on the ground
- Jumping with upward velocity, gravity pulls back down
- Horizontal movement with proportional joystick control (smooth diagonal jumps at any angle)
- Screen boundary clamping (players can't leave the arena)
- Players auto-face each other

### Controls
- **Desktop (Keyboard)**:
  - Player 1: A/D to move, W to jump
  - Player 2: Arrow keys to move, Up to jump
- **Android (Touch)**:
  - Two large circular virtual joysticks (semi-transparent)
  - P1 joystick on bottom-left, P2 joystick on bottom-right
  - Tilt to move, push up to jump
  - Fixed base position, thumb snaps back to center on release

### Collision
- Circle-circle collision between players — they push each other apart and cannot overlap

### HUD
- P1 red health bar (top-left) and P2 blue health bar (top-right)
- Health bars are large (780px wide, 72px tall) with dark backgrounds and white borders
- "P1" and "P2" labels in DM Serif Display (54px)

### Platforms
- Desktop (LWJGL3) — `./gradlew lwjgl3:run`
- Android — `./gradlew android:installDebug`
