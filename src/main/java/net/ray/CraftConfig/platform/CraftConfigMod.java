package net.ray.CraftConfig.platform;


import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? fabric {
import net.ray.CraftConfig.platform.fabric.FabricPlatform;
//?} neoforge {
/*import net.ray.CraftConfig.platform.neoforge.NeoforgePlatform;
*///?} forge {
/*import net.ray.CraftConfig.platform.forge.ForgePlatform;
*///?}

@SuppressWarnings("LoggingSimilarMessage")
public class CraftConfigMod {

	public static final String MOD_ID = /*$ mod_id*/ "craft_config";
//	public static final String MOD_VERSION = /*$ mod_version*/ "1.0.0";
//	public static final String MOD_FRIENDLY_NAME = /*$ mod_name*/ "Craft Config Library";
	public static final Logger LOGGER = LogUtils.getLogger();

	private static final Platform PLATFORM = createPlatformInstance();

	public static void onInitialize() {
//		LOGGER.info("Initializing {} on {}", MOD_ID, HologramMod.xplat().loader());
//		LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
	}

	public static void onInitializeClient() {
//		LOGGER.info("Initializing {} Client on {}", MOD_ID, HologramMod.xplat().loader());
//		LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
	}

	static Platform xplat() {
		return PLATFORM;
	}

	private static Platform createPlatformInstance() {
		//? fabric {
		return new FabricPlatform();
		//?} neoforge {
		/*return new NeoforgePlatform();
		 *///?} forge {
		/*return new ForgePlatform();
		*///?}
	}
}
