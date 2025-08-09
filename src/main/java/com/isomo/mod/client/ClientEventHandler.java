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

/**
 * Handles client-side events for the build mode system.
 * 
 * <p>This event handler manages all client-side interactions for the build mode
 * functionality, including key input processing and rendering integration.
 * It serves as the bridge between Minecraft's event system and the build mode
 * components.
 * 
 * <p>Key responsibilities:
 * <ul>
 *   <li>Processing key input events for build mode toggle</li>
 *   <li>Updating preview positions based on player look direction</li>
 *   <li>Coordinating wireframe rendering during world render events</li>
 *   <li>Providing user feedback through action bar messages</li>
 * </ul>
 * 
 * <p>All methods in this class are static and registered with Forge's event
 * bus to receive client-side events automatically. The handler only operates
 * on the client side and does not send any network packets.
 * 
 * @author isomo
 * @since 1.0.0
 */
@Mod.EventBusSubscriber(modid = "isomomod", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {
    
    /** Maximum reach distance for build preview ray tracing (in blocks) */
    private static final double PREVIEW_REACH_DISTANCE = 5.0;
    
    /**
     * Handles key input events from the player.
     * 
     * <p>This method processes all key press events and specifically handles
     * the build mode toggle key. When the build mode key is pressed, it
     * toggles the build mode state and displays a status message to the player.
     * 
     * <p>The method performs safety checks to ensure the game world and player
     * are available before processing input. It uses the {@link KeyBindings}
     * consumeClick() method to ensure each key press is only processed once.
     * 
     * @param event the key input event containing key press information
     */
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
    
    /**
     * Handles world rendering events to draw build mode wireframes.
     * 
     * <p>This method is called during each frame when the world is being rendered.
     * It specifically hooks into the AFTER_TRANSLUCENT_BLOCKS stage to ensure
     * wireframes are drawn on top of world geometry but maintain proper depth testing.
     * 
     * <p>The method only executes when build mode is active and performs:
     * <ul>
     *   <li>Preview position updates based on player look direction</li>
     *   <li>Wireframe rendering through the WireframeRenderer</li>
     *   <li>Proper integration with Minecraft's rendering pipeline</li>
     * </ul>
     * 
     * <p>Performance considerations: This method is called every frame, so it
     * includes early returns to minimize overhead when build mode is inactive.
     * 
     * @param event the render event containing rendering context and stage information
     */
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
    
    /**
     * Updates the preview position based on player's look direction using ray tracing.
     * 
     * <p>This method performs a ray trace from the player's eye position in the
     * direction they are looking to find the nearest block surface. The preview
     * position is set to the adjacent block position where new blocks would be
     * placed (accounting for the hit surface normal).
     * 
     * <p>Ray tracing details:
     * <ul>
     *   <li>Start point: Player's eye position</li>
     *   <li>Direction: Player's look vector</li>
     *   <li>Max distance: {@value #PREVIEW_REACH_DISTANCE} blocks</li>
     *   <li>Target: Block outlines only (no fluids)</li>
     * </ul>
     * 
     * <p>The method handles edge cases where no valid block surface is found
     * by not updating the preview position, maintaining the last valid position.
     * 
     * @param minecraft the Minecraft client instance for world access
     * @param manager the build mode manager to update with new preview position
     */
    private static void updatePreviewPosition(Minecraft minecraft, BuildModeManager manager) {
        Player player = minecraft.player;
        if (player == null) {
            return;
        }
        
        // Perform ray trace to find where player is looking
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getViewVector(1.0F);
        Vec3 endPosition = eyePosition.add(lookVector.scale(PREVIEW_REACH_DISTANCE));
        
        ClipContext clipContext = new ClipContext(
            eyePosition,
            endPosition,
            ClipContext.Block.OUTLINE,
            ClipContext.Fluid.NONE,
            player
        );
        
        BlockHitResult hitResult = minecraft.level.clip(clipContext);
        
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            // Calculate target position adjacent to the hit block face
            BlockPos targetPos = hitResult.getBlockPos().relative(hitResult.getDirection());
            manager.setPreviewPosition(targetPos);
        }
    }
}