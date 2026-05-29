package net.tricube.CraftConfig.platform.neoforge;
//? neoforge {
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.tricube.CraftConfig.api.registry.CraftConfigRegistry;
import net.tricube.CraftConfig.api.v1.CraftConfig;
import net.tricube.CraftConfig.gui.ConfigScreen;

import java.util.Map;

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
//?}
