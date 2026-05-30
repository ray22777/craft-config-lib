package net.tricube.CraftConfig.platform.neoforge;

//? neoforge {

/*import net.minecraft.client.Minecraft;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.tricube.CraftConfig.api.registry.KeybindRegistry;
import net.tricube.CraftConfig.platform.CraftConfigMod;
import net.tricube.CraftConfig.preset.PresetRegisteration;

@EventBusSubscriber(modid = CraftConfigMod.MOD_ID, value = Dist.CLIENT)
public class NeoforgeClientEventSubscriber {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
		CraftConfigMod.onInitializeClient();
		event.enqueueWork(NeoforgeConfigScreen::registerProvidedConfigScreens);

    }
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
		NeoforgeCommands.register(event.getDispatcher());
	}
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        NeoforgeKeybindRegistry.init(event);
    }

	@SubscribeEvent
	public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
		PresetRegisteration.checkPreset();
	}

	@SubscribeEvent
	public static void onClientLogin(PlayerEvent.PlayerChangedDimensionEvent event) {
		PresetRegisteration.checkPreset();
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event){
		KeybindRegistry.tick(Minecraft.getInstance());
	}
}

*///?}

