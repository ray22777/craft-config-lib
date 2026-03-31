package net.ray.CraftConfig.api.registry;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.ray.CraftConfig.api.v1.*;
import net.ray.CraftConfig.preset.ConfigPreset;
import net.ray.CraftConfig.preset.PresetManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class KeybindRegistry {


    public record Entry(ConfigOption<Boolean> option, ConfigKeybinds settings, CraftConfig config) {}

    private static final List<Entry>   entries = new ArrayList<>();
    private static final List<Boolean> wasHeld = new ArrayList<>();

    private static final List<Consumer<ConfigOption<Boolean>>> changeListeners = new ArrayList<>();

    public static void addEntry(ConfigOption<Boolean> option, ConfigKeybinds settings, CraftConfig config) {
        entries.add(new Entry(option, settings, config));
        wasHeld.add(false);
    }

    public record PresetEntry(ConfigPreset preset, PresetManager manager, String modDisplayName) {}

    private static final List<PresetEntry> presetEntries = new ArrayList<>();
    public static void addPresetEntry(ConfigPreset preset, PresetManager manager, String modDisplayName) {
        presetEntries.add(new PresetEntry(preset, manager, modDisplayName));
    }

    public static void tick(Minecraft client) {

        for (int i = 0; i < entries.size(); i++) {
            Entry e = entries.get(i);
            ConfigKeybinds kb = e.settings();
            if (!kb.isEnabled() || kb.keyMapping() == null) continue;

            KeyMapping km       = kb.keyMapping();
            boolean heldNow     = km.isDown();
            boolean wasHeldPrev = wasHeld.get(i);
            boolean changed     = false;

            switch (kb.mode()) {
                case TOGGLE -> {
                    while (km.consumeClick()) {
                        boolean newVal = !e.option().get();
                        e.option().set(newVal);
                        if (kb.onChanged() != null) kb.onChanged().accept(newVal);
                        if (kb.sendChatMessage()) sendOptionChat(client, e.option(), newVal);
                        changed = true;
                    }
                }
                case HOLD -> {
                    if (heldNow && !wasHeldPrev) {
                        e.option().set(true);
                        if (kb.onChanged() != null) kb.onChanged().accept(true);
                        if (kb.sendChatMessage()) sendOptionChat(client, e.option(), true);
                        changed = true;
                    } else if (!heldNow && wasHeldPrev) {
                        e.option().set(false);
                        if (kb.onChanged() != null) kb.onChanged().accept(false);
                        if (kb.sendChatMessage()) sendOptionChat(client, e.option(), false);
                        changed = true;
                    }
                }
                case HOLD_INVERTED -> {
                    if (heldNow && !wasHeldPrev) {
                        e.option().set(false);
                        if (kb.onChanged() != null) kb.onChanged().accept(false);
                        if (kb.sendChatMessage()) sendOptionChat(client, e.option(), false);
                        changed = true;
                    } else if (!heldNow && wasHeldPrev) {
                        e.option().set(true);
                        if (kb.onChanged() != null) kb.onChanged().accept(true);
                        if (kb.sendChatMessage()) sendOptionChat(client, e.option(), true);
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
    public static void notifyChangeListeners(ConfigOption<Boolean> option) {
        for (Consumer<ConfigOption<Boolean>> l : changeListeners) l.accept(option);
    }

    private static void sendOptionChat(Minecraft client, ConfigOption<Boolean> opt, boolean val) {
        if (client.player != null) {
			//~ if >=26.1 'displayClientMessage' -> 'sendOverlayMessage'
            client.player.displayClientMessage(
                    Component.literal("§8[§7" + opt.name().getString() + "§8] §f- §r"
                            + (val ? "§aON" : "§cOFF"))
					//?if <26.1
                    ,true
            );
        }
    }

    private static void sendPresetChat(Minecraft client, ConfigPreset preset, String modDisplayName) {
        if (client.player != null) {
			//~ if >=26.1 'displayClientMessage' -> 'sendOverlayMessage'
            client.player.displayClientMessage(
                    Component.literal("§8[§7" + modDisplayName + "§8] §f→ §a" + preset.name())
					//?if <26.1
					,true
            );
        }
    }
}
