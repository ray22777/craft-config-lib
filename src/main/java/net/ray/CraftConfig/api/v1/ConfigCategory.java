package net.ray.CraftConfig.api.v1;

import net.minecraft.network.chat.Component;

import java.util.*;

public class ConfigCategory {

    private final Component name;
    private final List<ConfigSection> sections;

    private ConfigCategory(Builder builder) {
        this.name     = builder.name;
        this.sections = builder.sections;
    }

    public static Builder builder(Component name) { return new Builder(name); }

    public Component name()                       { return name; }
    public List<ConfigSection> sections()      { return Collections.unmodifiableList(sections); }

    public static class Builder {
        private final Component name;
        private Component tooltip = Component.empty();
        private final List<ConfigSection> sections = new ArrayList<>();

        private Builder(Component name) { this.name = name; }

        public Builder section(ConfigSection s) { sections.add(s); return this; }
        public ConfigCategory build()           { return new ConfigCategory(this); }
    }
}
