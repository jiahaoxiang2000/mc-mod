package com.isomo.mod.client;

import com.isomo.mod.building.BuildPattern;
import com.isomo.mod.building.BuildPatterns;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BuildModeManager {
    
    private static BuildModeManager instance;
    private boolean buildModeActive = false;
    private BuildPattern currentPattern = BuildPatterns.SINGLE_BLOCK;
    private BlockPos previewPosition = BlockPos.ZERO;
    
    private BuildModeManager() {}
    
    public static BuildModeManager getInstance() {
        if (instance == null) {
            instance = new BuildModeManager();
        }
        return instance;
    }
    
    public boolean isBuildModeActive() {
        return buildModeActive;
    }
    
    public void toggleBuildMode() {
        buildModeActive = !buildModeActive;
    }
    
    public void setBuildMode(boolean active) {
        this.buildModeActive = active;
    }
    
    public BuildPattern getCurrentPattern() {
        return currentPattern;
    }
    
    public void setCurrentPattern(BuildPattern pattern) {
        this.currentPattern = pattern;
    }
    
    public BlockPos getPreviewPosition() {
        return previewPosition;
    }
    
    public void setPreviewPosition(BlockPos position) {
        this.previewPosition = position;
    }
    
    public List<BlockPos> getPreviewPositions() {
        return currentPattern.getPositions(previewPosition);
    }
    
    public void nextPattern() {
        currentPattern = BuildPatterns.getNext(currentPattern);
    }
    
    public void previousPattern() {
        currentPattern = BuildPatterns.getPrevious(currentPattern);
    }
}