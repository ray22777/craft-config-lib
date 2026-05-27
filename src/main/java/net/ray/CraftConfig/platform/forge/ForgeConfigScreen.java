package net.ray.CraftConfig.platform.forge;
//? forge {
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.client.ConfigScreenHandler;
import net.ray.CraftConfig.api.registry.CraftConfigRegistry;
import net.ray.CraftConfig.api.v1.CraftConfig;
import net.ray.CraftConfig.gui.ConfigScreen;

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
//?}
