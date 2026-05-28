package net.ray.CraftConfig.platform.neoforge;

//? neoforge {

/*import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.ray.CraftConfig.api.registry.CraftConfigRegistry;
import net.ray.CraftConfig.api.registry.KeybindRegistry;
import net.ray.CraftConfig.platform.CraftConfigMod;
import net.ray.CraftConfig.preset.PresetRegisteration;

@EventBusSubscriber(modid = CraftConfigMod.MOD_ID, value = Dist.CLIENT)
public class NeoforgeClientEventSubscriber {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
		NeoforgeConfigScreen.registerProvidedConfigScreens();
		CraftConfigMod.onInitializeClient();
    }
	@SubscribeEvent
	public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
		NeoforgeCommands.register(event.getDispatcher());
	}
    @SubscribeEvent
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

