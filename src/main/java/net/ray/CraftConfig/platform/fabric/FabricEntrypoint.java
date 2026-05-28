package net.ray.CraftConfig.platform.fabric;

//? fabric {

import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ModInitializer;
import net.ray.CraftConfig.platform.CraftConfigMod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entrypoint("main")
public final class FabricEntrypoint implements ModInitializer {
	public static final String MOD_ID = CraftConfigMod.MOD_ID;
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CraftConfigMod.onInitialize();

	}
}
//?}
