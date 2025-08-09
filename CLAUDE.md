# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Minecraft Forge mod project targeting Minecraft 1.20.1 with Forge 47.4.6. The mod is currently named "Isomo Mod" and provides a toolbox of functionality.

## Development Commands

### Building and Testing

- `./gradlew build` - Builds the mod JAR file
- `./gradlew clean` - Cleans build artifacts
- `./gradlew jar` - Creates the mod JAR without running tests
- `./gradlew reobfJar` - Reobfuscates the JAR for distribution
- `./gradlew test` - Runs unit tests

### Running Minecraft

- `./gradlew runClient` - Launches Minecraft client with the mod loaded
- `./gradlew runServer` - Launches dedicated server with the mod
- `./gradlew runData` - Runs data generators
- `./gradlew runGameTestServer` - Runs game tests

## Project Structure

The mod follows standard Forge mod structure and should be updated to match gradle.properties configuration:

**Current Structure (needs updating):**

- **Main mod class**: `src/main/java/com/example/examplemod/ExampleMod.java` - Contains mod initialization, registry setup, and event handling
- **Configuration**: `src/main/java/com/example/examplemod/Config.java` - Handles mod configuration

**Target Structure (based on gradle.properties):**

- **Main mod class**: `src/main/java/com/isomo/mod/IsomoMod.java` - Should contain @Mod("isomomod") annotation
- **Configuration**: `src/main/java/com/isomo/mod/Config.java` - Handles mod configuration
- **Resources**: `src/main/resources/META-INF/mods.toml` - Mod metadata and dependencies
- **Generated resources**: `src/generated/resources/` - Auto-generated assets from data generators

**Package Structure:**

- Base package: `com.isomo.mod` (from mod_group_id in gradle.properties)
- Mod ID: `isomomod` (from mod_id in gradle.properties)
- Main class should be annotated with @Mod("isomomod")

### Key Configuration Files

- `gradle.properties` - Contains mod metadata (mod_id: isomomod, version: 1.0.0, author: isomo, group_id: com.isomo.mod)
- `build.gradle` - Forge build configuration with Chinese mirror repositories for faster downloads
- `run/` - Development runtime directory for client/server testing

## Architecture Notes

The mod uses Forge's DeferredRegister system for registering content:

- Blocks registered through `IsomoMod.BLOCKS` (or equivalent in main mod class)
- Items registered through `IsomoMod.ITEMS` (or equivalent in main mod class)
- Creative tabs registered through `IsomoMod.CREATIVE_MODE_TABS` (or equivalent in main mod class)

**Important:** The current codebase uses example package structure (`com.example.examplemod`) but should be refactored to match the gradle.properties configuration (`com.isomo.mod` package with `isomomod` mod ID).

The codebase currently includes example implementations for blocks, items, and creative mode tabs that serve as templates for new content.

Configuration is handled through Forge's config system with automatic file generation and validation.

## Development Notes

- The project uses Java 17 as required by Minecraft 1.20.1
- Mappings are set to "official" Mojang mappings
- Chinese Maven mirrors are configured for faster dependency downloads
- The working directory for run configurations is `run/`
- Generated resources from data generators are included in the build
