# Changelog

All notable changes to the Isomo Mod will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Version Links

[Unreleased](https://github.com/jiahaoxiang2000/mc-mod/compare/v1.1.4...HEAD) [1.1.4](https://github.com/jiahaoxiang2000/mc-mod/compare/v1.1.3...v1.1.4) [1.1.3](https://github.com/jiahaoxiang2000/mc-mod/compare/v1.1.2...v1.1.3) [1.1.2](https://github.com/jiahaoxiang2000/mc-mod/compare/v1.1.1...v1.1.2) [1.1.1](https://github.com/jiahaoxiang2000/mc-mod/compare/v1.1.0...v1.1.1) [1.1.0](https://github.com/jiahaoxiang2000/mc-mod/compare/v1.0.0...v1.1.0) [1.0.0](https://github.com/jiahaoxiang2000/mc-mod/releases/tag/v1.0.0)

## [Unreleased]

### ✨ Added

- Future features will be listed here

### 🔄 Changed

- Future changes will be listed here

### 🐛 Fixed

- Future bug fixes will be listed here

### ⚡ Performance

- Future performance improvements will be listed here

---

## [1.1.4] - 2025-08-10

### 🐛 Fixed

- **ResourceLocation Compatibility**: Fixed NoSuchMethodError preventing mod from loading in Minecraft 1.20.1
  - **API Issue**: Reverted ResourceLocation.fromNamespaceAndPath() which doesn't exist in MC 1.20.1
  - **Solution**: Use deprecated ResourceLocation constructor for backward compatibility
  - **Impact**: Mod now loads properly without runtime errors

### 📚 Documentation

- **Version Management**: Added guidelines to CLAUDE.md for keeping git tags and gradle.properties in sync
- **Build Process**: Improved documentation for release management

---

## [1.1.3] - 2025-08-10

### 🔄 Changed

- **GitHub Actions Simplification**: Reverted workflow optimizations to use simpler, more reliable configuration
  - **JDK Version**: Reverted to JDK 17 for better compatibility
  - **Caching Strategy**: Reverted to standard Gradle caching approach
  - **Build Command**: Simplified to basic `./gradlew build` for reliability
  - **Focus on Stability**: Prioritized reliable builds over advanced optimizations

### ✨ Retained

- **Repository Optimization**: Kept conditional Chinese mirrors fix from v1.1.2 for faster CI builds

---

## [1.1.2] - 2025-08-10

### ⚡ Performance

- **Repository Optimization**: Fixed slow GitHub Actions builds caused by Chinese mirrors
  - **Conditional Repositories**: Chinese mirrors now only used in local development (when CI environment variable is not set)
  - **CI Performance**: GitHub Actions now uses default international repositories for faster builds
  - **Local Development**: Chinese developers still benefit from faster Aliyun mirrors locally

### 🐛 Fixed

- **CI/CD Build Speed**: Resolved extremely slow GitHub Actions builds caused by international access to Chinese Maven mirrors

---

## [1.1.1] - 2025-08-10

### ⚡ Performance

- **GitHub Actions Optimization**: Significantly improved CI/CD build performance
  - **Upgraded JDK**: Updated from JDK 17 to JDK 22 for better performance
  - **Enhanced Gradle Caching**: Replaced manual caching with official gradle/actions/setup-gradle@v3
  - **Parallel Builds**: Enabled --parallel, --build-cache, and --configuration-cache flags
  - **Optimized JVM Settings**: Configured 4GB heap and 4 workers for faster builds
  - **Security**: Added Gradle wrapper validation step
  - **Cache Management**: Improved cache cleanup for more reliable cache hits

### 🔄 Changed

- **CI/CD Pipeline**: Complete rebuild workflow optimization for faster releases

---

## [1.1.0] - 2025-08-10

### 🔄 Changed

- **Client-Server Architecture**: Completely redesigned block operations to use proper client-server communication
  - **Client-side changes**: BlockPlacementHandler now sends network packets instead of performing direct block operations
  - **Server-side validation**: All block placement and deletion operations are now validated and executed server-side
  - **Network protocol**: Added comprehensive packet system with BlockOperationPacket for client-server communication
  - **Multiplayer support**: Block operations now properly synchronize across all connected players
  - **Persistence**: Block changes now correctly save with the world and persist across server restarts
  - **Compatibility**: Integrated with Forge event system for proper mod compatibility and protection system support

### 🐛 Fixed

- **Block persistence**: Fixed blocks not saving properly - all block operations now persist correctly across world reloads
- **Multiplayer synchronization**: Fixed desync issues where block changes only appeared on client side
- **Server validation**: Added proper server-side validation for all block operations to prevent cheating
- **API deprecation**: Updated ResourceLocation usage to use fromNamespaceAndPath() instead of deprecated constructor

### ⚡ Performance

- **Network optimization**: Bulk block operations sent as single packets to reduce network overhead

---

## [1.0.0] - 2025-08-09

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
