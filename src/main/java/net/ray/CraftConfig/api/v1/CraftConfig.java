package net.ray.CraftConfig.api.v1;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.ray.CraftConfig.gui.ConfigScreen;
import net.ray.CraftConfig.preset.PresetManager;

import java.lang.reflect.Type;
import java.util.*;


public class CraftConfig {

    private final String modId;
    private final Component title;
    private final List<ConfigCategory> categories;
    private final PresetManager presetManager;

    private CraftConfig(Builder builder) {
        this.modId          = builder.modId;
        this.title          = builder.title;
        this.categories     = builder.categories;
        this.presetManager  = new PresetManager(this, modId);
    }

    public static Builder create(String modId) { return new Builder(modId); }

    public void load() {
        presetManager.load();
    }
    public void save() {
        presetManager.saveCurrentValuesIntoActivePreset();
    }
    public Screen createScreen(Screen parent) {
        return new ConfigScreen(parent, this);
    }

    public Component title()                      { return title; }
    public List<ConfigCategory> categories()      { return Collections.unmodifiableList(categories); }
    public PresetManager presetManager()           { return presetManager; }


    public static class Builder {
        private final String modId;
        private Component title = Component.literal("Config");
        private final List<ConfigCategory> categories = new ArrayList<>();

        private Builder(String modId) { this.modId = modId; }

        public Builder title(Component title)         { this.title = title; return this; }
        public Builder category(ConfigCategory cat)   { categories.add(cat); return this; }
        public CraftConfig build()                     { return new CraftConfig(this); }
    }
}
