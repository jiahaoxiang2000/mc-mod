# Changelog

All notable changes to the Isomo Mod will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### ✨ Added

- **Runtime Configuration System** - Change wireframe colors and settings without restarting!
  - `/buildmode color [preset]` command to change to predefined colors (blue, green, yellow, purple, cyan, white)
  - `/buildmode color [r] [g] [b] [a]` command to set custom RGBA wireframe colors
  - `/buildmode reach [distance]` command to adjust preview reach distance (1-10 blocks)
  - `/buildmode info` command to display current configuration settings
  - All changes take effect immediately without client restart
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
