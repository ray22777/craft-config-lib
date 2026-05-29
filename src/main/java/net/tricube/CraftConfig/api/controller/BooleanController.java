package net.tricube.CraftConfig.api.controller;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.tricube.CraftConfig.api.v1.ConfigOption;

public class BooleanController implements OptionController<Boolean> {

	public static final BooleanController INSTANCE = new BooleanController();

	@Override
	public Button createWidget(ConfigOption<Boolean> option, int x, int y, int w, int h) {
		return Button.builder(
						buttonLabel(option),
						btn -> {
							option.set(!option.get());
							btn.setMessage(buttonLabel(option));
						})
				.bounds(x, y, w, h)
				.build();
	}

	private static Component buttonLabel(ConfigOption<Boolean> opt) {
		return Component.literal(opt.get() ? "§aON" : "§cOFF");
	}
}
