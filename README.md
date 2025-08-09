# Isomo Mod

A Minecraft Forge mod for Minecraft 1.20.1 that provides a comprehensive toolbox of building functionality with an intuitive build mode system.

## Features

### Build Mode System
- **Toggle Build Mode**: Press 'B' to activate/deactivate wireframe preview mode
- **Real-time Wireframes**: Configurable colored wireframe previews follow your cursor for precise building
- **Pattern Rotation**: Press 'R' to rotate patterns by 90° (clockwise: 0° → 90° → 180° → 270°)
  - Rotation persists when switching between patterns
- **Extended Block Placement**: Place blocks at the same distance as wireframe previews (beyond vanilla reach)
- **Pattern-Based Placement**: Place entire patterns with a single right-click
- **Pattern-Based Deletion**: Delete blocks using the same patterns with left-click
- **Runtime Configuration**: Change colors and settings instantly without restarting the client
- **Multiple Build Patterns**: Choose from 5 different configurable building patterns:
  - **Single Block**: Precise single-block placement
  - **Wall**: Configurable vertical wall pattern (default 3x3, rotatable)
  - **Floor**: Configurable horizontal floor pattern (default 5x5)
  - **Pillar**: Configurable vertical pillar (default 5H)
  - **Line**: Configurable horizontal line (default 5H, rotatable: East-West ↔ North-South)
- **Dynamic Pattern Sizing**: All pattern sizes can be changed at runtime (1-50 blocks)
- **Pattern Switching**: Cycle through patterns with mouse wheel
- **Extended Reach**: Configurable reach distance (1-10 blocks) for both preview and placement
- **Client-side Only**: Minimal performance impact with client-side rendering

## Requirements

- **Minecraft**: 1.20.1
- **Forge**: 47.4.6 or higher
- **Java**: 17 or higher

## Installation

1. Download the latest release from the releases page
2. Place the `.jar` file in your `mods` folder
3. Launch Minecraft with Forge profile

## Controls

- **B Key**: Toggle build mode on/off (configurable in controls menu)
- **R Key**: Rotate current pattern by 90° clockwise (when in build mode)
- **Right-Click**: Place blocks according to current pattern (at adjacent position)
- **Left-Click**: Delete blocks according to current pattern (at direct hit position)
- **Mouse Wheel**: Switch between build patterns (when in build mode)

## Commands

- **`/buildmode color [preset]`**: Change wireframe color using presets (blue, green, yellow, purple, cyan, white)
- **`/buildmode color [r] [g] [b] [a]`**: Set custom RGBA wireframe color (values 0.0-1.0)
- **`/buildmode reach [distance]`**: Set preview reach distance (1-10 blocks)
- **`/buildmode rotation [0-3]`**: Set pattern rotation (0=0°, 1=90°, 2=180°, 3=270°)
- **`/buildmode info`**: Display current configuration settings

All configuration changes take effect immediately without requiring a client restart!

## Technical Details

### Architecture
- Built using Forge's event system with proper client-side architecture
- Singleton pattern for state management
- Comprehensive Javadoc documentation throughout codebase
- Event-driven design for optimal performance

### Build Patterns
All patterns are implemented through the `BuildPattern` interface and provide:
- Pattern name and description
- Block position calculations relative to center point
- Wireframe preview generation

## Development

### Project Structure
```
src/main/java/com/isomo/mod/
├── building/           # Build pattern definitions
├── client/             # Client-side managers and handlers  
├── command/           # Runtime configuration commands
├── config/            # Configuration management system
├── event/             # Event handling system
└── IsomoMod.java      # Main mod class
```

### Contributing
This mod follows standard Java coding conventions with comprehensive documentation. All public methods include Javadoc comments, and the changelog is maintained for all releases.

## Version History

See [CHANGELOG.md](CHANGELOG.md) for detailed version history and feature additions.

## License

This mod is provided as-is for educational and entertainment purposes.
