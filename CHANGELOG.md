# Changelog

All notable changes to the Isomo Mod will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### ✨ Added

- **Left-Click Block Deletion** - Delete blocks using the same patterns as placement!
  - **Left-click functionality**: Delete blocks at the direct hit position (not adjacent)
  - **Pattern-based deletion**: Remove blocks according to current build pattern shape
  - **Smart validation**: Prevents deletion of unbreakable blocks like bedrock
  - **Consistent behavior**: Same rotation and pattern logic as placement
  - **Drop handling**: Deleted blocks drop as items (in survival mode)
- **Configurable Pattern Sizes** - Change pattern sizes dynamically without hardcoding!
  - **`/buildmode size wall [width] [height]`**: Configure wall pattern dimensions (1-50 blocks)
  - **`/buildmode size floor [width] [depth]`**: Configure floor pattern dimensions (1-50 blocks)
  - **`/buildmode size pillar [height]`**: Configure pillar height (1-50 blocks)
  - **`/buildmode size line [length]`**: Configure line length (1-50 blocks)
  - **Dynamic Pattern Names**: Pattern names update to show current size (e.g., "Wall 7x4", "Floor 10x8")
  - **Instant Changes**: Size changes take effect immediately without restart
- **Pattern Rotation System** - Rotate build patterns by 90° using the 'R' key!
  - **'R' Key Binding**: Rotate current pattern clockwise (0° → 90° → 180° → 270° → back to 0°)
  - **Smart Rotation Logic**: Automatically applies appropriate rotation to each pattern type
  - **Visual Feedback**: Shows pattern name and rotation angle in action bar
  - **Command Support**: `/buildmode rotation [0-3]` to set specific rotation angles
  - **Persistent Rotation State**: Rotation is maintained when switching between patterns
- **Runtime Configuration System** - Change wireframe colors and settings without restarting!
  - `/buildmode color [preset]` command to change to predefined colors (blue, green, yellow, purple, cyan, white)
  - `/buildmode color [r] [g] [b] [a]` command to set custom RGBA wireframe colors
  - `/buildmode reach [distance]` command to adjust preview reach distance (1-10 blocks)
  - `/buildmode info` command to display current configuration settings
  - All changes take effect immediately without client restart
- **Extended Block Placement** - Place blocks at the same distance as wireframe previews!
  - **BlockPlacementHandler** enables placing blocks beyond vanilla reach distance
  - Automatic pattern-based placement - place entire patterns with one click
  - Matches configurable reach distance from BuildModeConfig
- **Mouse Wheel Pattern Switching** - Scroll to cycle through build patterns when build mode is active
  - Scroll up for next pattern, scroll down for previous pattern
  - Prevents normal hotbar scrolling when build mode is active
  - Optional pattern name display in action bar
- **BuildModeConfig** singleton class for managing runtime settings
- **BuildModeCommand** class providing comprehensive command system
- Auto-completion support for color preset commands
- **Build Mode System** - Press 'B' to toggle wireframe preview mode for faster, more accurate building
  - Toggle build mode on/off with 'B' key (configurable in controls)
  - Real-time wireframe previews follow cursor position (default: blue wireframes)
  - Multiple build patterns: Single Block, 3x3 Wall, 5x5 Floor, 5-block Pillar, 5-block Line
  - Client-side only rendering with minimal performance impact
  - 5-block reach distance for preview placement
  - No confirmation dialogs - instant visual feedback for building assistance
- **BuildModeManager** singleton class for state management
- **WireframeRenderer** for efficient OpenGL line rendering
- **BuildPattern** interface with 5 predefined implementations
- **BuildPatterns** utility class for pattern navigation
- Comprehensive Javadoc documentation throughout codebase
- Built using Forge's event system with proper client-side architecture

### 🔄 Changed

- Wireframe colors are now dynamically loaded from configuration instead of static constants
- WireframeRenderer now supports runtime color changes
- Updated project structure to include `config/` and `command/` packages
- Migrated from changelog.txt to modern CHANGELOG.md format

### 🐛 Fixed

- Fixed RegisterClientCommandsEvent registration error by moving to Forge event bus instead of Mod event bus
- Fixed ClientEventHandler to use configurable reach distance from BuildModeConfig instead of hardcoded constant

### 📚 Documentation

- Updated README.md with new commands and configuration options
- Enhanced Javadoc documentation for all new classes
- Added command usage examples and configuration instructions
- Complete README.md with installation and usage instructions
- Comprehensive code documentation following Java conventions
- CLAUDE.md with development guidelines and code style requirements

---

## Legend

- ✨ **Added** for new features
- 🔄 **Changed** for changes in existing functionality
- 🗑️ **Deprecated** for soon-to-be removed features
- ❌ **Removed** for now removed features
- 🐛 **Fixed** for any bug fixes
- 🔒 **Security** for vulnerability fixes
- 📚 **Documentation** for documentation changes
- ⚡ **Performance** for performance improvements

## Links

- [Keep a Changelog](https://keepachangelog.com/)
- [Semantic Versioning](https://semver.org/)
- [Minecraft Forge Documentation](https://docs.minecraftforge.net/)
