package net.tricube.CraftConfig.platform.forge;

//? forge {

/*import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;import net.tricube.CraftConfig.platform.CraftConfigMod;


@Mod.EventBusSubscriber(modid = CraftConfigMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ForgeClientEventSubscriber {

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		CraftConfigMod.onInitializeClient();
		event.enqueueWork(()->{
			ForgeConfigScreen.registerProvidedConfigScreens();
		});
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
		ForgeKeybindRegistry.init(event);
	}
}


*///?}
