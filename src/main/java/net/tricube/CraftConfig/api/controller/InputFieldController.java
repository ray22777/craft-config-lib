package net.tricube.CraftConfig.api.controller;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
//? if>=26.1{
/*import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.ChatFormatting;
*///?}
import net.tricube.CraftConfig.api.v1.ConfigOption;

public class InputFieldController<T> implements OptionController<T> {

	@Override
	@SuppressWarnings("unchecked")
	public EditBox createWidget(ConfigOption<T> option, int x, int y, int w, int h) {
		EditBox field = new EditBox(Minecraft.getInstance().font, x, y, w, h, option.name());
		field.setMaxLength(256);

		T currentValue = option.get();
		field.setValue(currentValue != null ? String.valueOf(currentValue) : "");

		//? if >=26.1 {
				/*if (option.type() == ConfigOption.Type.INTEGER) {
					field.addFormatter((text, offset) -> text.matches("-?\\d*") ? null : FormattedCharSequence.forward(text, Style.EMPTY.withColor(ChatFormatting.RED)));
				} else if (option.type() == ConfigOption.Type.DOUBLE || option.type() == ConfigOption.Type.FLOAT) {
					field.addFormatter((text, offset) -> text.matches("-?\\d*\\.?\\d*") ? null : FormattedCharSequence.forward(text, Style.EMPTY.withColor(ChatFormatting.RED)));
				}
		*///?} else {
				if (option.type() == ConfigOption.Type.INTEGER) {
					field.setFilter(s -> s.matches("-?\\d*"));
				} else if (option.type() == ConfigOption.Type.DOUBLE || option.type() == ConfigOption.Type.FLOAT) {
					field.setFilter(s -> s.matches("-?\\d*\\.?\\d*"));
				}
		//?}

		field.setResponder(s -> {
			try {
				switch (option.type()) {
					case INTEGER -> {
						if (s.isEmpty() || s.equals("-")) {
							return;
						}
						int value = Integer.parseInt(s.trim());
						option.set((T) Integer.valueOf(value));
					}
					case DOUBLE -> {
						if (s.isEmpty() || s.equals("-") || s.equals(".") || s.equals("-.")) {
							return;
						}
						double value = Double.parseDouble(s.trim());
						option.set((T) Double.valueOf(value));
					}
					case FLOAT -> {
						if (s.isEmpty() || s.equals("-") || s.equals(".") || s.equals("-.")) {
							return;
						}
						float value = Float.parseFloat(s.trim());
						option.set((T) Float.valueOf(value));
					}
					case STRING -> {
						option.set((T) s);
					}
					default -> {
						option.set((T) s);
					}
				}
			} catch (NumberFormatException ignored) {
			}
		});

		return field;
	}
}
