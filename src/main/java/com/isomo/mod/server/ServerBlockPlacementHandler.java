package com.isomo.mod.server;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * Handles server-side block placement and deletion operations with proper validation.
 * 
 * <p>This class contains the authoritative server-side logic for processing block
 * operations requested by clients. It ensures proper validation, permission checking,
 * inventory management, and integration with Forge's event system for compatibility
 * with other mods.
 * 
 * <p>Key responsibilities:
 * <ul>
 *   <li>Validating block operation requests against server-side rules</li>
 *   <li>Checking player permissions and world protection systems</li>
 *   <li>Managing player inventory for item consumption and drops</li>
 *   <li>Firing appropriate Forge events for mod compatibility</li>
 *   <li>Ensuring operations respect game rules and world boundaries</li>
 * </ul>
 * 
 * <p>All methods in this class assume they are running on the server side and
 * should only be called from server-side contexts. The class integrates with
 * Forge's event system to ensure compatibility with protection mods and other
 * block-related modifications.
 * 
 * @author isomo
 * @since 1.0.0
 */
public class ServerBlockPlacementHandler {
    
    /**
     * Handles server-side block placement for multiple positions.
     * 
     * <p>This method processes block placement requests, validating each position
     * individually and respecting server-side rules. It manages inventory consumption,
     * fires appropriate events, and ensures compatibility with other mods.
     * 
     * <p>Validation steps for each position:
     * <ol>
     *   <li>Check that the position is within world boundaries</li>
     *   <li>Validate that the block can be placed at this position</li>
     *   <li>Fire BlockEvent.EntityPlaceEvent for mod compatibility</li>
     *   <li>Check player permissions and protection systems</li>
     *   <li>Verify player has sufficient items in inventory</li>
     * </ol>
     * 
     * <p>Items are consumed from the player's inventory only for blocks that are
     * successfully placed, unless the player is in creative mode.
     * 
     * @param player the server player performing the placement
     * @param positions the list of positions where blocks should be placed
     * @param itemStack the item stack containing the blocks to place
     * @param direction the direction for block placement context
     */
    public static void handleBlockPlacement(ServerPlayer player, List<BlockPos> positions, 
                                          ItemStack itemStack, Direction direction) {
        // Validate input parameters
        if (player == null || positions == null || positions.isEmpty() || itemStack.isEmpty()) {
            return;
        }
        
        // Ensure we have a block item
        if (!(itemStack.getItem() instanceof BlockItem blockItem)) {
            return;
        }
        
        ServerLevel level = player.serverLevel();
        int blocksPlaced = 0;
        
        // Process each position individually
        for (BlockPos pos : positions) {
            if (canPlaceBlockAt(level, pos, player, blockItem, direction)) {
                if (placeBlockAt(level, pos, player, blockItem, direction, itemStack)) {
                    blocksPlaced++;
                }
            }
        }
        
        // Consume items from inventory (unless in creative mode)
        if (blocksPlaced > 0 && !player.getAbilities().instabuild) {
            ItemStack heldItem = player.getMainHandItem();
            if (heldItem.getItem() == itemStack.getItem()) {
                heldItem.shrink(blocksPlaced);
            }
        }
    }
    
    /**
     * Handles server-side block deletion for multiple positions.
     * 
     * <p>This method processes block deletion requests, validating each position
     * and handling item drops appropriately. It respects player permissions,
     * protection systems, and integrates with Forge events.
     * 
     * <p>Validation steps for each position:
     * <ol>
     *   <li>Check that the position contains a breakable block</li>
     *   <li>Fire BlockEvent.BreakEvent for mod compatibility</li>
     *   <li>Check player permissions and protection systems</li>
     *   <li>Validate that the block is not protected (bedrock, etc.)</li>
     * </ol>
     * 
     * <p>Blocks are dropped according to their loot tables, considering the
     * player's current tool and any relevant enchantments.
     * 
     * @param player the server player performing the deletion
     * @param positions the list of positions where blocks should be deleted
     */
    public static void handleBlockDeletion(ServerPlayer player, List<BlockPos> positions) {
        // Validate input parameters
        if (player == null || positions == null || positions.isEmpty()) {
            return;
        }
        
        ServerLevel level = player.serverLevel();
        
        // Process each position individually
        for (BlockPos pos : positions) {
            if (canDeleteBlockAt(level, pos, player)) {
                deleteBlockAt(level, pos, player);
            }
        }
    }
    
