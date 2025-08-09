package com.isomo.mod.client;

import java.util.List;

import com.isomo.mod.config.BuildModeConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
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
 * <p>Block placement follows the current build pattern, allowing players
 * to place multiple blocks in the pattern shape with a single click.
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
     * Handles right-click block placement logic.
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
        
        // Place blocks at all preview positions
        int blocksPlaced = 0;
        for (BlockPos pos : previewPositions) {
            if (canPlaceBlockAt(level, pos, blockItem, player)) {
                if (placeBlockAt(level, pos, blockItem, player, hitResult.getDirection())) {
                    blocksPlaced++;
                }
            }
        }
        
        // Consume items from inventory based on blocks placed
        if (blocksPlaced > 0 && !player.getAbilities().instabuild) {
            heldItem.shrink(blocksPlaced);
        }
    }
    
    /**
     * Handles left-click block deletion logic.
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
        
        // Delete blocks at all pattern positions
        int blocksDeleted = 0;
        for (BlockPos pos : patternPositions) {
            if (canDeleteBlockAt(level, pos)) {
                if (deleteBlockAt(level, pos, player)) {
                    blocksDeleted++;
                }
            }
        }
    }
    
    /**
     * Checks if a block can be placed at the specified position.
     * 
     * <p>This method verifies that the target position is valid for block
     * placement by checking for air blocks and ensuring the placement
     * wouldn't conflict with existing blocks or entities.
     * 
     * @param level the world level
     * @param pos the position to check
     * @param blockItem the block item to place
     * @param player the player attempting placement
     * @return true if the block can be placed at this position
     */
    private static boolean canPlaceBlockAt(Level level, BlockPos pos, BlockItem blockItem, LocalPlayer player) {
        // Check if the position is air or replaceable
        return level.getBlockState(pos).canBeReplaced(
            new BlockPlaceContext(level, player, InteractionHand.MAIN_HAND, 
                new ItemStack(blockItem), new BlockHitResult(Vec3.atCenterOf(pos), 
                Direction.UP, pos, false))
        );
    }
    
    /**
     * Places a block at the specified position.
     * 
     * <p>This method handles the actual block placement logic, including
     * setting the block state and triggering appropriate placement sounds
     * and effects.
     * 
     * @param level the world level
     * @param pos the position to place the block
     * @param blockItem the block item to place
     * @param player the player performing the placement
     * @param face the face direction for placement context
     * @return true if the block was successfully placed
     */
    private static boolean placeBlockAt(Level level, BlockPos pos, BlockItem blockItem, 
                                       LocalPlayer player, Direction face) {
        ItemStack itemStack = new ItemStack(blockItem);
        
        BlockPlaceContext context = new BlockPlaceContext(level, player, InteractionHand.MAIN_HAND,
            itemStack, new BlockHitResult(Vec3.atCenterOf(pos), face, pos, false));
        
        InteractionResult result = blockItem.place(context);
        
        return result.consumesAction();
    }
    
    /**
     * Checks if a block can be deleted at the specified position.
     * 
     * <p>This method verifies that the target position contains a block
     * that can be safely deleted, excluding protected blocks like bedrock.
     * 
     * @param level the world level
     * @param pos the position to check
     * @return true if the block can be deleted at this position
     */
    private static boolean canDeleteBlockAt(Level level, BlockPos pos) {
        var blockState = level.getBlockState(pos);
        
        // Don't delete air blocks
        if (blockState.isAir()) {
            return false;
        }
        
        // Don't delete bedrock or other unbreakable blocks
        if (blockState.getDestroySpeed(level, pos) < 0) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Deletes a block at the specified position.
     * 
     * <p>This method handles the actual block deletion logic, including
     * dropping items and triggering appropriate block breaking effects.
     * 
     * @param level the world level
     * @param pos the position to delete the block from
     * @param player the player performing the deletion
     * @return true if the block was successfully deleted
     */
    private static boolean deleteBlockAt(Level level, BlockPos pos, LocalPlayer player) {
        var blockState = level.getBlockState(pos);
        
        // Remove the block
        boolean success = level.removeBlock(pos, false);
        
        if (success && !player.getAbilities().instabuild) {
            // Drop the block as an item (client-side, items will be handled by server)
            blockState.getBlock().playerWillDestroy(level, pos, blockState, player);
        }
        
        return success;
    }
}