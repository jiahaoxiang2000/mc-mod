package com.isomo.mod.building;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Collection of predefined build patterns for the build mode system.
 * 
 * <p>This class provides a library of commonly used building patterns that
 * players can preview and use for construction. Each pattern implements the
 * {@link BuildPattern} interface and defines a specific arrangement of blocks
 * relative to a center position.
 * 
 * <p>The class includes:
 * <ul>
 *   <li>Individual pattern constants for direct access</li>
 *   <li>Navigation methods to cycle between patterns</li>
 *   <li>Utility methods to access all available patterns</li>
 * </ul>
 * 
 * <p>All patterns are designed to be useful for common building tasks and
 * provide good examples for creating custom patterns. The patterns use
 * standard Minecraft coordinate conventions (Y-up, right-handed system).
 * 
 * @author isomo
 * @since 1.0.0
 */
public class BuildPatterns {
    
    /**
     * Single block placement pattern.
     * 
     * <p>The most basic pattern that places only one block at the anchor position.
     * This is the default pattern and is useful for precise single-block placement
     * with wireframe guidance.
     */
    public static final BuildPattern SINGLE_BLOCK = new BuildPattern() {
        @Override
        public String getName() {
            return "Single Block";
        }
        
        @Override
        public List<BlockPos> getPositions(BlockPos anchor) {
            return List.of(anchor);
        }
        
        @Override
        public String getDescription() {
            return "Single block placement at anchor point";
        }
    };
    
    /**
     * 3x3 vertical wall pattern.
     * 
     * <p>Creates a 3x3 grid of blocks in the vertical plane (X-Y plane, Z=anchor).
     * The pattern starts from the anchor position (bottom-left corner) and extends
     * 3 blocks right and 3 blocks up. Useful for building walls, windows,
     * or decorative panels.
     */
    public static final BuildPattern WALL_3X3 = new BuildPattern() {
        @Override
        public String getName() {
            return "Wall 3x3";
        }
        
        @Override
        public List<BlockPos> getPositions(BlockPos anchor) {
            List<BlockPos> positions = new ArrayList<>();
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    positions.add(anchor.offset(x, y, 0));
                }
            }
            return positions;
        }
        
        @Override
        public String getDescription() {
            return "3x3 wall pattern starting from anchor point";
        }
    };
    
    /**
     * 5x5 horizontal floor pattern.
     * 
     * <p>Creates a 5x5 grid of blocks in the horizontal plane (X-Z plane, Y=anchor).
     * The pattern starts from the anchor position (corner) and extends 5 blocks
     * in both X and Z directions. Perfect for building floors, ceilings, or platforms.
     */
    public static final BuildPattern FLOOR_5X5 = new BuildPattern() {
        @Override
        public String getName() {
            return "Floor 5x5";
        }
        
        @Override
        public List<BlockPos> getPositions(BlockPos anchor) {
            List<BlockPos> positions = new ArrayList<>();
            for (int x = 0; x < 5; x++) {
                for (int z = 0; z < 5; z++) {
                    positions.add(anchor.offset(x, 0, z));
                }
            }
            return positions;
        }
        
        @Override
        public String getDescription() {
            return "5x5 floor pattern starting from corner";
        }
    };
    
    /**
     * 5-block high vertical pillar pattern.
     * 
     * <p>Creates a vertical column of 5 blocks starting at the anchor position
     * and extending upward. The pattern builds from the anchor position (bottom)
     * to 4 blocks above it. Useful for building pillars, towers, or support columns.
     */
    public static final BuildPattern PILLAR_HEIGHT_5 = new BuildPattern() {
        @Override
        public String getName() {
            return "Pillar 5H";
        }
        
        @Override
        public List<BlockPos> getPositions(BlockPos anchor) {
            List<BlockPos> positions = new ArrayList<>();
            for (int y = 0; y < 5; y++) {
                positions.add(anchor.offset(0, y, 0));
            }
            return positions;
        }
        
        @Override
        public String getDescription() {
            return "5 block high pillar starting from base";
        }
    };
    
    /**
     * 5-block horizontal line pattern.
     * 
     * <p>Creates a straight line of 5 blocks in the X direction (east-west).
     * The pattern starts from the anchor position and extends 5 blocks in the
     * positive X direction. Useful for building foundations, borders, or
     * alignment guides.
     */
    public static final BuildPattern LINE_HORIZONTAL_5 = new BuildPattern() {
        @Override
        public String getName() {
            return "Line H5";
        }
        
        @Override
        public List<BlockPos> getPositions(BlockPos anchor) {
            List<BlockPos> positions = new ArrayList<>();
            for (int x = 0; x < 5; x++) {
                positions.add(anchor.offset(x, 0, 0));
            }
            return positions;
        }
        
        @Override
        public String getDescription() {
            return "5 block horizontal line starting from anchor";
        }
    };
    
    /** Immutable list of all available build patterns in order */
    private static final List<BuildPattern> ALL_PATTERNS = Arrays.asList(
        SINGLE_BLOCK,
        WALL_3X3,
        FLOOR_5X5,
        PILLAR_HEIGHT_5,
        LINE_HORIZONTAL_5
    );
    
    /**
     * Gets the next pattern in the sequence after the given pattern.
     * 
     * <p>Cycles through patterns in a predefined order. When the last pattern
     * is reached, wraps around to the first pattern. If the current pattern
     * is not found in the list, returns the first pattern.
     * 
     * @param current the current build pattern
     * @return the next pattern in the sequence, never null
     * @throws IllegalArgumentException if current is null
     */
    public static BuildPattern getNext(BuildPattern current) {
        if (current == null) {
            throw new IllegalArgumentException("Current pattern cannot be null");
        }
        
        int currentIndex = ALL_PATTERNS.indexOf(current);
        if (currentIndex == -1 || currentIndex == ALL_PATTERNS.size() - 1) {
            return ALL_PATTERNS.get(0);
        }
        return ALL_PATTERNS.get(currentIndex + 1);
    }
    
    /**
     * Gets the previous pattern in the sequence before the given pattern.
     * 
     * <p>Cycles through patterns in reverse order. When the first pattern
     * is reached, wraps around to the last pattern. If the current pattern
     * is not found in the list, returns the last pattern.
     * 
     * @param current the current build pattern
     * @return the previous pattern in the sequence, never null
     * @throws IllegalArgumentException if current is null
     */
    public static BuildPattern getPrevious(BuildPattern current) {
        if (current == null) {
            throw new IllegalArgumentException("Current pattern cannot be null");
        }
        
        int currentIndex = ALL_PATTERNS.indexOf(current);
        if (currentIndex <= 0) {
            return ALL_PATTERNS.get(ALL_PATTERNS.size() - 1);
        }
        return ALL_PATTERNS.get(currentIndex - 1);
    }
    
    /**
     * Gets a defensive copy of all available build patterns.
     * 
     * <p>Returns a new list containing all predefined build patterns in their
     * natural order. The returned list can be safely modified without affecting
     * the internal pattern registry.
     * 
     * @return a new list containing all build patterns, never null or empty
     */
    public static List<BuildPattern> getAllPatterns() {
        return new ArrayList<>(ALL_PATTERNS);
    }
}