package com.isomo.mod.client.renderer;

import com.isomo.mod.client.BuildModeManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.List;

public class WireframeRenderer {
    
    private static final float[] WIREFRAME_COLOR = {0.0f, 1.0f, 0.0f, 0.8f}; // Green
    private static final float[] INVALID_COLOR = {1.0f, 0.0f, 0.0f, 0.8f}; // Red
    
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
        
        for (BlockPos pos : positions) {
            renderBlockWireframe(poseStack, vertexConsumer, pos, cameraPos, WIREFRAME_COLOR);
        }
    }
    
    private static void renderBlockWireframe(PoseStack poseStack, VertexConsumer vertexConsumer, 
                                           BlockPos pos, Vec3 cameraPos, float[] color) {
        poseStack.pushPose();
        
        // Translate to block position relative to camera
        double x = pos.getX() - cameraPos.x;
        double y = pos.getY() - cameraPos.y;
        double z = pos.getZ() - cameraPos.z;
        
        poseStack.translate(x, y, z);
        Matrix4f matrix = poseStack.last().pose();
        
        float r = color[0];
        float g = color[1];
        float b = color[2];
        float a = color[3];
        
        // Draw the 12 edges of a block wireframe
        // Bottom face edges
        addLine(vertexConsumer, matrix, 0, 0, 0, 1, 0, 0, r, g, b, a);
        addLine(vertexConsumer, matrix, 1, 0, 0, 1, 0, 1, r, g, b, a);
        addLine(vertexConsumer, matrix, 1, 0, 1, 0, 0, 1, r, g, b, a);
        addLine(vertexConsumer, matrix, 0, 0, 1, 0, 0, 0, r, g, b, a);
        
        // Top face edges
        addLine(vertexConsumer, matrix, 0, 1, 0, 1, 1, 0, r, g, b, a);
        addLine(vertexConsumer, matrix, 1, 1, 0, 1, 1, 1, r, g, b, a);
        addLine(vertexConsumer, matrix, 1, 1, 1, 0, 1, 1, r, g, b, a);
        addLine(vertexConsumer, matrix, 0, 1, 1, 0, 1, 0, r, g, b, a);
        
        // Vertical edges
        addLine(vertexConsumer, matrix, 0, 0, 0, 0, 1, 0, r, g, b, a);
        addLine(vertexConsumer, matrix, 1, 0, 0, 1, 1, 0, r, g, b, a);
        addLine(vertexConsumer, matrix, 1, 0, 1, 1, 1, 1, r, g, b, a);
        addLine(vertexConsumer, matrix, 0, 0, 1, 0, 1, 1, r, g, b, a);
        
        poseStack.popPose();
    }
    
    private static void addLine(VertexConsumer vertexConsumer, Matrix4f matrix,
                               float x1, float y1, float z1, float x2, float y2, float z2,
                               float r, float g, float b, float a) {
        vertexConsumer.vertex(matrix, x1, y1, z1).color(r, g, b, a).normal(0, 1, 0).endVertex();
        vertexConsumer.vertex(matrix, x2, y2, z2).color(r, g, b, a).normal(0, 1, 0).endVertex();
    }
}