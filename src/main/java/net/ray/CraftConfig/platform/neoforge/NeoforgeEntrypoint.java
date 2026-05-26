package net.ray.CraftConfig.platform.neoforge;

//? neoforge {

import net.neoforged.fml.common.Mod;
import net.ray.CraftConfig.platform.CraftConfigMod;
import net.ray.CraftConfig.platform.CraftConfigModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(CraftConfigMod.MOD_ID)
public class NeoforgeEntrypoint {

    public static final Logger LOGGER = LoggerFactory.getLogger(CraftConfigMod.MOD_ID);

    public NeoforgeEntrypoint() {
        CraftConfigModInitializer.onInit(LOGGER);
    }
}

//?}
