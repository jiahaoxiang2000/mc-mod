package com.isomo.mod.network;

import com.isomo.mod.IsomoMod;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Handles network packet registration and communication between client and server.
 * 
 * <p>This class manages the network channel for the Isomo mod, providing a centralized
 * location for packet registration and version management. It establishes a communication
 * channel between client and server for build mode operations.
 * 
 * <p>The network protocol version is used to ensure compatibility between different
 * mod versions. Clients and servers must have matching protocol versions to communicate.
 * 
 * <p>Packet types handled:
 * <ul>
 *   <li>Block operation packets (place/delete) from client to server</li>
 *   <li>Future expansion for additional mod features</li>
 * </ul>
 * 
 * @author isomo
 * @since 1.0.0
 */
public class NetworkHandler {
    
    /**
     * Protocol version for network compatibility checking.
     * 
     * <p>This version should be incremented whenever packet structures change
     * or new packets are added that would break compatibility with older versions.
     */
    private static final String PROTOCOL_VERSION = "1.0.0";
    
    /**
     * The network channel for mod communication.
     * 
     * <p>This channel handles all packet transmission between client and server
     * for build mode operations. It uses the mod's resource location as the
     * channel identifier.
     */
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        ResourceLocation.fromNamespaceAndPath(IsomoMod.MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );
    
    /**
     * Registers all network packets for the mod.
     * 
     * <p>This method must be called during mod initialization to register packet
     * handlers for both client-to-server and server-to-client communication.
     * Each packet type is assigned a unique message ID and associated with its
     * handler class.
     * 
     * <p>Packet registration order is important as it determines message IDs.
     * The same order must be maintained across client and server to ensure
     * proper packet deserialization.
     * 
     * @see BlockOperationPacket
     * @see BlockOperationPacketHandler
     */
    public static void registerPackets() {
        int messageId = 0;
        
        // Register block operation packet (client to server)
        CHANNEL.registerMessage(messageId++,
            BlockOperationPacket.class,
            BlockOperationPacket::encode,
            BlockOperationPacket::decode,
            BlockOperationPacketHandler::handle
        );
    }
}