package net.ray.CraftConfig.platform.fabric;
//? fabric {
/*import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ray.CraftConfig.api.registry.CraftConfigRegistry;
import net.ray.CraftConfig.config.Config;
import net.ray.CraftConfig.example.ExampleConfig;
import net.ray.CraftConfig.gui.ConfigScreen;
import net.ray.CraftConfig.platform.CraftConfigMod;
import net.ray.CraftConfig.platform.Platform;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> new ConfigScreen(parent, Config.config);
	}


    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return CraftConfigRegistry.getConfigScreenFactories();
    }
}
*///?}
