//package net.ray.CraftConfig.api.v1;
//
//import net.minecraft.network.chat.Component;
//
//import java.util.*;
//
///** A collapsible group of options within a category. */
//public class ConfigSection {
//
//    private final String name;
//    private final Component description;
//    private final List<ConfigOption<?>> options;
//
//    private ConfigSection(Builder builder) {
//        this.name        = builder.name;
//        this.description = builder.description;
//        this.options     = builder.options;
//    }
//
//    public static Builder builder(String name) { return new Builder(name); }
//
//    public String name()                        { return name; }
//    public Component description()                   { return description; }
//    public List<ConfigOption<?>> options()      { return Collections.unmodifiableList(options); }
//
//    public static class Builder {
//        private final String name;
//        private Component description = Component.empty();
//        private final List<ConfigOption<?>> options = new ArrayList<>();
//
//        private Builder(String name) { this.name = name; }
//
//        public Builder description(Component description) { this.description = description; return this; }
//        public Builder option(ConfigOption<?> opt)   { options.add(opt); return this; }
////		public Builder option(ConfigOption.Builder<?> opt) {
////			options.add(opt.build());
////			return this;
////		}
//        public ConfigSection build()                  { return new ConfigSection(this); }
//    }
//}
package net.ray.CraftConfig.api.v1;

import net.minecraft.network.chat.Component;
import java.util.*;

public class ConfigSection {

	private final Component name;
	private final List<ConfigOption<?>> options;

	private ConfigSection(Builder builder) {
		this.name = builder.name;
		this.options = builder.options;
	}

	public static Builder builder(Component name) { return new Builder(name); }

	public Component name() { return name; }
	public List<ConfigOption<?>> options() { return Collections.unmodifiableList(options); }

	public static class Builder {
		private final Component name;
		private Component description = Component.empty();
		private final List<ConfigOption<?>> options = new ArrayList<>();

		private Builder(Component name) { this.name = name; }

		public Builder description(Component description) {
			this.description = description;
			return this;
		}

		public Builder option(ConfigOption<?> opt) {
			options.add(opt);
			return this;
		}

		public <T> ConfigOption<T> option(ConfigOption<T> opt, boolean mutable) {
			options.add(opt);
			return opt;
		}

		public <T> ConfigOption<T> add(ConfigOption<T> opt) {
			options.add(opt);
			return opt;
		}

		public ConfigSection build() {
			return new ConfigSection(this);
		}
	}
}
