package com.isomo.mod.client.renderer;

import java.util.List;

import org.joml.Matrix4f;

import com.isomo.mod.client.BuildModeManager;
import com.isomo.mod.config.BuildModeConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * Handles rendering wireframe previews for the build mode system.
 * 
 * <p>This utility class provides methods for rendering colored wireframe outlines
 * of blocks in 3D space. It's designed to show players exactly where blocks will
 * be placed when using different build patterns, helping with accurate construction.
 * 
 * <p>The renderer uses OpenGL line rendering through Minecraft's vertex consumer
 * system to draw efficient wireframe cubes. Each wireframe consists of 12 lines
 * forming the edges of a complete block outline.
 * 
 * <p>Features:
 * <ul>
 *   <li>Configurable wireframe colors that can be changed at runtime</li>
 *   <li>Separate colors for valid and invalid build positions</li>
 *   <li>Camera-relative positioning for proper world rendering</li>
 *   <li>Efficient batch rendering of multiple wireframes</li>
 * </ul>
 * 
 * @author isomo
 * @since 1.0.0
 */
public class WireframeRenderer {
    
    /**
     * Renders wireframe previews for all positions in the current build pattern.
     * 
     * <p>This is the main entry point for wireframe rendering. It checks if build
     * mode is active and renders wireframes for all block positions defined by
     * the current build pattern. Only renders if build mode is active and there
     * are positions to preview.
     * 
     * <p>The method handles:
     * <ul>
     *   <li>Build mode state checking</li>
     *   <li>Getting preview positions from BuildModeManager</li>
     *   <li>Setting up vertex consumers for line rendering</li>
     *   <li>Batching multiple wireframe renders efficiently</li>
     * </ul>
     * 
     * @param poseStack the transformation matrix stack for positioning
     * @param bufferSource the source for vertex buffers and rendering contexts
     * @param cameraPos the camera position in world coordinates for relative positioning
     */
    public static void renderWireframes(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 cameraPos) {
        BuildModeManager manager = BuildModeManager.getInstance();
        
        if (!manager.isBuildModeActive()) {
            return;
        }
        
        List<BlockPos> positions = manager.getPreviewPositions();
        if (positions.isEmpty()) {
            return;
        }
        
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
        BuildModeConfig config = BuildModeConfig.getInstance();
        float[] wireframeColor = config.getWireframeColor();
        
        // Batch render all wireframes to reduce matrix operations
        renderWireframeBatch(poseStack, vertexConsumer, positions, cameraPos, wireframeColor);
    }
    
    /**
     * Renders multiple wireframes in a single batch to optimize performance.
     * 
     * <p>This method reduces the number of matrix operations by using a single
     * pose stack transformation for all wireframes. Instead of pushing/popping
     * the pose stack for each wireframe, it calculates relative positions directly
     * and renders all wireframes with minimal matrix overhead.
     * 
     * @param poseStack the transformation matrix stack for positioning
     * @param vertexConsumer the vertex consumer for drawing lines
     * @param positions list of all block positions to render wireframes for
     * @param cameraPos the camera position for relative positioning calculations
     * @param color RGBA color array [red, green, blue, alpha] with values 0.0-1.0
     */
    private static void renderWireframeBatch(PoseStack poseStack, VertexConsumer vertexConsumer,
                                           List<BlockPos> positions, Vec3 cameraPos, float[] color) {
        poseStack.pushPose();
        Matrix4f matrix = poseStack.last().pose();
        
        float r = color[0];
        float g = color[1];
        float b = color[2];
        float a = color[3];
        
        // Render all wireframes using the same matrix to reduce operations
        for (BlockPos pos : positions) {
            renderBlockWireframeDirect(vertexConsumer, matrix, pos, cameraPos, r, g, b, a);
        }
        
        poseStack.popPose();
    }
    
