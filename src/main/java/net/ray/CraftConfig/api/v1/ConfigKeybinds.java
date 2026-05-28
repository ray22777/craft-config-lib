package net.ray.CraftConfig.api.v1;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

import java.util.List;
import java.util.function.Consumer;

public class ConfigKeybinds {

	public enum Mode { TOGGLE, HOLD, HOLD_INVERTED }
	private boolean  enabled        = true;
	private int      defaultKey     = InputConstants.UNKNOWN.getValue();
	private boolean  notify = true;
	private Mode              mode      = Mode.TOGGLE;
	private Consumer<Object> onChanged = null;
	private KeyMapping              keyMapping  = null;
	private String                  optionName  = null;
	private ConfigOption.Type       optionType  = null;
	private ConfigKeybinds() {}
	public static ConfigKeybinds create()               { return new ConfigKeybinds(); }
	public static ConfigKeybinds of(int key)            { return new ConfigKeybinds().defaultKey(key); }
	public static ConfigKeybinds of(int key, Mode mode) { return new ConfigKeybinds().defaultKey(key).mode(mode); }
	public ConfigKeybinds enabled(boolean v)         { this.enabled = v;        return this; }
	public ConfigKeybinds defaultKey(int key)        { this.defaultKey = key;   return this; }
	public ConfigKeybinds notify(boolean v)  { this.notify = v; return this; }
	public ConfigKeybinds mode(Mode mode)                  { this.mode = mode;    return this; }
	public ConfigKeybinds onChanged(Consumer<Object> cb) { this.onChanged = cb; return this; }

	public void setEnabled(boolean v)               { this.enabled = v; }
	public void setDefaultKey(int key)              { this.defaultKey = key; }
	public void setNotify(boolean v)        { this.notify = v; }
	public void setMode(Mode mode)                  { this.mode = mode; }
	public void setOnChanged(Consumer<Object> cb) { this.onChanged = cb; }
	public void setKeyMapping(KeyMapping km)        { this.keyMapping = km; }
	public void setOptionName(String name)          { this.optionName = name; }
	public void setOptionType(ConfigOption.Type t)  { this.optionType = t; }

	private List<Object> cycleValues = null;

	@SuppressWarnings("unchecked")
	public <T> void setCycleValues(List<T> values) {
		this.cycleValues = (List<Object>) values;
	}

	public boolean            isEnabled()      { return enabled; }
	public int                defaultKey()     { return defaultKey; }
	public boolean            getNotify() 	   { return notify; }
	public Mode               mode()           { return mode; }
	public Consumer<Object> onChanged() { return onChanged; }
	public KeyMapping         keyMapping()     { return keyMapping; }
	public String             optionName()     { return optionName; }
	public ConfigOption.Type  optionType()     { return optionType; }

	public boolean isBoolean() { return optionType == ConfigOption.Type.BOOLEAN; }
	public boolean isCycle()   { return optionType == ConfigOption.Type.ENUM
			|| optionType == ConfigOption.Type.LIST; }

	public void triggerBoolean(ConfigOption<Boolean> option) {
		if (!enabled || keyMapping == null) return;
		boolean newVal = switch (mode) {
			case TOGGLE        -> !option.get();
			case HOLD          -> true;
			case HOLD_INVERTED -> false;
		};
		option.set(newVal);
		if (onChanged != null) onChanged.accept(newVal);
	}

	public void triggerBooleanRelease(ConfigOption<Boolean> option) {
		if (!enabled || keyMapping == null) return;
		if (mode == Mode.HOLD) {
			option.set(false);
			if (onChanged != null) onChanged.accept(false);
		} else if (mode == Mode.HOLD_INVERTED) {
			option.set(true);
			if (onChanged != null) onChanged.accept(true);
		}
	}


	@SuppressWarnings("unchecked")
	public <T> void triggerCycle(ConfigOption<T> option) {
		if (!enabled || keyMapping == null || cycleValues == null) return;
		int idx = cycleValues.indexOf(option.get());
		option.set((T) cycleValues.get((idx + 1) % cycleValues.size()));
	}

}
