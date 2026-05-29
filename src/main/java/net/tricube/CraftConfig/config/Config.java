package net.tricube.CraftConfig.config;

import net.minecraft.network.chat.Component;
import net.tricube.CraftConfig.api.registry.CraftConfigRegistry;
import net.tricube.CraftConfig.api.v1.ConfigCategory;
import net.tricube.CraftConfig.api.v1.ConfigOption;
import net.tricube.CraftConfig.api.v1.ConfigSection;
import net.tricube.CraftConfig.api.v1.CraftConfig;

public class Config {





	public enum NotifierType {HOTBAR,CHAT}
	public static final ConfigOption<NotifierType> keybindNotifier =
			ConfigOption.enumOption(Component.literal("Keybind Notifier Type"), NotifierType.HOTBAR)
					.description(Component.literal("Where to send notification on keybind press."));

	public static final ConfigOption<String> booleanFormat =
			ConfigOption.stringOption(Component.literal("Boolean formatting"), "&8[&7{name}&8] &7→ &r {value}")
					.description(Component.literal("Set message formatting for boolean type.\nAvailable placeholders: {name} {value}"));

	public static final ConfigOption<String> cyclingFormat =
			ConfigOption.stringOption(Component.literal("Cycling List formatting"), "&8[&7{name}&8] &7→ &r {value}")
					.description(Component.literal("Set message formatting for cycling list.\nAvailable placeholders: {name} {value}"));

	public static final ConfigOption<String> presetFormat =
			ConfigOption.stringOption(Component.literal("Preset formatting"), "&8[&7{mod}&8] &7→ &r {preset}")
					.description(Component.literal("Set message formatting preset switching.\nAvailable placeholders: {mod} {preset}"));


	//building the config screen
	public static final CraftConfig config = CraftConfig.create("craft_config")
			.title(Component.literal("Craft Config Lib Configuration"))

			.category(ConfigCategory.builder(Component.literal("General"))
					.section(ConfigSection.builder(Component.literal("Keybind Notification"))
							.option(keybindNotifier)
							.option(booleanFormat)
							.option(cyclingFormat)
							.option(presetFormat)
							.build())
					.build())
			.build();

	public static void init() {
		config.load();
		CraftConfigRegistry.register("craft_config", config, "Craft Config")
				.setModMenuEnabled(true)
				.setCommandEnabled(true)
				.setCustomCommand("craftconfig")
//				.setKeybindCategory("key.category.minecraft.example_config")
				.build();

	}

	//To get values from config, use ExampleConfig.exampleBoolean.get();   //this returns a boolean.
	//To set values to config, use ExampleConfig.exampleBoolean.set(true);
}
