# Isomo Mod

A Minecraft Forge mod for Minecraft 1.20.1 that provides a comprehensive toolbox of building functionality with an intuitive build mode system.

## Features

### Build Mode System
- **Toggle Build Mode**: Press 'B' to activate/deactivate wireframe preview mode
- **Real-time Wireframes**: Green wireframe previews follow your cursor for precise building
- **Multiple Build Patterns**: Choose from 5 different building patterns:
  - **Single Block**: Precise single-block placement
  - **Wall 3x3**: 3x3 vertical wall pattern
  - **Floor 5x5**: 5x5 horizontal floor pattern  
  - **Pillar 5H**: 5-block high vertical pillar
  - **Line H5**: 5-block horizontal line
- **Pattern Switching**: Cycle through patterns seamlessly
- **Extended Reach**: 5-block reach distance for preview placement
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
- **Mouse Wheel**: Switch between build patterns (when in build mode)

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
├── event/             # Event handling system
└── IsomoMod.java      # Main mod class
```

### Contributing
This mod follows standard Java coding conventions with comprehensive documentation. All public methods include Javadoc comments, and the changelog is maintained for all releases.

## Version History

See [changelog.txt](changelog.txt) for detailed version history and feature additions.

## License

This mod is provided as-is for educational and entertainment purposes.
