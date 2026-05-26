package net.ray.CraftConfig.api.controller;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.ray.CraftConfig.api.v1.ConfigOption;


public class SliderController<T extends Number> implements OptionController<T> {

	private final double min;
	private final double max;
	private final boolean isInteger;
	private final boolean isFloat;

	public SliderController(T min, T max) {
		this.min = min.doubleValue();
		this.max = max.doubleValue();
		this.isInteger = min instanceof Integer;
		this.isFloat = min instanceof Float;
	}

	@Override
	@SuppressWarnings("shadow")
	public AbstractSliderButton createWidget(ConfigOption<T> option, int x, int y, int w, int h) {
		double current = clamp(option.get().doubleValue());
		double initial = (current - min) / (max - min);

		return new AbstractSliderButton(x, y, w, h, label(option, option.get()), initial) {
			@Override
			protected void updateMessage() {
				T val = sliderToValue(value);
				setMessage(label(option, val));
			}

			@Override
			protected void applyValue() {
				option.set(sliderToValue(value));
			}
		};
	}

	@SuppressWarnings("unchecked")
	private T sliderToValue(double sliderValue) {
		double calculated = min + sliderValue * (max - min);

		if (isInteger) {
			int intValue = (int) Math.round(calculated);
			intValue = Math.max((int) min, Math.min((int) max, intValue));
			return (T) Integer.valueOf(intValue);
		} else if (isFloat) {
			float floatValue = (float) Math.max(min, Math.min(max, calculated));
			// Round to 2 decimal places to avoid floating point issues
			floatValue = Math.round(floatValue * 100) / 100.0f;
			return (T) Float.valueOf(floatValue);
		} else {
			double doubleValue = Math.max(min, Math.min(max, calculated));
			return (T) Double.valueOf(doubleValue);
		}
	}

	private double clamp(double value) {
		return Math.max(min, Math.min(max, value));
	}

	private Component label(ConfigOption<T> opt, T val) {
		if (isInteger) {
			return Component.literal(opt.name().getString() + ": " + val.intValue());
		} else if (isFloat) {
			float rounded = Math.round(val.floatValue() * 100) / 100.0f;
			return Component.literal(opt.name().getString() + ": " + rounded);
		} else {
			double rounded = Math.round(val.doubleValue() * 100) / 100.0;
			return Component.literal(opt.name().getString() + ": " + rounded);
		}
	}
}