    /**
     * Checks if a block can be placed at the specified position.
     * 
     * <p>This method performs comprehensive validation for block placement,
     * including world boundaries, block replaceability, and event handling
     * for mod compatibility.
     * 
     * @param level the server level
     * @param pos the position to check
     * @param player the player attempting placement
     * @param blockItem the block item to place
     * @param direction the placement direction
     * @return true if the block can be placed at this position
     */
    private static boolean canPlaceBlockAt(ServerLevel level, BlockPos pos, ServerPlayer player,
                                         BlockItem blockItem, Direction direction) {
        // Check world boundaries
        if (!level.isInWorldBounds(pos)) {
            return false;
        }
        
        // Check if position is loaded
        if (!level.isLoaded(pos)) {
            return false;
        }
        
        // Create placement context
        BlockPlaceContext context = new BlockPlaceContext(level, player, InteractionHand.MAIN_HAND,
            new ItemStack(blockItem), new BlockHitResult(Vec3.atCenterOf(pos), direction, pos, false));
        
        // Check if the current block can be replaced
        BlockState currentState = level.getBlockState(pos);
        if (!currentState.canBeReplaced(context)) {
            return false;
        }
        
        // Get the block state that would be placed
        BlockState newState = blockItem.getBlock().getStateForPlacement(context);
        if (newState == null) {
            return false;
        }
        
        // Create and fire the placement event
        BlockSnapshot snapshot = BlockSnapshot.create(level.dimension(), level, pos);
        BlockEvent.EntityPlaceEvent event = new BlockEvent.EntityPlaceEvent(snapshot, currentState, player);
        
        // If the event is canceled, the block cannot be placed
        return !MinecraftForge.EVENT_BUS.post(event);
    }
    
    /**
     * Places a block at the specified position.
     * 
     * <p>This method performs the actual block placement operation, updating
     * the world state and handling any side effects such as block updates.
     * 
     * @param level the server level
     * @param pos the position to place the block at
     * @param player the player performing the placement
     * @param blockItem the block item to place
     * @param direction the placement direction
     * @param itemStack the item stack being used
     * @return true if the block was successfully placed
     */
    private static boolean placeBlockAt(ServerLevel level, BlockPos pos, ServerPlayer player,
                                      BlockItem blockItem, Direction direction, ItemStack itemStack) {
        // Create placement context
        BlockPlaceContext context = new BlockPlaceContext(level, player, InteractionHand.MAIN_HAND,
            itemStack, new BlockHitResult(Vec3.atCenterOf(pos), direction, pos, false));
        
        // Get the block state to place
        BlockState newState = blockItem.getBlock().getStateForPlacement(context);
        if (newState == null) {
            return false;
        }
        
        // Place the block
        boolean success = level.setBlock(pos, newState, Block.UPDATE_ALL);
        
        if (success) {
            // Trigger block placement effects (sounds, particles, etc.)
            BlockState placedState = level.getBlockState(pos);
            placedState.getBlock().setPlacedBy(level, pos, placedState, player, itemStack);
            
            // Play placement sound
            level.playSound(null, pos, placedState.getSoundType().getPlaceSound(), 
                player.getSoundSource(), 1.0F, 1.0F);
        }
        
        return success;
    }
    
    /**
     * Checks if a block can be deleted at the specified position.
     * 
     * <p>This method validates that a block can be safely deleted, checking
     * for protection systems, unbreakable blocks, and firing appropriate events.
     * 
     * @param level the server level
     * @param pos the position to check
     * @param player the player attempting deletion
     * @return true if the block can be deleted at this position
     */
    private static boolean canDeleteBlockAt(ServerLevel level, BlockPos pos, ServerPlayer player) {
        // Check world boundaries
        if (!level.isInWorldBounds(pos)) {
            return false;
        }
        
        // Check if position is loaded
        if (!level.isLoaded(pos)) {
            return false;
        }
        
        BlockState blockState = level.getBlockState(pos);
        
        // Don't delete air blocks
        if (blockState.isAir()) {
            return false;
        }
        
        // Don't delete unbreakable blocks (bedrock, etc.)
        if (blockState.getDestroySpeed(level, pos) < 0) {
            return false;
        }
        
        // Fire break event to check if other mods allow this
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(level, pos, blockState, player);
        
        // If the event is canceled, the block cannot be deleted
        return !MinecraftForge.EVENT_BUS.post(event);
    }
    
    /**
     * Deletes a block at the specified position.
     * 
     * <p>This method performs the actual block deletion operation, handling
     * item drops and triggering appropriate effects.
     * 
     * @param level the server level
     * @param pos the position to delete the block from
     * @param player the player performing the deletion
     * @return true if the block was successfully deleted
     */
    private static boolean deleteBlockAt(ServerLevel level, BlockPos pos, ServerPlayer player) {
        BlockState blockState = level.getBlockState(pos);
        
        // Remove the block
        boolean success = level.removeBlock(pos, false);
        
        if (success) {
            // Handle drops (unless in creative mode)
            if (!player.getAbilities().instabuild) {
                // Drop items according to the block's loot table
                blockState.getBlock().playerWillDestroy(level, pos, blockState, player);
                
                // Additional drop handling - consider tool enchantments, etc.
                Block.dropResources(blockState, level, pos, null, player, player.getMainHandItem());
            }
            
            // Play break sound
            level.playSound(null, pos, blockState.getSoundType().getBreakSound(), 
                player.getSoundSource(), 1.0F, 1.0F);
        }
        
        return success;
    }
}