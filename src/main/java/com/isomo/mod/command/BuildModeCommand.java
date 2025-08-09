package com.isomo.mod.command;

import com.isomo.mod.config.BuildModeConfig;
import com.isomo.mod.client.BuildModeManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * Command handler for build mode configuration changes at runtime.
 * 
 * <p>This class provides client-side commands that allow players to modify
 * build mode settings without restarting the client. Changes take effect
 * immediately and persist for the current game session.
 * 
 * <p>Available commands:
 * <ul>
 *   <li>/buildmode color [preset] - Set wireframe color using presets</li>
 *   <li>/buildmode color [r] [g] [b] [a] - Set custom RGBA color</li>
 *   <li>/buildmode reach [distance] - Set preview reach distance</li>
 *   <li>/buildmode rotation [0-3] - Set pattern rotation (0°, 90°, 180°, 270°)</li>
 *   <li>/buildmode size wall [width] [height] - Set wall pattern size</li>
 *   <li>/buildmode size floor [width] [depth] - Set floor pattern size</li>
 *   <li>/buildmode size pillar [height] - Set pillar pattern height</li>
 *   <li>/buildmode size line [length] - Set line pattern length</li>
 *   <li>/buildmode info - Display current configuration</li>
 * </ul>
 * 
 * @author isomo
 * @since 1.0.0
 */
public class BuildModeCommand {
    
    /**
     * Registers all build mode commands with the command dispatcher.
     * 
     * <p>This method should be called during client initialization to make
     * the commands available to players. Commands are client-side only and
     * do not require server permissions.
     * 
     * @param dispatcher the command dispatcher to register commands with
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("buildmode")
            .then(Commands.literal("color")
                .then(Commands.argument("preset", StringArgumentType.string())
                    .suggests((context, builder) -> {
                        for (BuildModeConfig.WireframeColorType colorType : BuildModeConfig.WireframeColorType.values()) {
                            builder.suggest(colorType.name().toLowerCase());
                        }
                        return builder.buildFuture();
                    })
                    .executes(BuildModeCommand::setColorPreset))
                .then(Commands.argument("red", FloatArgumentType.floatArg(0.0f, 1.0f))
                    .then(Commands.argument("green", FloatArgumentType.floatArg(0.0f, 1.0f))
                        .then(Commands.argument("blue", FloatArgumentType.floatArg(0.0f, 1.0f))
                            .then(Commands.argument("alpha", FloatArgumentType.floatArg(0.0f, 1.0f))
                                .executes(BuildModeCommand::setColorCustom))))))
            .then(Commands.literal("reach")
                .then(Commands.argument("distance", FloatArgumentType.floatArg(1.0f, 128.0f))
                    .executes(BuildModeCommand::setReachDistance)))
            .then(Commands.literal("rotation")
                .then(Commands.argument("angle", IntegerArgumentType.integer(0, 3))
                    .executes(BuildModeCommand::setRotation)))
            .then(Commands.literal("size")
                .then(Commands.literal("wall")
                    .then(Commands.argument("width", IntegerArgumentType.integer(1, 50))
                        .then(Commands.argument("height", IntegerArgumentType.integer(1, 50))
                            .executes(BuildModeCommand::setWallSize))))
                .then(Commands.literal("floor")
                    .then(Commands.argument("width", IntegerArgumentType.integer(1, 50))
                        .then(Commands.argument("depth", IntegerArgumentType.integer(1, 50))
                            .executes(BuildModeCommand::setFloorSize))))
                .then(Commands.literal("pillar")
                    .then(Commands.argument("height", IntegerArgumentType.integer(1, 50))
                        .executes(BuildModeCommand::setPillarSize)))
                .then(Commands.literal("line")
                    .then(Commands.argument("length", IntegerArgumentType.integer(1, 50))
                        .executes(BuildModeCommand::setLineSize))))
            .then(Commands.literal("info")
                .executes(BuildModeCommand::showInfo))
        );
    }
    
    /**
     * Sets wireframe color using a predefined color preset.
     * 
     * @param context the command execution context
     * @return command execution result code
     */
    private static int setColorPreset(CommandContext<CommandSourceStack> context) {
        String presetName = StringArgumentType.getString(context, "preset").toUpperCase();
        
        try {
            BuildModeConfig.WireframeColorType colorType = BuildModeConfig.WireframeColorType.valueOf(presetName);
            BuildModeConfig.getInstance().setWireframeColor(colorType);
            
            context.getSource().sendSuccess(() -> 
                Component.literal("Wireframe color changed to " + presetName.toLowerCase()), false);
            
            return 1;
        } catch (IllegalArgumentException e) {
            context.getSource().sendFailure(
                Component.literal("Invalid color preset. Available: blue, green, yellow, purple, cyan, white"));
            return 0;
        }
    }
    
