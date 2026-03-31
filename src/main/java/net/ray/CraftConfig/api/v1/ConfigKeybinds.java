package net.ray.CraftConfig.api.v1;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

import java.util.function.Consumer;

public class ConfigKeybinds {

	public enum Mode {
		TOGGLE,
		HOLD,
		HOLD_INVERTED
	}

	// ── Fields ────────────────────────────────────────────────────────────────

	private boolean          enabled        = true;
	private int              defaultKey     = InputConstants.UNKNOWN.getValue(); // -1 = unbound
	private Mode             mode           = Mode.TOGGLE;
	private boolean          sendChatMessage = true;
	private Consumer<Boolean> onChanged     = null;

	private KeyMapping keyMapping  = null;
	private String     optionName  = null;

	private ConfigKeybinds() {}

	public static ConfigKeybinds create()              { return new ConfigKeybinds(); }
	public static ConfigKeybinds of(int key)           { return new ConfigKeybinds().defaultKey(key); }
	public static ConfigKeybinds of(int key, Mode mode){ return new ConfigKeybinds().defaultKey(key).mode(mode); }

	public ConfigKeybinds enabled(boolean v)              { this.enabled = v;         return this; }
	public ConfigKeybinds defaultKey(int key)             { this.defaultKey = key;    return this; }
	public ConfigKeybinds mode(Mode mode)                 { this.mode = mode;         return this; }
	public ConfigKeybinds sendChatMessage(boolean v)      { this.sendChatMessage = v; return this; }
	public ConfigKeybinds onChanged(Consumer<Boolean> cb) { this.onChanged = cb;      return this; }

	public void setEnabled(boolean v)              { this.enabled = v; }
	public void setDefaultKey(int key)             { this.defaultKey = key; }  // used by JsonSerializer
	public void setMode(Mode mode)                 { this.mode = mode; }
	public void setSendChatMessage(boolean v)      { this.sendChatMessage = v; }
	public void setOnChanged(Consumer<Boolean> cb) { this.onChanged = cb; }

	public void setKeyMapping(KeyMapping km) { this.keyMapping = km; }
	public void setOptionName(String name)   { this.optionName = name; }

	public boolean           isEnabled()       { return enabled; }
	public int               defaultKey()      { return defaultKey; }
	public Mode              mode()            { return mode; }
	public boolean           sendChatMessage() { return sendChatMessage; }
	public Consumer<Boolean> onChanged()       { return onChanged; }
	public KeyMapping        keyMapping()      { return keyMapping; }
	public String            optionName()      { return optionName; }

	public void trigger(ConfigOption<Boolean> option) {
		if (!enabled || keyMapping == null) return;
		boolean newVal = switch (mode) {
			case TOGGLE        -> !option.get();
			case HOLD          -> true;
			case HOLD_INVERTED -> false;
		};
		option.set(newVal);
		if (onChanged != null) onChanged.accept(newVal);
	}

	public void triggerRelease(ConfigOption<Boolean> option) {
		if (!enabled || keyMapping == null) return;
		if (mode == Mode.HOLD) {
			option.set(false);
			if (onChanged != null) onChanged.accept(false);
		} else if (mode == Mode.HOLD_INVERTED) {
			option.set(true);
			if (onChanged != null) onChanged.accept(true);
		}
	}
}
