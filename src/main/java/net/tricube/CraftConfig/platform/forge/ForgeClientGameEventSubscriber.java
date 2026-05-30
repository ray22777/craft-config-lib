package net.tricube.CraftConfig.platform.forge;

//? forge {

/*import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;import net.tricube.CraftConfig.api.registry.KeybindRegistry;import net.tricube.CraftConfig.platform.CraftConfigMod;import net.tricube.CraftConfig.preset.PresetRegisteration;

@Mod.EventBusSubscriber(modid = CraftConfigMod.MOD_ID, value = Dist.CLIENT)
public class ForgeClientGameEventSubscriber {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
		ForgeCommands.register(event.getDispatcher());
	}

	@SubscribeEvent
	public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
		PresetRegisteration.checkPreset();
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event){
		if (event.phase != TickEvent.Phase.END) return;
		KeybindRegistry.tick(Minecraft.getInstance());
	}

	@SubscribeEvent
	public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		PresetRegisteration.checkPreset();
	}
}


*///?}