    /**
     * Renders a wireframe directly without individual matrix transformations.
     * 
     * <p>This optimized method calculates world-relative positions directly
     * instead of using pose stack transformations, reducing matrix operation
     * overhead during batch rendering.
     * 
     * @param vertexConsumer the vertex consumer for drawing lines
     * @param matrix the transformation matrix for vertex positioning
     * @param pos the world block position to render wireframe at
     * @param cameraPos the camera position for relative positioning calculations
     * @param r red color component (0.0-1.0)
     * @param g green color component (0.0-1.0)
     * @param b blue color component (0.0-1.0)
     * @param a alpha transparency component (0.0-1.0)
     */
    private static void renderBlockWireframeDirect(VertexConsumer vertexConsumer, Matrix4f matrix,
                                                 BlockPos pos, Vec3 cameraPos,
                                                 float r, float g, float b, float a) {
        // Calculate relative position once
        float x = (float) (pos.getX() - cameraPos.x);
        float y = (float) (pos.getY() - cameraPos.y);
        float z = (float) (pos.getZ() - cameraPos.z);
        
        // Draw the 12 edges of a block wireframe with pre-calculated offsets
        // Bottom face edges (Y=0 plane)
        addLine(vertexConsumer, matrix, x, y, z, x + 1, y, z, r, g, b, a);
        addLine(vertexConsumer, matrix, x + 1, y, z, x + 1, y, z + 1, r, g, b, a);
        addLine(vertexConsumer, matrix, x + 1, y, z + 1, x, y, z + 1, r, g, b, a);
        addLine(vertexConsumer, matrix, x, y, z + 1, x, y, z, r, g, b, a);
        
        // Top face edges (Y=1 plane)
        addLine(vertexConsumer, matrix, x, y + 1, z, x + 1, y + 1, z, r, g, b, a);
        addLine(vertexConsumer, matrix, x + 1, y + 1, z, x + 1, y + 1, z + 1, r, g, b, a);
        addLine(vertexConsumer, matrix, x + 1, y + 1, z + 1, x, y + 1, z + 1, r, g, b, a);
        addLine(vertexConsumer, matrix, x, y + 1, z + 1, x, y + 1, z, r, g, b, a);
        
        // Vertical edges (connecting bottom to top)
        addLine(vertexConsumer, matrix, x, y, z, x, y + 1, z, r, g, b, a);
        addLine(vertexConsumer, matrix, x + 1, y, z, x + 1, y + 1, z, r, g, b, a);
        addLine(vertexConsumer, matrix, x + 1, y, z + 1, x + 1, y + 1, z + 1, r, g, b, a);
        addLine(vertexConsumer, matrix, x, y, z + 1, x, y + 1, z + 1, r, g, b, a);
    }
    
    /**
     * Adds a single line segment to the vertex consumer.
     * 
     * <p>This helper method creates a line segment between two 3D points with
     * the specified color. Each line consists of two vertices that define the
     * start and end points of the line segment.
     * 
     * <p>The method handles vertex transformation through the provided matrix
     * and applies consistent normal vectors for proper lighting (though lines
     * typically don't use lighting).
     * 
     * @param vertexConsumer the vertex consumer to add vertices to
     * @param matrix the transformation matrix for vertex positioning
     * @param x1 X coordinate of the line start point
     * @param y1 Y coordinate of the line start point  
     * @param z1 Z coordinate of the line start point
     * @param x2 X coordinate of the line end point
     * @param y2 Y coordinate of the line end point
     * @param z2 Z coordinate of the line end point
     * @param r red color component (0.0-1.0)
     * @param g green color component (0.0-1.0)
     * @param b blue color component (0.0-1.0)
     * @param a alpha transparency component (0.0-1.0)
     */
    private static void addLine(VertexConsumer vertexConsumer, Matrix4f matrix,
                               float x1, float y1, float z1, float x2, float y2, float z2,
                               float r, float g, float b, float a) {
        vertexConsumer.vertex(matrix, x1, y1, z1).color(r, g, b, a).normal(0, 1, 0).endVertex();
        vertexConsumer.vertex(matrix, x2, y2, z2).color(r, g, b, a).normal(0, 1, 0).endVertex();
    }
}