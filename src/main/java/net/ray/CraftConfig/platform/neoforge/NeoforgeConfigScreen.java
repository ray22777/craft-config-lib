package net.ray.CraftConfig.platform.neoforge;
//? neoforge {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.ray.CraftConfig.api.registry.CraftConfigRegistry;
import net.ray.CraftConfig.api.v1.CraftConfig;
import net.ray.CraftConfig.gui.ConfigScreen;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class NeoforgeConfigScreen {

	public static void registerProvidedConfigScreens() {
		Map<String, CraftConfig> configs = CraftConfigRegistry.getConfigsForRegistration();

		for (Map.Entry<String, CraftConfig> entry : configs.entrySet()) {
			String targetModId = entry.getKey();
			CraftConfig config = entry.getValue();

			ModList.get().getModContainerById(targetModId).ifPresent(modContainer -> {
				modContainer.registerExtensionPoint(
						IConfigScreenFactory.class,
						(client, parent) -> new ConfigScreen(parent, config)
				);
			});
		}
	}
}
*///?}
