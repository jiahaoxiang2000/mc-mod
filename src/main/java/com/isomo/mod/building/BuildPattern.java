package com.isomo.mod.building;

import net.minecraft.core.BlockPos;

import java.util.List;

public interface BuildPattern {
    
    String getName();
    
    List<BlockPos> getPositions(BlockPos center);
    
    String getDescription();
}