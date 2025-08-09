package com.isomo.mod.building;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuildPatterns {
    
    public static final BuildPattern SINGLE_BLOCK = new BuildPattern() {
        @Override
        public String getName() {
            return "Single Block";
        }
        
        @Override
        public List<BlockPos> getPositions(BlockPos center) {
            return List.of(center);
        }
        
        @Override
        public String getDescription() {
            return "Single block placement";
        }
    };
    
    public static final BuildPattern WALL_3X3 = new BuildPattern() {
        @Override
        public String getName() {
            return "Wall 3x3";
        }
        
        @Override
        public List<BlockPos> getPositions(BlockPos center) {
            List<BlockPos> positions = new ArrayList<>();
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    positions.add(center.offset(x, y, 0));
                }
            }
            return positions;
        }
        
        @Override
        public String getDescription() {
            return "3x3 wall pattern";
        }
    };
    
    public static final BuildPattern FLOOR_5X5 = new BuildPattern() {
        @Override
        public String getName() {
            return "Floor 5x5";
        }
        
        @Override
        public List<BlockPos> getPositions(BlockPos center) {
            List<BlockPos> positions = new ArrayList<>();
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    positions.add(center.offset(x, 0, z));
                }
            }
            return positions;
        }
        
        @Override
        public String getDescription() {
            return "5x5 floor pattern";
        }
    };
    
    public static final BuildPattern PILLAR_HEIGHT_5 = new BuildPattern() {
        @Override
        public String getName() {
            return "Pillar 5H";
        }
        
        @Override
        public List<BlockPos> getPositions(BlockPos center) {
            List<BlockPos> positions = new ArrayList<>();
            for (int y = 0; y < 5; y++) {
                positions.add(center.offset(0, y, 0));
            }
            return positions;
        }
        
        @Override
        public String getDescription() {
            return "5 block high pillar";
        }
    };
    
    public static final BuildPattern LINE_HORIZONTAL_5 = new BuildPattern() {
        @Override
        public String getName() {
            return "Line H5";
        }
        
        @Override
        public List<BlockPos> getPositions(BlockPos center) {
            List<BlockPos> positions = new ArrayList<>();
            for (int x = -2; x <= 2; x++) {
                positions.add(center.offset(x, 0, 0));
            }
            return positions;
        }
        
        @Override
        public String getDescription() {
            return "5 block horizontal line";
        }
    };
    
    private static final List<BuildPattern> ALL_PATTERNS = Arrays.asList(
        SINGLE_BLOCK,
        WALL_3X3,
        FLOOR_5X5,
        PILLAR_HEIGHT_5,
        LINE_HORIZONTAL_5
    );
    
    public static BuildPattern getNext(BuildPattern current) {
        int currentIndex = ALL_PATTERNS.indexOf(current);
        if (currentIndex == -1 || currentIndex == ALL_PATTERNS.size() - 1) {
            return ALL_PATTERNS.get(0);
        }
        return ALL_PATTERNS.get(currentIndex + 1);
    }
    
    public static BuildPattern getPrevious(BuildPattern current) {
        int currentIndex = ALL_PATTERNS.indexOf(current);
        if (currentIndex <= 0) {
            return ALL_PATTERNS.get(ALL_PATTERNS.size() - 1);
        }
        return ALL_PATTERNS.get(currentIndex - 1);
    }
    
    public static List<BuildPattern> getAllPatterns() {
        return new ArrayList<>(ALL_PATTERNS);
    }
}