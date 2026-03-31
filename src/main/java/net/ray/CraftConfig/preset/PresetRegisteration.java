package net.ray.CraftConfig.preset;

import net.minecraft.client.Minecraft;
import net.ray.CraftConfig.api.registry.CraftConfigRegistry;
import net.ray.CraftConfig.platform.CraftConfigMod;

public class PresetRegisteration {
	public static void checkPreset(){
		Minecraft mc = Minecraft.getInstance();

		String worldId = resolveWorldId(mc);
		if (worldId == null || worldId.isBlank()) {
			CraftConfigMod.LOGGER.warn("[CraftConfig] No valid worldId found, skipping preset switch.");
			return;
		}

		for (CraftConfigRegistry.Entry entry : CraftConfigRegistry.all()) {
			entry.config().presetManager()
					.resolveForWorld(worldId)
					.ifPresent(preset ->{
						entry.config().presetManager().switchTo(preset);
						CraftConfigMod.LOGGER.info("[CraftConfig] Loaded preset ["+ preset.name() + "] for world: " + worldId);
					});
		}
	}
	private static String resolveWorldId(Minecraft mc) {
		if (mc == null || mc.getConnection() == null) return null;

		if (mc.isLocalServer() && mc.getSingleplayerServer() != null) {
			return mc.getSingleplayerServer().getWorldData().getLevelName();
		}

		if (mc.getCurrentServer() != null) {
			return mc.getCurrentServer().ip;
		}

		if (mc.level != null && mc.level.getServer() != null) {
			return mc.level.getServer().getWorldData().getLevelName();
		}

		return null;
	}
}
