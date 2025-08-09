package com.isomo.mod.client;
import java.util.List;

import com.isomo.mod.building.BuildPattern;
import com.isomo.mod.building.BuildPatterns;

import net.minecraft.core.BlockPos;

/**
 * Manages the state and behavior of the build mode system.
 * 
 * <p>This singleton class serves as the central state manager for the build mode
 * functionality. It tracks whether build mode is currently active, manages the
 * current build pattern selection, and handles the preview position where
 * wireframes should be rendered.
 * 
 * <p>The manager provides methods to:
 * <ul>
 *   <li>Toggle build mode on and off</li>
 *   <li>Switch between different build patterns</li>
 *   <li>Update the preview position based on player cursor</li>
 *   <li>Generate preview positions for wireframe rendering</li>
 * </ul>
 * 
 * <p>This class is designed as a singleton to ensure consistent state across
 * all client-side build mode operations and to provide easy access from
 * event handlers and rendering systems.
 * 
 * @author isomo
 * @since 1.0.0
 */
public class BuildModeManager {
    
    /** The singleton instance of the build mode manager */
    private static BuildModeManager instance;
    
    /** Whether build mode is currently active */
    private boolean buildModeActive = false;
    
    /** The currently selected build pattern */
    private BuildPattern currentPattern = BuildPatterns.SINGLE_BLOCK;
    
    /** The current rotation state (0-3, representing 0°, 90°, 180°, 270°) */
    private int currentRotation = 0;
    
    /** The world position where previews should be shown */
    private BlockPos previewPosition = BlockPos.ZERO;
    
    /**
     * Private constructor to enforce singleton pattern.
     */
    private BuildModeManager() {}
    
    /**
     * Gets the singleton instance of the build mode manager.
     * 
     * <p>Creates a new instance if one doesn't already exist. This method
     * is thread-safe in single-threaded Minecraft client environment.
     * 
     * @return the singleton BuildModeManager instance
     */
    public static BuildModeManager getInstance() {
        if (instance == null) {
            instance = new BuildModeManager();
        }
        return instance;
    }
    
    /**
     * Checks if build mode is currently active.
     * 
     * @return true if build mode is active, false otherwise
     */
    public boolean isBuildModeActive() {
        return buildModeActive;
    }
    
    /**
     * Toggles build mode between active and inactive states.
     * 
     * <p>If build mode is currently off, this will turn it on.
     * If build mode is currently on, this will turn it off.
     */
    public void toggleBuildMode() {
        buildModeActive = !buildModeActive;
    }
    
    /**
     * Sets the build mode state explicitly.
     * 
     * @param active true to activate build mode, false to deactivate
     */
    public void setBuildMode(boolean active) {
        this.buildModeActive = active;
    }
    
    /**
     * Gets the currently selected build pattern.
     * 
     * @return the current BuildPattern being used for previews
     */
    public BuildPattern getCurrentPattern() {
        return currentPattern;
    }
    
    /**
     * Sets the current build pattern while preserving rotation.
     * 
     * <p>The rotation state is maintained when switching patterns, allowing
     * players to keep their preferred orientation across different pattern types.
     * 
     * @param pattern the BuildPattern to use for future previews
     * @throws IllegalArgumentException if pattern is null
     */
    public void setCurrentPattern(BuildPattern pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Build pattern cannot be null");
        }
        this.currentPattern = pattern;
        // Rotation is preserved when changing patterns
    }
    
    /**
     * Gets the current preview position in the world.
     * 
     * @return the BlockPos where previews should be centered
     */
    public BlockPos getPreviewPosition() {
        return previewPosition;
    }
    
    /**
     * Sets the preview position where wireframes should be shown.
     * 
     * <p>This is typically updated by the client event handler based on
     * where the player is looking in the world.
     * 
     * @param position the new BlockPos for preview placement
     */
    public void setPreviewPosition(BlockPos position) {
        this.previewPosition = position;
    }
    
    /**
     * Gets all block positions that should be previewed with wireframes.
     * 
     * <p>This method applies the current build pattern and rotation to the preview
     * position to generate a list of all BlockPos locations where wireframes should
     * be rendered. The rotation is applied according to the current rotation state.
     * 
     * @return a list of BlockPos locations for wireframe rendering with rotation applied
     */
    public List<BlockPos> getPreviewPositions() {
        return currentPattern.getRotatedPositions(previewPosition, currentRotation);
    }
    
    /**
     * Advances to the next build pattern in the sequence.
     * 
     * <p>Cycles through available patterns in a predefined order:
     * Single Block → Wall 3x3 → Floor 5x5 → Pillar 5H → Line H5 → (back to Single Block)
     * When the last pattern is reached, wraps around to the first pattern.
     * 
     * <p>This method is typically called in response to user input for pattern
     * switching, such as mouse wheel scrolling or keyboard shortcuts.
     * The rotation state is preserved when switching patterns.
     * 
     * @see BuildPatterns#getNext(BuildPattern)
     * @see #previousPattern()
     * @see #getCurrentPattern()
     */
    public void nextPattern() {
        currentPattern = BuildPatterns.getNext(currentPattern);
        // Rotation is preserved when changing patterns
    }
    
    /**
     * Goes back to the previous build pattern in the sequence.
     * 
     * <p>Cycles through available patterns in reverse order.
     * When the first pattern is reached, wraps around to the last pattern.
     * The rotation state is preserved when switching patterns.
     */
    public void previousPattern() {
        currentPattern = BuildPatterns.getPrevious(currentPattern);
        // Rotation is preserved when changing patterns
    }
    
    /**
     * Gets the current rotation state.
     * 
     * @return rotation value (0-3) representing 0°, 90°, 180°, 270°
     */
    public int getCurrentRotation() {
        return currentRotation;
    }
    
    /**
     * Sets the current rotation state.
     * 
     * @param rotation rotation value (0-3) representing 0°, 90°, 180°, 270°
     */
    public void setCurrentRotation(int rotation) {
        this.currentRotation = ((rotation % 4) + 4) % 4; // Normalize to 0-3 range
    }
    
    /**
     * Rotates the current pattern by 90 degrees clockwise.
     * 
     * <p>This method advances the rotation state by one step (90 degrees).
     * After reaching 270 degrees, the next rotation wraps back to 0 degrees.
     * 
     * <p>Rotation steps:
     * <ul>
     *   <li>0° → 90° → 180° → 270° → (back to 0°)</li>
     * </ul>
     * 
     * @return the new rotation value (0-3) after rotation
     */
    public int rotatePattern() {
        currentRotation = (currentRotation + 1) % 4;
        return currentRotation;
    }
    
    /**
     * Gets a human-readable rotation description.
     * 
     * @return rotation description like "0°", "90°", "180°", "270°"
     */
    public String getRotationDescription() {
        return switch (currentRotation) {
            case 0 -> "0°";
            case 1 -> "90°";
            case 2 -> "180°";
            case 3 -> "270°";
            default -> "0°"; // Fallback, should never happen
        };
    }
}