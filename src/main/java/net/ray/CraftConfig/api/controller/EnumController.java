package net.ray.CraftConfig.api.controller;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.ray.CraftConfig.api.v1.ConfigKeybinds;
import net.ray.CraftConfig.api.v1.ConfigOption;

import java.util.Arrays;

public class EnumController<E extends Enum<E>> implements OptionController<E> {

	@Override
	public Button createWidget(ConfigOption<E> option, int x, int y, int w, int h) {
		return Button.builder(label(option), btn -> {
			E[] values = (E[]) option.get().getClass().getEnumConstants();
			option.set(values[(option.get().ordinal() + 1) % values.length]);
			btn.setMessage(label(option));
		}).bounds(x, y, w, h).build();
	}

    private static <E extends Enum<E>> Component label(ConfigOption<E> opt) {
        String raw = opt.get().name().replace('_', ' ');
        String pretty = Character.toUpperCase(raw.charAt(0))
                + raw.substring(1).toLowerCase();
        return Component.literal(opt.get().name());
    }
	@Override
	public void initCycleValues(ConfigOption<E> option, ConfigKeybinds keybinds) {
		keybinds.setCycleValues(Arrays.asList(option.getDefault().getClass().getEnumConstants()));
	}
}
