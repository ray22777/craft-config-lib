package net.ray.CraftConfig.api.v1;

import net.minecraft.network.chat.Component;
import net.ray.CraftConfig.api.controller.OptionController;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ConfigOption<T> {

	public enum Type { BOOLEAN, INTEGER, DOUBLE, FLOAT, STRING, COLOR, ENUM, LIST }

	private final Component name;
	private final T defaultValue;
	private final Type type;
	private Component description;
	private OptionController<T> controller;
	private ConfigKeybinds keybindSettings;
	private T currentValue;
	private final List<Consumer<T>> changeListeners = new ArrayList<>();

	private ConfigOption(Component name, T defaultValue, Type type) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.type = type;
		this.currentValue = defaultValue;
	}
	public static ConfigOption<Boolean> booleanOption(Component name, boolean defaultValue) {
		return new ConfigOption<>(name, defaultValue, Type.BOOLEAN);
	}

	public static ConfigOption<Integer> intOption(Component name, int defaultValue) {
		return new ConfigOption<>(name, defaultValue, Type.INTEGER);
	}

	public static ConfigOption<Double> doubleOption(Component name, double defaultValue) {
		return new ConfigOption<>(name, defaultValue, Type.DOUBLE);
	}

	public static ConfigOption<Float> floatOption(Component name, float defaultValue) {
		return new ConfigOption<>(name, defaultValue, Type.FLOAT);
	}

	public static ConfigOption<String> stringOption(Component name, String defaultValue) {
		return new ConfigOption<>(name, defaultValue, Type.STRING);
	}

	public static ConfigOption<Color> colorOption(Component name, Color defaultValue) {
		return new ConfigOption<>(name, defaultValue, Type.COLOR);
	}

	public static <E extends Enum<E>> ConfigOption<E> enumOption(Component name, E defaultValue) {
		return new ConfigOption<>(name, defaultValue, Type.ENUM);
	}

	public static <V> ConfigOption<List<V>> listOption(Component name, List<V> defaultValue) {
		return new ConfigOption<>(name, (List<V>) defaultValue, Type.LIST);
	}

	public ConfigOption<T> description(Component description) {
		this.description = description;
		return this;
	}

	public ConfigOption<T> controller(OptionController<T> controller) {
		this.controller = controller;
		if (keybindSettings != null)
			controller.initCycleValues(this, keybindSettings);
		return this;
	}


	public ConfigOption<T> keybind(ConfigKeybinds keybindSettings) {
		this.keybindSettings = keybindSettings;
		keybindSettings.setOptionType(this.type);
		if (controller != null)
			controller.initCycleValues(this, keybindSettings);
		return this;
	}

	public ConfigOption<T> onChanged(Consumer<T> listener) {
		this.changeListeners.add(listener);
		return this;
	}

	public Component name() { return name; }
	public Component description() { return description; }
	public Type type() { return type; }
	public OptionController<T> controller() { return controller; }
	public ConfigKeybinds keybindSettings() { return keybindSettings; }
	public boolean isKeybindable() { return keybindSettings != null && keybindSettings.isEnabled(); }

	public T get() { return currentValue; }

	public void set(T value) {
		T oldValue = this.currentValue;
		if (!valuesEqual(oldValue, value)) {
			this.currentValue = value;
			changeListeners.forEach(l -> l.accept(value));
		}
	}

	public T getDefault() { return defaultValue; }
	public void reset() { set(defaultValue); }

	private boolean valuesEqual(T a, T b) {
		if (a == null && b == null) return true;
		if (a == null || b == null) return false;

		if (a instanceof List<?> listA && b instanceof List<?> listB) {
			if (listA.size() != listB.size()) return false;
			for (int i = 0; i < listA.size(); i++) {
				Object itemA = listA.get(i);
				Object itemB = listB.get(i);
				if (!Objects.equals(itemA, itemB)) return false;
			}
			return true;
		}

		return a.equals(b);
	}
}
