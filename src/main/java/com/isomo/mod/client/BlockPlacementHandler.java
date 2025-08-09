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
     * Handles mouse click events for extended reach block placement.
     * 
     * <p>This method intercepts right-click events when build mode is active
     * and extends the placement reach to match the preview reach distance.
     * It performs ray tracing using the same logic as preview positioning
     * to find valid placement locations.
     * 
     * <p>The placement process:
     * <ol>
     *   <li>Check if build mode is active and player holds a block item</li>
     *   <li>Perform ray trace to find target position at extended reach</li>
     *   <li>Verify target position matches current preview positions</li>
     *   <li>Place blocks according to current build pattern</li>
     *   <li>Consume appropriate number of items from inventory</li>
     * </ol>
     * 
     * @param event the mouse input event containing click information
     */
    @SubscribeEvent
    public static void onMouseClick(InputEvent.MouseButton event) {
        // Only handle right-click events
        if (event.getButton() != 1 || event.getAction() != 1) {
            return;
        }
        
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        Level level = minecraft.level;
        
        if (player == null || level == null) {
            return;
        }
        
        BuildModeManager buildManager = BuildModeManager.getInstance();
        
        // Only handle placement when build mode is active
        if (!buildManager.isBuildModeActive()) {
            return;
        }
        
        ItemStack heldItem = player.getMainHandItem();
        
        // Only handle block items
        if (!(heldItem.getItem() instanceof BlockItem blockItem)) {
            return;
        }
        
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
}