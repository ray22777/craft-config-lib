package net.ray.CraftConfig.api.registry;
//?if fabric {
/*import com.terraformersmc.modmenu.api.ConfigScreenFactory;
*///? }

import net.minecraft.network.chat.Component;
import net.ray.CraftConfig.api.v1.CraftConfig;

import java.util.*;

public class CraftConfigRegistry {

	public static class RegistrationBuilder {
		private final String modId;
		private final CraftConfig config;
		private boolean modMenuEnabled = false;
		private boolean commandEnabled = false;
		private String customCommand = null;
		private String keybindCategory = null;
		private final String modDisplayName;

		private RegistrationBuilder(String modId, CraftConfig config) {
			this.modId = modId;
			this.config = config;
			this.modDisplayName = modId;
		}
		private RegistrationBuilder(String modId, CraftConfig config, String modDisplayName) {
			this.modId = modId;
			this.config = config;
			this.modDisplayName = modDisplayName;
			this.keybindCategory = modDisplayName.replace(" ","_").toLowerCase();
		}

		public RegistrationBuilder setModMenuEnabled(boolean enabled) {
			this.modMenuEnabled = enabled;
			return this;
		}

		public RegistrationBuilder setCommandEnabled(boolean enabled) {
			this.commandEnabled = enabled;
			return this;
		}

		public RegistrationBuilder setCustomCommand(String command) {
			this.customCommand = command;
			return this;
		}

		public RegistrationBuilder setKeybindCategory(String category) {
			this.keybindCategory = category; //Please ensure that there is no space, and follows the regex [a-z0-9/._-].Make sure you use translation keys for versions >1.21.9
			return this;
		}

		public void build() {
			if (keybindCategory == null) {
				keybindCategory = modDisplayName.toLowerCase().replace(" ","_");
			}
			if (entries.containsKey(modId))
				throw new IllegalStateException("[CraftConfig] '" + modId + "' is already registered.");
			entries.put(modId, new Entry(modId, config, modMenuEnabled, commandEnabled, customCommand, keybindCategory));
		}
	}

	public static class Entry {
		private final String      modId;
		private final CraftConfig config;
		private final boolean     modMenuEnabled;
		private final boolean     commandEnabled;
		private final String      customCommand;
		private final String   keybindCategory;

		private Entry(String modId, CraftConfig config, boolean modMenuEnabled,
					  boolean commandEnabled, String customCommand, String keybindCategory) {
			this.modId = modId;
			this.config = config;
			this.modMenuEnabled = modMenuEnabled;
			this.commandEnabled = commandEnabled;
			this.customCommand = customCommand;
			this.keybindCategory = keybindCategory;
		}

		public String   keybindCategory() { return keybindCategory; }
		public String      modId()          { return modId; }
		public CraftConfig config()         { return config; }
		public boolean     modMenuEnabled() { return modMenuEnabled; }
		public boolean     commandEnabled() { return commandEnabled; }
		public String      customCommand()  { return customCommand; }
	}

	private static final Map<String, Entry> entries = new LinkedHashMap<>();

	public static RegistrationBuilder register(String modId, CraftConfig config) {
		return new RegistrationBuilder(modId, config);
	}
	public static RegistrationBuilder register(String modId, CraftConfig config,String modDisplayName) {
		return new RegistrationBuilder(modId, config, modDisplayName);
	}

	public static Collection<Entry> all() {
		return Collections.unmodifiableCollection(entries.values());
	}

	public static Optional<Entry> get(String modId) {
		return Optional.ofNullable(entries.get(modId));
	}
	//?if fabric {
	/*public static Map<String, ConfigScreenFactory<?>> getConfigScreenFactories() {
		Map<String, ConfigScreenFactory<?>> factories = new LinkedHashMap<>();
		for (Entry e : entries.values()) {
			if (!e.modMenuEnabled()) continue;
			CraftConfig cfg = e.config();
			factories.put(e.modId(), parent -> cfg.createScreen(parent));
		}
		return factories;
	}
 	*///?}
	public static Map<String, CraftConfig> getConfigsForRegistration() {
		Map<String, CraftConfig> configs = new LinkedHashMap<>();
		for (Entry e : entries.values()) {
			if (e.modMenuEnabled()) {
				configs.put(e.modId(), e.config());
			}
		}
		return configs;
	}
}
