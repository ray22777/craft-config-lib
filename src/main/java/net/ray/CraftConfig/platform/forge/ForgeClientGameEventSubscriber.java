package net.ray.CraftConfig.platform.forge;

//? forge {

/*import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ray.CraftConfig.api.registry.KeybindRegistry;
import net.ray.CraftConfig.platform.CraftConfigMod;
import net.ray.CraftConfig.preset.PresetRegisteration;

@Mod.EventBusSubscriber(modid = CraftConfigMod.MOD_ID, value = Dist.CLIENT)
public class ForgeClientGameEventSubscriber {

	@SubscribeEvent
	public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
		ForgeCommands.register(event.getDispatcher());
	}

	@SubscribeEvent
	public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
		PresetRegisteration.checkPreset();
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent event){
		KeybindRegistry.tick(Minecraft.getInstance());
	}
	@SubscribeEvent
	public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		PresetRegisteration.checkPreset();
	}
}


*///?}
