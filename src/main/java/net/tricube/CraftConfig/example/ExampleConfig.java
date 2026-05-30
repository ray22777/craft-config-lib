package net.tricube.CraftConfig.example;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
//import net.minecraft.world.item.TieredItem;
import net.tricube.CraftConfig.api.v1.*;
import net.tricube.CraftConfig.api.controller.*;
import net.tricube.CraftConfig.api.controller.*;
import net.tricube.CraftConfig.api.registry.CraftConfigRegistry;
import net.tricube.CraftConfig.api.v1.*;
import net.tricube.CraftConfig.api.v1.entries.SelectionGridEntry;

import java.awt.*;
import java.util.List;

public class ExampleConfig {


	// Boolean toggle
	public static final ConfigOption<Boolean> exampleBoolean =
			ConfigOption.booleanOption(Component.literal("Boolean Toggle"), true)
					.description(Component.literal("An example boolean"));

	// Integer with slider
	public static final ConfigOption<Integer> exampleInteger =
			ConfigOption.intOption(Component.translatable("craft-config.exampleInteger.config"), 50)
					.description(Component.translatable("craft-config.exampleInteger.description")); //Using translation keys

	// String input
	public static final ConfigOption<String> stringInput =
			ConfigOption.stringOption(Component.literal("String Input"), "bob");

	// Color picker
	public static final ConfigOption<Color> exampleColor =
			ConfigOption.colorOption(Component.literal("Color Picker"), new Color(0xFF3498DB, true));

	// Enum dropdown
	public enum Quality { LOW, MEDIUM, HIGH }
	public static final ConfigOption<Quality> exampleEnum =
			ConfigOption.enumOption(Component.literal("Enum Example"), Quality.MEDIUM);

	// Cycle through string values, similar to enum
	public static final ConfigOption<String> exampleCyclingList =
			ConfigOption.stringOption(Component.literal("Cycling List Example"), "Normal");

	// String list
	public static final ConfigOption<List<String>> exampleList =
			ConfigOption.listOption(Component.literal("List Selection Example"), List.of("bob", "john")); //or leave empty with List.of()


	//Selection grid controllers:

	// example block selection
	public static  ConfigOption<List<String>> allowedBlocks =
			ConfigOption.<String>listOption(Component.literal("Allowed Blocks"), List.of());


	// example tool blacklist
	public static  ConfigOption<List<String>> blockedTools =
			ConfigOption.<String>listOption(Component.literal("Banned Tools"), List.of());


	//Boolean with keybind toggling:
	public static final ConfigOption<Boolean> toggleExample =
			ConfigOption.booleanOption(Component.literal("Example Boolean Toggle"), false);



	//building the config screen
	public static final CraftConfig config = CraftConfig.create("example_mod")
			.title(Component.literal("Example Config"))

			.category(ConfigCategory.builder(Component.literal("General"))
					.section(ConfigSection.builder(Component.literal("Section One"))
							.description(Component.literal("This is a section description"))
							.option(exampleBoolean.controller(new BooleanController()))
							.option(exampleInteger.controller(new SliderController<>(0, 100))) //set the range between 0-100
							.option(stringInput.controller(new InputFieldController<>()))
							.option(exampleColor.controller(new ColorController()))
							.build())
					.section(ConfigSection.builder(Component.literal("Another Section"))
							.option(exampleEnum.controller(new EnumController<>())
									.keybind(ConfigKeybinds.create()
											.defaultKey(InputConstants.KEY_G)
											.notify(true)))
							.option(exampleCyclingList.controller(new CycleController<>(List.of("Easy", "Normal", "Hard", "Expert")))
									.keybind(ConfigKeybinds.create()
											.defaultKey(InputConstants.KEY_F)
											.notify(true)))
							.option(exampleList.controller(new ListController<>(ListController.ElementType.STRING)))
							.build())
					.build())

			.category(ConfigCategory.builder(Component.literal("Filtering"))
					.section(ConfigSection.builder(Component.literal("Block Management"))
							.option(allowedBlocks
									.controller(new SelectionGridController(
											() -> SelectionGridEntry.forBlocks()
											.addAll(SelectionGridEntry.getAllBlockKeys())
											.sortAlphabetically()
											.build()
							)))
							.option(blockedTools
									.controller(new SelectionGridController(
											() -> SelectionGridEntry.forItems()
													.addAll(SelectionGridEntry.getAllItemKeys())
//													.filter(rl -> net.minecraft.core.registries.BuiltInRegistries.ITEM.get(rl) instanceof TieredItem) //filters out only tools
													.sortAlphabetically()
													.build()
											,true //sets blacklist
									)))
							.build())
					.build())
			.category(ConfigCategory.builder(Component.literal("Controlled"))
					.section(ConfigSection.builder(Component.literal("example"))
							.option(toggleExample
									.controller(new BooleanController())
									.keybind(ConfigKeybinds.create()
											.defaultKey(InputConstants.KEY_R)
											.mode(ConfigKeybinds.Mode.TOGGLE)
											.notify(true)))
							.build())
					.build())
			.build();

	public static void init() {
		config.load();
		CraftConfigRegistry.register("example_mod", config, "Example Mod")
				.setModMenuEnabled(true)
				.setCommandEnabled(true)
				.setKeybindCategory("example_mod") // Use language file to translate category, "key.category.minecraft." is appended in front to make it consistent with newer versions.
				.build();

	}

	//To get values from config, use ExampleConfig.exampleBoolean.get();   //this returns a boolean.
	//To set values to config, use ExampleConfig.exampleBoolean.set(true);
}
