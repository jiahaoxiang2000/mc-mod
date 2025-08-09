package com.isomo.mod.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/**
 * Handles key binding registration for the Isomo Mod build system.
 * 
 * <p>This class registers client-side key mappings that allow players to interact
 * with the build mode system. Key bindings are automatically registered during
 * the mod loading process and can be customized through Minecraft's controls menu.
 * 
 * <p>Currently supports:
 * <ul>
 *   <li>Build Mode Toggle - Default key 'B' to enter/exit build mode</li>
 * </ul>
 * 
 * @author isomo
 * @since 1.0.0
 */
@Mod.EventBusSubscriber(modid = "isomomod", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyBindings {
    
    /**
     * Key mapping for toggling build mode on and off.
     * 
     * <p>Default binding: B key (GLFW_KEY_B)
     * <p>Category: "Isomo Mod" - appears in controls menu under this category
     * <p>Localization key: "key.isomomod.build_mode_toggle"
     * 
     * <p>When pressed, this key will toggle the build mode state through
     * the {@link com.isomo.mod.client.BuildModeManager} and show wireframe
     * previews to assist with building.
     */
    public static final KeyMapping BUILD_MODE_TOGGLE = new KeyMapping(
        "key.isomomod.build_mode_toggle",
        GLFW.GLFW_KEY_B,
        "key.categories.isomomod"
    );
    
    /**
     * Registers all key mappings with Forge's key binding system.
     * 
     * <p>This method is automatically called during mod initialization
     * via the Forge event bus. It registers all key mappings defined
     * in this class so they appear in Minecraft's controls menu and
     * can be customized by players.
     * 
     * @param event the key mapping registration event provided by Forge
     */
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(BUILD_MODE_TOGGLE);
    }
}