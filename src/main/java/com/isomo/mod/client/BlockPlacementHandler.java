package com.isomo.mod.client;

import java.util.List;

import com.isomo.mod.config.BuildModeConfig;
import com.isomo.mod.network.BlockOperationPacket;
import com.isomo.mod.network.NetworkHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles extended reach block placement for build mode system.
 * 
 * <p>This class extends the vanilla block placement reach distance to match
 * the configurable preview reach distance when build mode is active. This
 * allows players to place blocks at the same distance where wireframe
 * previews are shown, creating a consistent building experience.
 * 
 * <p>The handler intercepts right-click events when:
 * <ul>
 *   <li>Build mode is active</li>
 *   <li>Player is holding a block item</li>
 *   <li>Target position is within configured reach distance</li>
 *   <li>Target position matches current preview positions</li>
 * </ul>
 * 
 * <p>Block placement follows the current build pattern, sending network packets
 * to the server for validation and execution. The server handles all actual
 * world modifications to ensure proper synchronization and compatibility.
 * 
 * @author isomo
 * @since 1.0.0
 */
@Mod.EventBusSubscriber(modid = "isomomod", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class BlockPlacementHandler {
    
    /**
     * Handles mouse click events for extended reach block placement and deletion.
     * 
     * <p>This method intercepts both right-click and left-click events when build mode is active:
     * <ul>
     *   <li><strong>Right-click</strong>: Places blocks at adjacent position (hit block + face direction)</li>
     *   <li><strong>Left-click</strong>: Deletes blocks at direct hit position (no offset)</li>
     * </ul>
     * 
     * <p>Both actions extend reach to match the preview reach distance and apply
     * the current build pattern shape.
     * 
     * <p>The placement/deletion process:
     * <ol>
     *   <li>Check if build mode is active</li>
     *   <li>Perform ray trace to find target position at extended reach</li>
     *   <li>Calculate pattern positions based on action type (adjacent vs direct)</li>
     *   <li>Execute blocks placement or deletion according to current build pattern</li>
     *   <li>Handle inventory changes (consume items or add drops)</li>
     * </ol>
     * 
     * @param event the mouse input event containing click information
     */
    @SubscribeEvent
    public static void onMouseClick(InputEvent.MouseButton event) {
        // Handle both right-click (1) and left-click (0) events
        if ((event.getButton() != 1 && event.getButton() != 0) || event.getAction() != 1) {
            return;
        }
        
        boolean isRightClick = event.getButton() == 1;
        boolean isLeftClick = event.getButton() == 0;
        
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        Level level = minecraft.level;
        
        if (player == null || level == null) {
            return;
        }
        
        BuildModeManager buildManager = BuildModeManager.getInstance();
        
        // Only handle actions when build mode is active
        if (!buildManager.isBuildModeActive()) {
            return;
        }
        
        ItemStack heldItem = player.getMainHandItem();
        
        // For right-click placement, only handle block items
        if (isRightClick && !(heldItem.getItem() instanceof BlockItem blockItem)) {
            return;
        }
        
        // For left-click deletion, we don't need to check held item
        BlockItem blockItem = (heldItem.getItem() instanceof BlockItem) ? (BlockItem) heldItem.getItem() : null;
        
        // Get configurable reach distance
        BuildModeConfig config = BuildModeConfig.getInstance();
        double reachDistance = config.getReachDistance();
        
        // Perform ray trace to find placement location
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getViewVector(1.0F);
        Vec3 endPosition = eyePosition.add(lookVector.scale(reachDistance));
        
        ClipContext clipContext = new ClipContext(
            eyePosition,
            endPosition,
            ClipContext.Block.OUTLINE,
            ClipContext.Fluid.NONE,
            player
        );
        
        BlockHitResult hitResult = level.clip(clipContext);
        
        // Only proceed if we hit a block within extended reach
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }
        
        if (isRightClick) {
            // Handle block placement (existing logic)
            handleBlockPlacement(event, level, player, buildManager, blockItem, hitResult, heldItem);
        } else if (isLeftClick) {
            // Handle block deletion (new logic)
            handleBlockDeletion(event, level, player, buildManager, hitResult);
        }
    }
    
    /**
     * Handles right-click block placement logic by sending a packet to the server.
     * 
     * <p>This method validates the placement request client-side and sends a network
     * packet to the server for actual processing. The server will handle validation,
     * inventory management, and world modification to ensure proper synchronization.
     * 
     * @param event the mouse input event
     * @param level the world level
     * @param player the player performing the action
     * @param buildManager the build mode manager instance
     * @param blockItem the block item to place
     * @param hitResult the ray trace hit result
     * @param heldItem the item stack being held
     */
    private static void handleBlockPlacement(InputEvent.MouseButton event, Level level, LocalPlayer player,
                                           BuildModeManager buildManager, BlockItem blockItem, 
                                           BlockHitResult hitResult, ItemStack heldItem) {
        // Get the placement position (adjacent to hit block)
        BlockPos placementPos = hitResult.getBlockPos().relative(hitResult.getDirection());
        
        // Get all positions that should be placed according to current pattern
        List<BlockPos> previewPositions = buildManager.getPreviewPositions();
        
        // Verify the clicked position matches one of the preview positions
        if (!previewPositions.contains(placementPos)) {
            return;
        }
        
        // Cancel the event to prevent vanilla placement
        event.setCanceled(true);
        
        // Send placement packet to server
        BlockOperationPacket packet = new BlockOperationPacket(
            BlockOperationPacket.OperationType.PLACE,
            previewPositions,
            heldItem,
            hitResult.getDirection()
        );
        
        NetworkHandler.CHANNEL.sendToServer(packet);
    }
    
    /**
     * Handles left-click block deletion logic by sending a packet to the server.
     * 
     * <p>This method validates the deletion request client-side and sends a network
     * packet to the server for actual processing. The server will handle validation,
     * permission checking, item drops, and world modification.
     * 
     * @param event the mouse input event
     * @param level the world level
     * @param player the player performing the action
     * @param buildManager the build mode manager instance
     * @param hitResult the ray trace hit result
     */
    private static void handleBlockDeletion(InputEvent.MouseButton event, Level level, LocalPlayer player,
                                          BuildModeManager buildManager, BlockHitResult hitResult) {
        // Get the deletion position (direct hit block, not adjacent)
        BlockPos deletionPos = hitResult.getBlockPos();
        
        // Calculate pattern positions starting from the hit block
        List<BlockPos> patternPositions = buildManager.getCurrentPattern()
            .getRotatedPositions(deletionPos, buildManager.getCurrentRotation());
        
        // Cancel the event to prevent vanilla block breaking
        event.setCanceled(true);
        
        // Send deletion packet to server
        BlockOperationPacket packet = new BlockOperationPacket(
            BlockOperationPacket.OperationType.DELETE,
            patternPositions,
            ItemStack.EMPTY,
            hitResult.getDirection()
        );
        
        NetworkHandler.CHANNEL.sendToServer(packet);
    }
}