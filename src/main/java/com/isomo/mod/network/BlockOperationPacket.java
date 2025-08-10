package com.isomo.mod.network;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

/**
 * Network packet for communicating block operations between client and server.
 * 
 * <p>This packet carries information about block placement or deletion operations
 * that need to be processed by the server. It includes the operation type, target
 * positions, and associated item data for proper server-side validation and execution.
 * 
 * <p>The packet supports bulk operations, allowing multiple block positions to be
 * processed in a single network message. This is essential for build patterns that
 * affect multiple blocks simultaneously.
 * 
 * <p>Packet data structure:
 * <ul>
 *   <li><strong>Operation type</strong>: PLACE or DELETE</li>
 *   <li><strong>Positions</strong>: List of block positions to affect</li>
 *   <li><strong>Item stack</strong>: Block item for placement operations (empty for deletion)</li>
 *   <li><strong>Direction</strong>: Placement face direction for block context</li>
 * </ul>
 * 
 * @author isomo
 * @since 1.0.0
 */
public class BlockOperationPacket {
    
    /**
     * Enumeration of supported block operations.
     * 
     * <p>This enum defines the types of operations that can be requested
     * through the network packet. Each operation type requires different
     * validation and processing logic on the server side.
     */
    public enum OperationType {
        /**
         * Place blocks at the specified positions using the provided item stack.
         * 
         * <p>Requires a valid BlockItem in the item stack and will consume
         * items from the player's inventory based on blocks successfully placed.
         */
        PLACE,
        
        /**
         * Delete blocks at the specified positions.
         * 
         * <p>Will drop items according to the blocks' drop tables and the
         * player's current tool and enchantments.
         */
        DELETE
    }
    
    /**
     * The type of operation to perform.
     */
    private final OperationType operationType;
    
    /**
     * List of block positions to operate on.
     * 
     * <p>All positions in this list will be processed according to the
     * operation type. The server will validate each position individually.
     */
    private final List<BlockPos> positions;
    
    /**
     * Item stack containing the block to place (for PLACE operations).
     * 
     * <p>For DELETE operations, this can be empty. For PLACE operations,
     * this must contain a valid BlockItem.
     */
    private final ItemStack itemStack;
    
    /**
     * Direction for block placement context.
     * 
     * <p>Used to determine block orientation and placement behavior.
     * This corresponds to the face of the block that was targeted during
     * the original client-side ray trace.
     */
    private final Direction direction;
    
    /**
     * Creates a new block operation packet.
     * 
     * @param operationType the type of operation (PLACE or DELETE)
     * @param positions the list of block positions to operate on
     * @param itemStack the item stack for placement operations (can be empty for deletion)
     * @param direction the placement direction for block context
     * 
     * @throws IllegalArgumentException if operationType is null or positions is null/empty
     */
    public BlockOperationPacket(OperationType operationType, List<BlockPos> positions, 
                               ItemStack itemStack, Direction direction) {
        if (operationType == null) {
            throw new IllegalArgumentException("Operation type cannot be null");
        }
        if (positions == null || positions.isEmpty()) {
            throw new IllegalArgumentException("Positions list cannot be null or empty");
        }
        
        this.operationType = operationType;
        this.positions = new ArrayList<>(positions);
        this.itemStack = itemStack != null ? itemStack : ItemStack.EMPTY;
        this.direction = direction != null ? direction : Direction.UP;
    }
    
    /**
     * Encodes the packet data to a network buffer.
     * 
     * <p>This method serializes all packet data into a format suitable for
     * network transmission. The encoding order must match the decoding order
     * to ensure proper deserialization on the receiving end.
     * 
     * @param packet the packet instance to encode
     * @param buffer the network buffer to write data to
     */
    public static void encode(BlockOperationPacket packet, FriendlyByteBuf buffer) {
        // Write operation type as ordinal
        buffer.writeEnum(packet.operationType);
        
        // Write number of positions and then each position
        buffer.writeInt(packet.positions.size());
        for (BlockPos pos : packet.positions) {
            buffer.writeBlockPos(pos);
        }
        
        // Write item stack
        buffer.writeItem(packet.itemStack);
        
        // Write direction
        buffer.writeEnum(packet.direction);
    }
    
    /**
     * Decodes packet data from a network buffer.
     * 
     * <p>This method deserializes packet data received from the network into
     * a new packet instance. The decoding order must match the encoding order
     * to ensure proper reconstruction of the packet data.
     * 
     * @param buffer the network buffer to read data from
     * @return a new packet instance with decoded data
     */
    public static BlockOperationPacket decode(FriendlyByteBuf buffer) {
        // Read operation type
        OperationType operationType = buffer.readEnum(OperationType.class);
        
        // Read positions
        int positionCount = buffer.readInt();
        List<BlockPos> positions = new ArrayList<>();
        for (int i = 0; i < positionCount; i++) {
            positions.add(buffer.readBlockPos());
        }
        
        // Read item stack
        ItemStack itemStack = buffer.readItem();
        
        // Read direction
        Direction direction = buffer.readEnum(Direction.class);
        
        return new BlockOperationPacket(operationType, positions, itemStack, direction);
    }
    
    /**
     * Gets the operation type for this packet.
     * 
     * @return the operation type (PLACE or DELETE)
     */
    public OperationType getOperationType() {
        return operationType;
    }
    
    /**
     * Gets the list of positions to operate on.
     * 
     * @return an immutable copy of the positions list
     */
    public List<BlockPos> getPositions() {
        return new ArrayList<>(positions);
    }
    
    /**
     * Gets the item stack for placement operations.
     * 
     * @return the item stack, or ItemStack.EMPTY for deletion operations
     */
    public ItemStack getItemStack() {
        return itemStack;
    }
    
    /**
     * Gets the placement direction for block context.
     * 
     * @return the direction for block placement
     */
    public Direction getDirection() {
        return direction;
    }
}