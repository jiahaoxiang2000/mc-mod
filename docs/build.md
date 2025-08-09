# Build Mode System Documentation

## Overview

The Build Mode system provides a visual wireframe preview system to help players build more accurately and efficiently in Minecraft. Players can toggle build mode on/off and see green wireframe outlines that show exactly where blocks will be placed.

## Features

### Core Functionality
- **Toggle Build Mode**: Press 'B' to enter/exit build mode
- **Real-time Preview**: Green wireframes follow your cursor position
- **Multiple Build Patterns**: Support for different building structures
- **Performance Optimized**: Client-side only rendering with minimal overhead
- **No Interruption**: Works alongside vanilla Minecraft building mechanics

### Build Patterns Available
1. **Single Block** - Places one block at cursor position
2. **Wall 3x3** - Creates a 3x3 vertical wall pattern
3. **Floor 5x5** - Creates a 5x5 horizontal floor pattern
4. **Pillar 5H** - Creates a 5-block high vertical pillar
5. **Line H5** - Creates a 5-block horizontal line

## Usage Guide

### Basic Usage
1. **Activate**: Press 'B' to toggle build mode ON
   - Status message appears: "Build Mode: ON"
   - Green wireframes appear where you look
2. **Build**: Use normal Minecraft building mechanics
   - Wireframes guide accurate block placement
   - No confirmation dialogs - build at full speed
3. **Deactivate**: Press 'B' again to toggle build mode OFF
   - Status message appears: "Build Mode: OFF"
   - Wireframes disappear

### Visual Feedback
- **Green Wireframes**: Valid build positions
- **Preview Updates**: Wireframes follow your crosshair in real-time
- **5-block Reach**: Preview works within normal building range

## Technical Implementation

### Architecture
```
src/main/java/com/isomo/mod/
├── client/
│   ├── KeyBindings.java           # 'B' key registration
│   ├── BuildModeManager.java      # State management
│   ├── ClientEventHandler.java   # Input & render events
│   └── renderer/
│       └── WireframeRenderer.java # OpenGL wireframe rendering
└── building/
    ├── BuildPattern.java          # Pattern interface
    └── BuildPatterns.java         # Pattern definitions
```

### Key Components

#### BuildModeManager
- Singleton pattern for state management
- Tracks build mode on/off state
- Manages current build pattern selection
- Updates preview position based on player view

#### WireframeRenderer
- Efficient OpenGL line rendering
- Renders 12-edge wireframe cubes
- Uses green color (0.0f, 1.0f, 0.0f, 0.8f) for visibility
- Renders during AFTER_TRANSLUCENT_BLOCKS stage

#### KeyBindings
- Registers 'B' key mapping
- Uses LWJGL key constants
- Proper Forge event bus registration

#### ClientEventHandler
- Handles key input events
- Performs ray tracing to determine cursor position
- Updates wireframe render during level render events
- Shows status messages to player

### Performance Characteristics
- **Client-side Only**: No network packets or server load
- **Conditional Rendering**: Only renders when build mode is active
- **Efficient Ray Tracing**: 5-block reach limit
- **Minimal Memory**: Singleton pattern and efficient data structures

## Configuration

### Key Binding
- **Default Key**: B
- **Configurable**: Yes (through Minecraft's Controls menu)
- **Category**: "Isomo Mod"
- **Localization**: Supports en_us.json language file

### Pattern Switching
- **Current**: Fixed to single pattern during build mode session
- **Future Enhancement**: Scroll wheel or number keys for pattern cycling
- **Extensible**: Easy to add new patterns to BuildPatterns.java

## Troubleshooting

### Common Issues
1. **Wireframes Not Appearing**
   - Ensure build mode is active (press 'B')
   - Check you're looking at a valid block position
   - Verify you're within 5-block reach

2. **Key Binding Not Working**
   - Check Minecraft Controls menu for conflicts
   - Ensure mod is properly loaded
   - Look for "Isomo Mod" category in controls

3. **Performance Issues**
   - Build mode uses minimal resources
   - Disable if experiencing frame drops (rare)
   - Check other mods for conflicts

### Debug Information
- Build mode state logged during client setup
- Status messages show current mode state
- Console output available for debugging

## Future Enhancements

### Planned Features
1. **Pattern Cycling**: Switch between patterns with scroll wheel
2. **Custom Patterns**: User-defined building templates
3. **Color Coding**: Different colors for different block types
4. **Multi-Material Preview**: Show different materials in wireframe
5. **Saved Templates**: Store and recall complex building patterns

### Extension Points
- **New Patterns**: Add to BuildPatterns.java
- **Custom Rendering**: Extend WireframeRenderer
- **Input Handling**: Additional keys in KeyBindings
- **State Management**: Enhanced BuildModeManager features

## API Reference

### BuildPattern Interface
```java
public interface BuildPattern {
    String getName();                           // Human-readable name
    List<BlockPos> getPositions(BlockPos center); // Block positions to preview
    String getDescription();                    // Tooltip description
}
```

### BuildModeManager Methods
```java
boolean isBuildModeActive()              // Check current state
void toggleBuildMode()                   // Switch on/off
void setPreviewPosition(BlockPos pos)    // Update preview location
List<BlockPos> getPreviewPositions()     // Get current preview positions
```

This documentation covers the complete Build Mode system implementation and usage.