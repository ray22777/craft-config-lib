package net.tricube.CraftConfig.api.controller;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.tricube.CraftConfig.api.v1.ConfigOption;
import net.tricube.CraftConfig.gui.components.ColorButton;
import net.tricube.CraftConfig.gui.subscreens.ColorPickerScreen;

import java.awt.*;

public class ColorController implements OptionController<Color> {

	@Override
	public AbstractWidget createWidget(ConfigOption<Color> option, int x, int y, int w, int h) {
		Color currentColor = option.get() != null ? option.get() : new Color(0xFFFFFFFF, true);
		int colorValue = currentColor.getRGB();

		return ColorButton.colorBuilder(
						Component.literal(String.format("#%08X", colorValue) + "  "),
						() -> Minecraft.getInstance().setScreen(new ColorPickerScreen(
								//~ if >=26.2 '.screen' -> '.gui.screen()'
								Minecraft.getInstance().screen,
								option.name(),
								currentColor,
								newValue -> {
									option.set(newValue);
								}
						))
				).bounds(x, y, w, h)
				.previewColor(colorValue)
				.previewPos(w/2 + 28,(h - 12) / 2)
				.previewSize(12)
				.build();
	}
}

