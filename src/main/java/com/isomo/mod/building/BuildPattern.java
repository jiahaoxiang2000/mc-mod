package com.isomo.mod.building;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a build pattern interface for the build mode system.
 * 
 * <p>Build patterns represent different structural templates that can be previewed
 * and built in the game world. Each pattern defines a collection of block positions
 * relative to a center point, allowing players to visualize and construct common
 * building structures like walls, floors, pillars, and custom shapes.
 * 
 * <p>Implementations of this interface should:
 * <ul>
 *   <li>Provide a human-readable name for display purposes</li>
 *   <li>Calculate all block positions relative to a center point</li>
 *   <li>Include a helpful description for tooltips or UI elements</li>
 *   <li>Be immutable and thread-safe for client-side use</li>
 * </ul>
 * 
 * <p>Build patterns are used by the {@link com.isomo.mod.client.BuildModeManager}
 * to generate preview positions and by the {@link com.isomo.mod.client.renderer.WireframeRenderer}
 * to display wireframe outlines in the game world.
 * 
 * @author isomo
 * @since 1.0.0
 * @see com.isomo.mod.building.BuildPatterns
 */
public interface BuildPattern {
    
    /**
     * Gets the display name of this build pattern.
     * 
     * <p>The name should be short, descriptive, and suitable for display
     * in user interfaces. Examples: "Single Block", "Wall 3x3", "Floor 5x5".
     * 
     * @return a human-readable name for this pattern, never null or empty
     */
    String getName();
    
    /**
     * Calculates all block positions for this pattern relative to a center point.
     * 
     * <p>This method takes a center position and returns a list of all block
     * positions that should be part of this pattern. The positions are absolute
     * world coordinates, calculated by applying the pattern's offset logic to
     * the provided center position.
     * 
     * <p>Implementations should:
     * <ul>
     *   <li>Return a new list each time (defensive copying)</li>
     *   <li>Never return null (return empty list if no positions)</li>
     *   <li>Include the center position if it's part of the pattern</li>
     *   <li>Use consistent coordinate system (center-relative)</li>
     * </ul>
     * 
     * @param center the center point around which to build the pattern
     * @return a list of absolute block positions for this pattern, never null
     * @throws IllegalArgumentException if center is null
     */
    List<BlockPos> getPositions(BlockPos center);
    
    /**
     * Gets a detailed description of this build pattern.
     * 
     * <p>The description should provide more details than the name, explaining
     * what the pattern does or how it's structured. This is useful for tooltips,
     * help text, or configuration interfaces.
     * 
     * <p>Examples: "Places a single block at cursor position", 
     * "Creates a 3x3 vertical wall pattern", "Builds a 5x5 horizontal floor".
     * 
     * @return a descriptive explanation of this pattern, never null or empty
     */
    String getDescription();
    
    /**
     * Calculates all block positions for this pattern with rotation applied.
     * 
     * <p>This method applies 90-degree rotation steps around the Y-axis to the
     * pattern before calculating positions. Each rotation step represents a
     * 90-degree clockwise rotation when viewed from above.
     * 
     * <p>Rotation steps:
     * <ul>
     *   <li>0 = No rotation (original orientation)</li>
     *   <li>1 = 90° clockwise rotation</li>
     *   <li>2 = 180° rotation</li>
     *   <li>3 = 270° clockwise rotation (90° counter-clockwise)</li>
     * </ul>
     * 
     * <p>Default implementation calls {@link #getPositions(BlockPos)} and applies
     * rotation transformation. Patterns that don't benefit from rotation (like
     * single blocks or symmetric patterns) may override this to optimize performance.
     * 
     * @param center the center point around which to build the pattern
     * @param rotations number of 90-degree rotation steps (0-3, wraps around)
     * @return a list of absolute block positions for this rotated pattern, never null
     * @throws IllegalArgumentException if center is null
     */
    default List<BlockPos> getRotatedPositions(BlockPos center, int rotations) {
        if (center == null) {
            throw new IllegalArgumentException("Center position cannot be null");
        }
        
        // Normalize rotations to 0-3 range
        int normalizedRotations = ((rotations % 4) + 4) % 4;
        
        // No rotation needed
        if (normalizedRotations == 0) {
            return getPositions(center);
        }
        
        // Get base positions and apply rotation
        List<BlockPos> basePositions = getPositions(center);
        List<BlockPos> rotatedPositions = new ArrayList<>();
        
        for (BlockPos pos : basePositions) {
            // Calculate relative position from center
            int relativeX = pos.getX() - center.getX();
            int relativeZ = pos.getZ() - center.getZ();
            int relativeY = pos.getY() - center.getY();
            
            // Apply rotation around Y-axis
            int newX = relativeX;
            int newZ = relativeZ;
            
            for (int i = 0; i < normalizedRotations; i++) {
                int tempX = newX;
                newX = -newZ;  // 90° clockwise rotation: (x,z) → (-z,x)
                newZ = tempX;
            }
            
            // Convert back to absolute position
            rotatedPositions.add(center.offset(newX, relativeY, newZ));
        }
        
        return rotatedPositions;
    }
}