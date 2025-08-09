# Source Code Overview

This document provides an overview of the main source code files in the `src/main/java/com/isomo/mod/` directory.

## IsomoMod.java

The main mod class (`IsomoMod.java:32`) serves as the entry point and central registry for the Isomo Mod. Key features include:

### Core Components
- **Mod ID**: `isomomod` (`IsomoMod.java:36`)
- **Logger**: SLF4J logger for debugging and information output (`IsomoMod.java:38`)
- **Deferred Registers**: Used for registering blocks, items, and creative mode tabs (`IsomoMod.java:40-44`)

### Example Content
The mod includes example implementations to demonstrate Forge's registration system:

- **Example Block**: A basic stone-colored block (`IsomoMod.java:47`)
- **Example Block Item**: The corresponding item form of the block (`IsomoMod.java:49`)
- **Example Item**: A food item with custom nutrition properties (`IsomoMod.java:52-53`)
- **Example Creative Tab**: Custom creative mode tab containing the example item (`IsomoMod.java:56-61`)

### Event Handling
The mod implements several event handlers:

- **Common Setup** (`IsomoMod.java:87-98`): Handles mod initialization, logging configuration values
- **Creative Tab Population** (`IsomoMod.java:101-105`): Adds items to existing creative tabs
- **Server Starting** (`IsomoMod.java:109-113`): Responds to server startup events
- **Client Setup** (`IsomoMod.java:120-125`): Client-side initialization and user information logging

### Constructor
The mod constructor (`IsomoMod.java:63-85`) sets up:
- Event listeners for mod lifecycle events
- Registration of deferred registers to the mod event bus
- Configuration registration with Forge's config system

## Config.java

The configuration class (`Config.java:19`) demonstrates Forge's configuration API usage:

### Configuration Options
- **logDirtBlock** (`Config.java:23-25`): Boolean flag to control dirt block logging
- **magicNumber** (`Config.java:27-29`): Integer value with range validation (0 to Integer.MAX_VALUE)
- **magicNumberIntroduction** (`Config.java:31-33`): String for customizing log messages
- **items** (`Config.java:36-38`): List of item resource locations for logging

### Features
- **Validation**: Custom validation for item names using `validateItemName` (`Config.java:47-50`)
- **Dynamic Loading**: Configuration values are loaded and converted to usable objects (`Config.java:53-63`)
- **Type Safety**: Uses ForgeConfigSpec for type-safe configuration management

### Usage
Configuration values are accessed as static fields after being loaded from the config file during mod initialization. The config file is automatically created and managed by Forge's configuration system.

## Architecture Notes

The codebase follows Forge's recommended patterns:
- Uses `DeferredRegister` for lazy registration of game objects
- Implements proper event handling through both instance and static methods
- Separates configuration logic into its own class
- Provides examples that can be extended or replaced with actual mod content

The current implementation serves as a foundation that can be built upon to add actual mod functionality while maintaining proper Forge integration patterns.