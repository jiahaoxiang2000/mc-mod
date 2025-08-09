package com.isomo.mod.config;

/**
 * Configuration manager for build mode settings that can be changed at runtime.
 * 
 * <p>This singleton class manages all configurable aspects of the build mode system,
 * including wireframe colors, patterns, and behavior settings. Changes take effect
 * immediately without requiring a client restart.
 * 
 * <p>The configuration supports:
 * <ul>
 *   <li>Runtime color changes for wireframes</li>
 *   <li>Transparency adjustments</li>
 *   <li>Pattern switching preferences</li>
 *   <li>Key binding modifications</li>
 * </ul>
 * 
 * @author isomo
 * @since 1.0.0
 */
public class BuildModeConfig {
    
    /** The singleton instance of the configuration manager */
    private static BuildModeConfig instance;
    
    /** Current wireframe color (RGBA format: red, green, blue, alpha) */
    private float[] wireframeColor = {0.0f, 0.5f, 1.0f, 0.8f}; // Default blue
    
    /** Color for invalid build positions */
    private float[] invalidColor = {1.0f, 0.0f, 0.0f, 0.8f}; // Red
    
    /** Whether to show pattern name in chat when switching */
    private boolean showPatternName = true;
    
    /** Maximum reach distance for previews */
    private double reachDistance = 5.0;
    
    /**
     * Private constructor to enforce singleton pattern.
     */
    private BuildModeConfig() {}
    
    /**
     * Gets the singleton instance of the configuration manager.
     * 
     * @return the singleton BuildModeConfig instance
     */
    public static BuildModeConfig getInstance() {
        if (instance == null) {
            instance = new BuildModeConfig();
        }
        return instance;
    }
    
    /**
     * Gets the current wireframe color.
     * 
     * @return array of [red, green, blue, alpha] values from 0.0 to 1.0
     */
    public float[] getWireframeColor() {
        return wireframeColor.clone(); // Return defensive copy
    }
    
    /**
     * Sets the wireframe color immediately without restart.
     * 
     * @param red red component (0.0-1.0)
     * @param green green component (0.0-1.0) 
     * @param blue blue component (0.0-1.0)
     * @param alpha alpha transparency (0.0-1.0)
     * @throws IllegalArgumentException if any value is outside 0.0-1.0 range
     */
    public void setWireframeColor(float red, float green, float blue, float alpha) {
        validateColorComponent(red, "red");
        validateColorComponent(green, "green");
        validateColorComponent(blue, "blue");
        validateColorComponent(alpha, "alpha");
        
        this.wireframeColor = new float[]{red, green, blue, alpha};
    }
    
    /**
     * Sets wireframe color using predefined color constants.
     * 
     * @param colorType the predefined color to use
     */
    public void setWireframeColor(WireframeColorType colorType) {
        this.wireframeColor = colorType.getColor().clone();
    }
    
    /**
     * Gets the invalid position wireframe color.
     * 
     * @return array of [red, green, blue, alpha] values from 0.0 to 1.0
     */
    public float[] getInvalidColor() {
        return invalidColor.clone(); // Return defensive copy
    }
    
    /**
     * Sets the invalid position wireframe color.
     * 
     * @param red red component (0.0-1.0)
     * @param green green component (0.0-1.0)
     * @param blue blue component (0.0-1.0)
     * @param alpha alpha transparency (0.0-1.0)
     * @throws IllegalArgumentException if any value is outside 0.0-1.0 range
     */
    public void setInvalidColor(float red, float green, float blue, float alpha) {
        validateColorComponent(red, "red");
        validateColorComponent(green, "green");
        validateColorComponent(blue, "blue");
        validateColorComponent(alpha, "alpha");
        
        this.invalidColor = new float[]{red, green, blue, alpha};
    }
    
    /**
     * Gets whether pattern names are shown in chat when switching.
     * 
     * @return true if pattern names should be displayed
     */
    public boolean isShowPatternName() {
        return showPatternName;
    }
    
    /**
     * Sets whether to show pattern names in chat when switching.
     * 
     * @param showPatternName true to show pattern names
     */
    public void setShowPatternName(boolean showPatternName) {
        this.showPatternName = showPatternName;
    }
    
    /**
     * Gets the maximum reach distance for preview placement.
     * 
     * @return reach distance in blocks
     */
    public double getReachDistance() {
        return reachDistance;
    }
    
    /**
     * Sets the maximum reach distance for preview placement.
     * 
     * @param distance reach distance in blocks (1.0-10.0)
     * @throws IllegalArgumentException if distance is outside valid range
     */
    public void setReachDistance(double distance) {
        if (distance < 1.0 || distance > 128.0) {
            throw new IllegalArgumentException("Reach distance must be between 1.0 and 128.0 blocks");
        }
        this.reachDistance = distance;
    }
    
    /**
     * Validates that a color component is within the valid range.
     * 
     * @param value the color component value to validate
     * @param componentName the name of the component for error messages
     * @throws IllegalArgumentException if value is outside 0.0-1.0 range
     */
    private void validateColorComponent(float value, String componentName) {
        if (value < 0.0f || value > 1.0f) {
            throw new IllegalArgumentException(componentName + " component must be between 0.0 and 1.0");
        }
    }
    
    /**
     * Predefined color types for easy wireframe customization.
     */
    public enum WireframeColorType {
        /** Bright blue with transparency */
        BLUE(new float[]{0.0f, 0.5f, 1.0f, 0.8f}),
        
        /** Bright green with transparency */
        GREEN(new float[]{0.0f, 1.0f, 0.0f, 0.8f}),
        
        /** Yellow/orange with transparency */
        YELLOW(new float[]{1.0f, 1.0f, 0.0f, 0.8f}),
        
        /** Purple/magenta with transparency */
        PURPLE(new float[]{1.0f, 0.0f, 1.0f, 0.8f}),
        
        /** Cyan with transparency */
        CYAN(new float[]{0.0f, 1.0f, 1.0f, 0.8f}),
        
        /** White with transparency */
        WHITE(new float[]{1.0f, 1.0f, 1.0f, 0.8f});
        
        private final float[] color;
        
        WireframeColorType(float[] color) {
            this.color = color;
        }
        
        /**
         * Gets the RGBA color array for this color type.
         * 
         * @return array of [red, green, blue, alpha] values
         */
        public float[] getColor() {
            return color;
        }
    }
}