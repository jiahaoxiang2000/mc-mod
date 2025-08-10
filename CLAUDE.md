# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Minecraft Forge mod project targeting Minecraft 1.20.1 with Forge 47.4.6. The mod is named "Isomo Mod" and provides a comprehensive toolbox of building functionality with an intuitive build mode system.

## Code Style Guidelines

### Documentation Requirements

- **Javadoc Comments**: All public classes, methods, and fields MUST have comprehensive Javadoc documentation
- **Parameter Documentation**: Use `@param` for all method parameters
- **Return Documentation**: Use `@return` for all non-void methods
- **Exception Documentation**: Use `@throws` for declared exceptions
- **Author/Version**: Include `@author` and `@since` tags on class-level documentation
- **Cross-references**: Use `@see` tags to link related methods and classes

### File Organization

- **Package Structure**:
  - `building/` - Build pattern definitions and interfaces
  - `client/` - Client-side managers and handlers
  - `event/` - Event handling system
- **Naming Conventions**: Use descriptive, self-documenting names

### Completion Requirements

When finishing code changes, you MUST:

1. **Update CHANGELOG.md**: Add detailed entry for new features/changes following the existing format
2. **Update README.md**: Reflect any new functionality or changes in the README
3. **Comprehensive Documentation**: Ensure all new code has complete Javadoc comments
4. **Cross-Reference**: Add appropriate `@see` tags to link related functionality
5. **Validation**: Include proper null checking and parameter validation
