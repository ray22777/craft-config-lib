package net.tricube.CraftConfig.platform;


import com.mojang.logging.LogUtils;
import net.tricube.CraftConfig.config.Config;
import org.slf4j.Logger;

//? fabric {
/*import net.tricube.CraftConfig.platform.fabric.FabricPlatform;
*///?} neoforge {
import net.tricube.CraftConfig.platform.neoforge.NeoforgePlatform;
//?} forge {
/*import net.tricube.CraftConfig.platform.forge.ForgePlatform;
*///?}

@SuppressWarnings("LoggingSimilarMessage")
public class CraftConfigMod {

	public static final String MOD_ID = /*$ mod_id*/ "craft_config";
//	public static final String MOD_VERSION = /*$ mod_version*/ "1.0.0";
//	public static final String MOD_FRIENDLY_NAME = /*$ mod_name*/ "Craft Config Library";
	public static final Logger LOGGER = LogUtils.getLogger();

	private static final Platform PLATFORM = createPlatformInstance();

	public static void onInitialize() {

	}

	public static void onInitializeClient() {
		LOGGER.info("Craft Config Lib loaded.");

		//register your mods in Client Init
		Config.init();
//		ExampleConfig.init(); //registering it
	}

	static Platform xplat() {
		return PLATFORM;
	}

	private static Platform createPlatformInstance() {
		//? fabric {
		/*return new FabricPlatform();
		*///?} neoforge {
		return new NeoforgePlatform();
		 //?} forge {
		/*return new ForgePlatform();
		*///?}
	}
}
