package net.tricube.CraftConfig.platform.fabric;

//? fabric {

/*import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//~ if >=26.1 '.keybinding.v1.KeyBindingHelper' -> '.keymapping.v1.KeyMappingHelper'
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
//?if>=1.21.9
//import net.minecraft.resources.Identifier;
import net.tricube.CraftConfig.api.registry.CraftConfigRegistry;
import net.tricube.CraftConfig.api.registry.KeybindRegistry;
import net.tricube.CraftConfig.api.v1.*;
import net.tricube.CraftConfig.api.v1.*;
import net.tricube.CraftConfig.preset.ConfigPreset;
import net.tricube.CraftConfig.preset.PresetManager;

public class FabricKeybindRegistry {

    @SuppressWarnings("unchecked")
	public static void init() {
		for (CraftConfigRegistry.Entry entry : CraftConfigRegistry.all()) {
			String modId          = entry.modId();
			CraftConfig config    = entry.config();
			PresetManager manager = config.presetManager();

			String modDisplayName = FabricLoader.getInstance()
					.getModContainer(modId)
					.map(c -> c.getMetadata().getName())
					.orElse(modId);

			//?if>=1.21.9{
			/^KeyMapping.Category category = KeyMapping.Category.register(
					Identifier.withDefaultNamespace(entry.keybindCategory()));
			^///?}

			for (ConfigCategory cat : config.categories()) {
				for (ConfigSection sec : cat.sections()) {
					for (ConfigOption<?> opt : sec.options()) {
						ConfigKeybinds kb = opt.keybindSettings();
						if (kb == null) continue;

						kb.setOptionName(opt.name().getString());
						//~ if >=26.1 'KeyBindingHelper.registerKeyBinding' -> 'KeyMappingHelper.registerKeyMapping' {
						KeyMapping mapping = KeyBindingHelper.registerKeyBinding(new KeyMapping(
								//~}
								opt.name().getString(),
								InputConstants.Type.KEYSYM,
								kb.defaultKey(),
								//?if>=1.21.9{
								/^category
								^///?}else{
								"key.category.minecraft." + entry.keybindCategory()
								 //?}
						));

						kb.setKeyMapping(mapping);
						if (opt.type() == ConfigOption.Type.BOOLEAN) {
							KeybindRegistry.addBooleanEntry((ConfigOption<Boolean>) opt, kb, config);
						} else {
							KeybindRegistry.addCycleEntry(opt, kb, config);
						}
					}
				}
			}

			for (ConfigPreset preset : manager.presets()) {
				if (preset.isDefault()) continue;

				//~ if >=26.1 'KeyBindingHelper.registerKeyBinding' -> 'KeyMappingHelper.registerKeyMapping' {
				KeyMapping mapping = KeyBindingHelper.registerKeyBinding(new KeyMapping(
						//~}
						"[Preset] " + preset.name(),
						InputConstants.Type.KEYSYM,
						preset.keyBind(),
						//?if>=1.21.9{
						/^category
						^///?}else{
						"key.category.minecraft." + entry.keybindCategory()
						 //?}
				));

				preset.setKeyMapping(mapping);
				KeybindRegistry.addPresetEntry(preset, manager, modDisplayName);
			}
		}


	}
}
*///?}