    /**
     * Sets wireframe color using custom RGBA values.
     * 
     * @param context the command execution context
     * @return command execution result code
     */
    private static int setColorCustom(CommandContext<CommandSourceStack> context) {
        float red = FloatArgumentType.getFloat(context, "red");
        float green = FloatArgumentType.getFloat(context, "green");
        float blue = FloatArgumentType.getFloat(context, "blue");
        float alpha = FloatArgumentType.getFloat(context, "alpha");
        
        BuildModeConfig.getInstance().setWireframeColor(red, green, blue, alpha);
        
        context.getSource().sendSuccess(() -> 
            Component.literal(String.format("Wireframe color set to RGBA(%.2f, %.2f, %.2f, %.2f)", 
                red, green, blue, alpha)), false);
        
        return 1;
    }
    
    /**
     * Sets the preview reach distance.
     * 
     * @param context the command execution context
     * @return command execution result code
     */
    private static int setReachDistance(CommandContext<CommandSourceStack> context) {
        float distance = FloatArgumentType.getFloat(context, "distance");
        
        BuildModeConfig.getInstance().setReachDistance(distance);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Preview reach distance set to " + distance + " blocks"), false);
        
        return 1;
    }
    
    /**
     * Sets the pattern rotation angle.
     * 
     * @param context the command execution context
     * @return command execution result code
     */
    private static int setRotation(CommandContext<CommandSourceStack> context) {
        int rotation = IntegerArgumentType.getInteger(context, "angle");
        
        BuildModeManager manager = BuildModeManager.getInstance();
        manager.setCurrentRotation(rotation);
        
        String rotationDesc = manager.getRotationDescription();
        String patternName = manager.getCurrentPattern().getName();
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Pattern rotation set to " + rotationDesc + " for " + patternName), false);
        
        return 1;
    }
    
    /**
     * Sets the wall pattern size.
     * 
     * @param context the command execution context
     * @return command execution result code
     */
    private static int setWallSize(CommandContext<CommandSourceStack> context) {
        int width = IntegerArgumentType.getInteger(context, "width");
        int height = IntegerArgumentType.getInteger(context, "height");
        
        BuildModeConfig config = BuildModeConfig.getInstance();
        config.setWallWidth(width);
        config.setWallHeight(height);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Wall pattern size set to " + width + "x" + height), false);
        
        return 1;
    }
    
    /**
     * Sets the floor pattern size.
     * 
     * @param context the command execution context
     * @return command execution result code
     */
    private static int setFloorSize(CommandContext<CommandSourceStack> context) {
        int width = IntegerArgumentType.getInteger(context, "width");
        int depth = IntegerArgumentType.getInteger(context, "depth");
        
        BuildModeConfig config = BuildModeConfig.getInstance();
        config.setFloorWidth(width);
        config.setFloorDepth(depth);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Floor pattern size set to " + width + "x" + depth), false);
        
        return 1;
    }
    
    /**
     * Sets the pillar pattern height.
     * 
     * @param context the command execution context
     * @return command execution result code
     */
    private static int setPillarSize(CommandContext<CommandSourceStack> context) {
        int height = IntegerArgumentType.getInteger(context, "height");
        
        BuildModeConfig config = BuildModeConfig.getInstance();
        config.setPillarHeight(height);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Pillar pattern height set to " + height + " blocks"), false);
        
        return 1;
    }
    
    /**
     * Sets the line pattern length.
     * 
     * @param context the command execution context
     * @return command execution result code
     */
    private static int setLineSize(CommandContext<CommandSourceStack> context) {
        int length = IntegerArgumentType.getInteger(context, "length");
        
        BuildModeConfig config = BuildModeConfig.getInstance();
        config.setLineLength(length);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Line pattern length set to " + length + " blocks"), false);
        
        return 1;
    }
    
    /**
     * Displays current build mode configuration information.
     * 
     * @param context the command execution context
     * @return command execution result code
     */
    private static int showInfo(CommandContext<CommandSourceStack> context) {
        BuildModeConfig config = BuildModeConfig.getInstance();
        BuildModeManager manager = BuildModeManager.getInstance();
        float[] color = config.getWireframeColor();
        
        context.getSource().sendSuccess(() -> 
            Component.literal("=== Build Mode Configuration ==="), false);
        
        context.getSource().sendSuccess(() -> 
            Component.literal(String.format("Wireframe Color: RGBA(%.2f, %.2f, %.2f, %.2f)", 
                color[0], color[1], color[2], color[3])), false);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Reach Distance: " + config.getReachDistance() + " blocks"), false);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Current Pattern: " + manager.getCurrentPattern().getName()), false);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Current Rotation: " + manager.getRotationDescription()), false);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Build Mode Active: " + (manager.isBuildModeActive() ? "Yes" : "No")), false);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("=== Pattern Sizes ==="), false);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Wall: " + config.getWallWidth() + "x" + config.getWallHeight()), false);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Floor: " + config.getFloorWidth() + "x" + config.getFloorDepth()), false);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Pillar Height: " + config.getPillarHeight() + " blocks"), false);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Line Length: " + config.getLineLength() + " blocks"), false);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Pattern Names: " + (config.isShowPatternName() ? "Enabled" : "Disabled")), false);
        
        return 1;
    }
}