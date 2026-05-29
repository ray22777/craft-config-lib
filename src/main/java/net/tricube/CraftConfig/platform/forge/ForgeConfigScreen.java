package net.tricube.CraftConfig.platform.forge;
//? forge {
/*import net.minecraftforge.fml.ModList;
import net.minecraftforge.client.ConfigScreenHandler;
import net.tricube.CraftConfig.api.registry.CraftConfigRegistry;
import net.tricube.CraftConfig.api.v1.CraftConfig;
import net.tricube.CraftConfig.gui.ConfigScreen;

import java.util.Map;

public class ForgeConfigScreen {

    public static void registerProvidedConfigScreens() {
        Map<String, CraftConfig> configs = CraftConfigRegistry.getConfigsForRegistration();

        for (Map.Entry<String, CraftConfig> entry : configs.entrySet()) {
            String targetModId = entry.getKey();
            CraftConfig config = entry.getValue();
            ModList.get().getModContainerById(targetModId).ifPresent(modContainer -> {
                modContainer.registerExtensionPoint(
                    ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (mc, parent) -> new ConfigScreen(parent, config)
                    )
                );
            });
        }

    }
}
*///?}
