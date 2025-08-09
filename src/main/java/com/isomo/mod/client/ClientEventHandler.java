package com.isomo.mod.client;

import com.isomo.mod.client.renderer.WireframeRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "isomomod", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {
    
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }
        
        // Handle build mode toggle
        if (KeyBindings.BUILD_MODE_TOGGLE.consumeClick()) {
            BuildModeManager.getInstance().toggleBuildMode();
            
            String status = BuildModeManager.getInstance().isBuildModeActive() ? "ON" : "OFF";
            minecraft.player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("Build Mode: " + status), 
                true
            );
        }
    }
    
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        
        BuildModeManager manager = BuildModeManager.getInstance();
        if (!manager.isBuildModeActive()) {
            return;
        }
        
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }
        
        // Update preview position based on where player is looking
        updatePreviewPosition(minecraft, manager);
        
        // Render wireframes using the event's buffer source
        WireframeRenderer.renderWireframes(
            event.getPoseStack(),
            minecraft.renderBuffers().bufferSource(),
            event.getCamera().getPosition()
        );
    }
    
    private static void updatePreviewPosition(Minecraft minecraft, BuildModeManager manager) {
        Player player = minecraft.player;
        if (player == null) {
            return;
        }
        
        // Perform ray trace to find where player is looking
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getViewVector(1.0F);
        Vec3 endPosition = eyePosition.add(lookVector.scale(5.0)); // 5 block reach
        
        ClipContext clipContext = new ClipContext(
            eyePosition,
            endPosition,
            ClipContext.Block.OUTLINE,
            ClipContext.Fluid.NONE,
            player
        );
        
        BlockHitResult hitResult = minecraft.level.clip(clipContext);
        
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos targetPos = hitResult.getBlockPos().relative(hitResult.getDirection());
            manager.setPreviewPosition(targetPos);
        }
    }
}