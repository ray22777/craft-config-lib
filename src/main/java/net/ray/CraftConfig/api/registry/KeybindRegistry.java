package net.ray.CraftConfig.api.registry;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.ray.CraftConfig.api.v1.*;
import net.ray.CraftConfig.config.Config;
import net.ray.CraftConfig.preset.ConfigPreset;
import net.ray.CraftConfig.preset.PresetManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class KeybindRegistry {

	public record BooleanEntry(ConfigOption<Boolean> option, ConfigKeybinds settings, CraftConfig config) {}
	public record CycleEntry(ConfigOption<?> option, ConfigKeybinds settings, CraftConfig config) {}
	public record PresetEntry(ConfigPreset preset, PresetManager manager, String modDisplayName) {}

	private static final List<BooleanEntry> booleanEntries = new ArrayList<>();
	private static final List<Boolean>      wasHeld        = new ArrayList<>();
	private static final List<CycleEntry>   cycleEntries   = new ArrayList<>();
	private static final List<PresetEntry>  presetEntries  = new ArrayList<>();

	private static final List<Consumer<ConfigOption<Boolean>>> changeListeners = new ArrayList<>();

	public static void addBooleanEntry(ConfigOption<Boolean> option, ConfigKeybinds settings, CraftConfig config) {
		booleanEntries.add(new BooleanEntry(option, settings, config));
		wasHeld.add(false);
	}

	public static void addCycleEntry(ConfigOption<?> option, ConfigKeybinds settings, CraftConfig config) {
		cycleEntries.add(new CycleEntry(option, settings, config));
	}

	public static void addPresetEntry(ConfigPreset preset, PresetManager manager, String modDisplayName) {
		presetEntries.add(new PresetEntry(preset, manager, modDisplayName));
	}


	public static void tick(Minecraft client) {
		tickBoolean(client);
		tickCycle(client);
		tickPresets(client);
	}

	private static void tickBoolean(Minecraft client) {
		for (int i = 0; i < booleanEntries.size(); i++) {
			BooleanEntry   e  = booleanEntries.get(i);
			ConfigKeybinds kb = e.settings();
			if (!kb.isEnabled() || kb.keyMapping() == null) continue;

			KeyMapping km       = kb.keyMapping();
			boolean    heldNow  = km.isDown();
			boolean    wasHeldPrev = wasHeld.get(i);
			boolean    changed  = false;

			switch (kb.mode()) {
				case TOGGLE -> {
					if (heldNow && !wasHeldPrev) {
						km.consumeClick();
						kb.triggerBoolean(e.option());
						if (kb.getNotify()) sendBooleanChat(client, e.option());
						changed = true;
					} else if (heldNow) {
						while (km.consumeClick()) {}
					}
				}
				case HOLD, HOLD_INVERTED -> {
					if (heldNow && !wasHeldPrev) {
						kb.triggerBoolean(e.option());
						if (kb.getNotify()) sendBooleanChat(client, e.option());
						changed = true;
					} else if (!heldNow && wasHeldPrev) {
						kb.triggerBooleanRelease(e.option());
						if (kb.getNotify()) sendBooleanChat(client, e.option());
						changed = true;
					}
				}
			}

			if (changed) {
				e.config().save();
				notifyChangeListeners(e.option());
			}
			wasHeld.set(i, heldNow);
		}
	}

	private static void tickCycle(Minecraft client) {
		for (CycleEntry e : cycleEntries) {
			ConfigKeybinds kb = e.settings();
			if (!kb.isEnabled() || kb.keyMapping() == null) continue;

			KeyMapping km = kb.keyMapping();
			boolean firedOnce = false;
			while (km.consumeClick()) {
				if (!firedOnce) {
					kb.triggerCycle(e.option());
					if (kb.getNotify()) sendCycleChat(client, e.option());
					e.config().save();
					firedOnce = true;
				}
			}
		}
	}

	private static void tickPresets(Minecraft client) {
		for (PresetEntry pe : presetEntries) {
			KeyMapping km = pe.preset().getKeyMapping();
			if (km == null) continue;
			while (km.consumeClick()) {
				if (pe.preset().id().equals(pe.manager().activePresetId())) break;
				pe.manager().switchTo(pe.preset());
				sendPresetChat(client, pe.preset(), pe.modDisplayName());
			}
		}
	}


	private static void sendBooleanChat(Minecraft client, ConfigOption<Boolean> opt) {

		if (client.player == null) return;
		boolean isHotbar = false;
		if(Config.keybindNotifier.get() == Config.NotifierType.HOTBAR){
			isHotbar = true;
		}
		Component displayMessage = Component.literal(Config.booleanFormat.get().replace("&","§").replace("{name}",opt.name().getString()).replace("{value}",opt.get() ? "§aON" : "§cOFF"));
		//? if >= 26.1{
		/*if(isHotbar){
			client.player.sendOverlayMessage(displayMessage);
		} else{
			client.player.sendSystemMessage(displayMessage);
		}
		*///?}else{
			client.player.displayClientMessage(displayMessage, isHotbar);
		//?}





	}

	private static void sendCycleChat(Minecraft client, ConfigOption<?> opt) {
		if (client.player == null) return;
		boolean isHotbar = false;
		if(Config.keybindNotifier.get() == Config.NotifierType.HOTBAR){
			isHotbar = true;
		}
		Component displayMessage = Component.literal(Config.cyclingFormat.get().replace("&","§").replace("{name}",opt.name().getString()).replace("{value}", opt.get().toString()));
		//? if >= 26.1{
		/*if(isHotbar){
			client.player.sendOverlayMessage(displayMessage);
		} else{
			client.player.sendSystemMessage(displayMessage);
		}
		*///?}else{
		client.player.displayClientMessage(displayMessage, isHotbar);
		//?}
	}

	private static void sendPresetChat(Minecraft client, ConfigPreset preset, String modDisplayName) {
		if (client.player == null) return;
		boolean isHotbar = false;
		if(Config.keybindNotifier.get() == Config.NotifierType.HOTBAR){
			isHotbar = true;
		}
		Component displayMessage = Component.literal(Config.presetFormat.get().replace("&","§").replace("{mod}",modDisplayName).replace("{preset}", preset.name()));
		//? if >= 26.1{
		/*if(isHotbar){
			client.player.sendOverlayMessage(displayMessage);
		} else{
			client.player.sendSystemMessage(displayMessage);
		}
		*///?}else{
		client.player.displayClientMessage(displayMessage, isHotbar);
		//?}
	}

	public static void notifyChangeListeners(ConfigOption<Boolean> option) {
		for (Consumer<ConfigOption<Boolean>> l : changeListeners) l.accept(option);
	}
}
