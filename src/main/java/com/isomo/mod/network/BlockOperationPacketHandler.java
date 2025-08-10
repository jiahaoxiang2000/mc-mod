package com.isomo.mod.network;

import com.isomo.mod.server.ServerBlockPlacementHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

/**
 * Handles incoming block operation packets on the server side.
 * 
 * <p>This class processes block operation requests received from clients and
 * delegates the actual block operations to the server-side handler. It ensures
 * proper validation, permission checking, and thread safety when processing
 * network packets.
 * 
 * <p>The handler performs several critical functions:
 * <ul>
 *   <li>Validates the incoming packet and network context</li>
 *   <li>Extracts the sender player from the network context</li>
 *   <li>Delegates to appropriate server-side operation handlers</li>
 *   <li>Handles operation results and potential errors gracefully</li>
 * </ul>
 * 
 * <p>All packet processing is performed on the network thread, which is safe
 * for world modification operations in modern Minecraft versions. The handler
 * ensures that operations are only processed if the player is valid and has
 * appropriate permissions.
 * 
 * @author isomo
 * @since 1.0.0
 */
public class BlockOperationPacketHandler {
    
    /**
     * Handles a block operation packet received from a client.
     * 
     * <p>This method is called automatically by the network system when a
     * {@link BlockOperationPacket} is received from a client. It validates
     * the network context, extracts the sender player, and delegates the
     * actual block operations to the server-side handler.
     * 
     * <p>Processing steps:
     * <ol>
     *   <li>Validate that the packet was received on the server side</li>
     *   <li>Extract the sender player from the network context</li>
     *   <li>Validate that the player is still valid and connected</li>
     *   <li>Delegate to the appropriate operation handler based on packet type</li>
     *   <li>Handle any errors or exceptions gracefully</li>
     * </ol>
     * 
     * <p>The method ensures thread safety by running all operations on the
     * network thread, which is safe for world modifications in recent
     * Minecraft/Forge versions.
     * 
     * @param packet the block operation packet to process
     * @param contextSupplier the network context supplier containing sender information
     */
    public static void handle(BlockOperationPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        
        // Ensure we're on the server side
        if (context.getDirection().getReceptionSide().isClient()) {
            // This packet should only be processed on the server
            context.setPacketHandled(true);
            return;
        }
        
        // Get the player who sent the packet
        ServerPlayer player = context.getSender();
        if (player == null) {
            // Invalid sender - packet cannot be processed
            context.setPacketHandled(true);
            return;
        }
        
        // Enqueue the work to be done on the server thread
        context.enqueueWork(() -> {
            try {
                // Validate that player is still connected and in a valid state
                if (player.hasDisconnected()) {
                    return;
                }
                
                // Delegate to appropriate handler based on operation type
                switch (packet.getOperationType()) {
                    case PLACE -> ServerBlockPlacementHandler.handleBlockPlacement(
                        player,
                        packet.getPositions(),
                        packet.getItemStack(),
                        packet.getDirection()
                    );
                    case DELETE -> ServerBlockPlacementHandler.handleBlockDeletion(
                        player,
                        packet.getPositions()
                    );
                }
                
            } catch (Exception e) {
                // Log any errors that occur during packet processing
                // In a production environment, you might want to use a proper logger
                System.err.println("Error processing block operation packet from player " 
                    + player.getName().getString() + ": " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        // Mark the packet as handled
        context.setPacketHandled(true);
    }
}