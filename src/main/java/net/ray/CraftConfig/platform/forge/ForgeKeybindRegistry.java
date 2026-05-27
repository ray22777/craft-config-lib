package net.ray.CraftConfig.platform.forge;

//? forge {

/*import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
//?if>=1.21.9
//import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.ModList;
import net.ray.CraftConfig.api.registry.CraftConfigRegistry;
import net.ray.CraftConfig.api.registry.KeybindRegistry;
import net.ray.CraftConfig.api.v1.*;
import net.ray.CraftConfig.preset.ConfigPreset;
import net.ray.CraftConfig.preset.PresetManager;

public class ForgeKeybindRegistry {

    @SuppressWarnings("unchecked")
    public static void init(RegisterKeyMappingsEvent event) {
        for (CraftConfigRegistry.Entry entry : CraftConfigRegistry.all()) {
            String modId          = entry.modId();
            CraftConfig config    = entry.config();
            PresetManager manager = config.presetManager();

            String modDisplayName = ModList.get()
                    .getModContainerById(modId)
                    .map(c -> c.getModInfo().getDisplayName())
                    .orElse(modId);
            for (ConfigCategory cat : config.categories()) {
                for (ConfigSection sec : cat.sections()) {
                    for (ConfigOption<?> opt : sec.options()) {
                        ConfigKeybinds kb = opt.keybindSettings();
                        if (kb == null) continue;
						int keyCode = kb.defaultKey();
						InputConstants.Key key = keyCode == -1
								? InputConstants.UNKNOWN
								: InputConstants.getKey(keyCode, 0);
                        kb.setOptionName(opt.name().getString());
                        KeyMapping mapping = new KeyMapping(
                                opt.name().getString(),
                                KeyConflictContext.IN_GAME,
								key,
								"key.category.minecraft.entry." + entry.keybindCategory()
                        );
                        event.register(mapping);
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
				int keyCode = preset.keyBind();
				InputConstants.Key key = keyCode == -1
						? InputConstants.UNKNOWN
						: InputConstants.getKey(keyCode, 0);
                KeyMapping mapping = new KeyMapping(
                        preset.name(),
                        KeyConflictContext.IN_GAME,
						key,
						"key.category.minecraft." + entry.keybindCategory()
                );
                event.register(mapping);
                preset.setKeyMapping(mapping);

                KeybindRegistry.addPresetEntry(preset, manager, modDisplayName);
            }
        }
    }
}

*///?}
