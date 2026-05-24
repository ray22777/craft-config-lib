package net.ray.CraftConfig.platform.neoforge;

//? neoforge {

/*import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.fml.ModList;
import net.ray.CraftConfig.api.registry.CraftConfigRegistry;
import net.ray.CraftConfig.api.registry.KeybindRegistry;
import net.ray.CraftConfig.api.v1.*;
import net.ray.CraftConfig.preset.ConfigPreset;
import net.ray.CraftConfig.preset.PresetManager;

public class NeoforgeKeybindRegistry {

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

            // ── Config option keybinds ────────────────────────────────────────
            for (ConfigCategory cat : config.categories()) {
                for (ConfigSection sec : cat.sections()) {
                    for (ConfigOption<?> opt : sec.options()) {
                        if (opt.type() != ConfigOption.Type.BOOLEAN) continue;
                        ConfigKeybinds kb = opt.keybindSettings();
                        if (kb == null) continue;

                        kb.setOptionName(opt.name().getString());

                        KeyMapping mapping = new KeyMapping(
                                opt.name().getString(),
                                KeyConflictContext.IN_GAME,
                                InputConstants.getKey(kb.defaultKey(), -1),
                                entry.keybindCategory()
                        );
                        event.register(mapping);
                        kb.setKeyMapping(mapping);

                        KeybindRegistry.addEntry((ConfigOption<Boolean>) opt, kb, config);
                    }
                }
            }

            for (ConfigPreset preset : manager.presets()) {
                if (preset.isDefault()) continue;

                KeyMapping mapping = new KeyMapping(
                        preset.name(),
                        KeyConflictContext.IN_GAME,
                        InputConstants.getKey(preset.keyBind(), -1),
                        entry.keybindCategory()
                );
                event.register(mapping);
                preset.setKeyMapping(mapping);

                KeybindRegistry.addPresetEntry(preset, manager, modDisplayName);
            }
        }
    }
}

*///?}
