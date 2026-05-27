package net.ray.CraftConfig.platform.neoforge;

//? neoforge {

/*import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
//?if>=1.21.9
//import net.minecraft.resources.ResourceLocation;
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
			//?if>=1.21.9{
			/^KeyMapping.Category category = KeyMapping.Category.register(
					ResourceLocation.withDefaultNamespace(entry.keybindCategory()));
			^///?}
            for (ConfigCategory cat : config.categories()) {
                for (ConfigSection sec : cat.sections()) {
                    for (ConfigOption<?> opt : sec.options()) {
                        ConfigKeybinds kb = opt.keybindSettings();
                        if (kb == null) continue;
						//?if <1.21.11{
						int keyCode = kb.defaultKey();
						InputConstants.Key key = keyCode == -1
								? InputConstants.UNKNOWN
								: InputConstants.getKey(keyCode, 0);
						//?}
                        kb.setOptionName(opt.name().getString());
                        KeyMapping mapping = new KeyMapping(
                                opt.name().getString(),
                                KeyConflictContext.IN_GAME,
								//?if >=1.21.11{
								/^InputConstants.Type.KEYSYM.getOrCreate(kb.defaultKey()),
								^///?}else{
								key,
								//?}
								//?if>=1.21.9{
								/^category
								^///?}else{
								"key.category.minecraft." + entry.keybindCategory()
								 //?}
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
				//?if <1.21.11{
				int keyCode = preset.keyBind();
				InputConstants.Key key = keyCode == -1
						? InputConstants.UNKNOWN
						: InputConstants.getKey(keyCode, 0);
				//?}
                KeyMapping mapping = new KeyMapping(
                        preset.name(),
                        KeyConflictContext.IN_GAME,
						//?if>=1.21.11{
						/^InputConstants.Type.KEYSYM.getOrCreate(preset.keyBind()),
						^///?}else{
						key,
						//?}
						//?if>=1.21.9{
						/^category
						^///?}else{
						"key.category.minecraft." + entry.keybindCategory()
						//?}
                );
                event.register(mapping);
                preset.setKeyMapping(mapping);

                KeybindRegistry.addPresetEntry(preset, manager, modDisplayName);
            }
        }
    }
}

*///?}
