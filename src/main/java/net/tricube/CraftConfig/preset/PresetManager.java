package net.tricube.CraftConfig.preset;

import com.google.gson.*;
import com.mojang.blaze3d.platform.InputConstants;
import net.tricube.CraftConfig.api.v1.ConfigCategory;
import net.tricube.CraftConfig.api.v1.ConfigOption;
import net.tricube.CraftConfig.api.v1.ConfigSection;
import net.tricube.CraftConfig.api.v1.CraftConfig;
import net.tricube.CraftConfig.platform.CraftConfigMod;
import net.tricube.CraftConfig.serialization.JsonSerializer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class PresetManager {

	private final CraftConfig config;
	private final String modId;

	private final Path configDir;
	private final Path indexFile;
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private final List<ConfigPreset> presets = new ArrayList<>();
	private String activePresetId;

	public PresetManager(CraftConfig config, String modId) {
		this.config    = config;
		this.modId     = modId;
		this.configDir = Path.of("config", modId);
		this.indexFile = configDir.resolve("_presets.json");
	}

	public void load() {
		presets.clear();

		if (!Files.exists(indexFile)) {
			createDefaultAndSave();
			return;
		}

		try (Reader r = Files.newBufferedReader(indexFile)) {
			JsonObject root = gson.fromJson(r, JsonObject.class);
			if (root == null) throw new JsonParseException("empty index file");

			activePresetId = root.has("activePresetId") && !root.get("activePresetId").isJsonNull()
					? root.get("activePresetId").getAsString() : null;

			JsonArray presetArray = root.has("presets") && root.get("presets").isJsonArray()
					? root.getAsJsonArray("presets") : new JsonArray();

			for (JsonElement el : presetArray) {
				try {
					JsonObject obj = el.getAsJsonObject();
					if (!obj.has("id") || !obj.has("name") || !obj.has("scope")) continue; // skip malformed entry

					String id = obj.get("id").getAsString();
					String name = obj.get("name").getAsString();
					ConfigPreset.Scope scope = ConfigPreset.Scope.valueOf(obj.get("scope").getAsString());
					int keyBind = obj.has("keyBind") ? obj.get("keyBind").getAsInt() : InputConstants.UNKNOWN.getValue();

					List<String> worlds = new ArrayList<>();
					if (obj.has("worldIds") && obj.get("worldIds").isJsonArray()) {
						for (JsonElement w : obj.getAsJsonArray("worldIds")) worlds.add(w.getAsString());
					}

					JsonObject values = new JsonObject();
					Path valuesFile = presetFile(name);
					if (Files.exists(valuesFile)) {
						try (Reader vr = Files.newBufferedReader(valuesFile)) {
							JsonObject parsed = gson.fromJson(vr, JsonObject.class);
							if (parsed != null) values = parsed;
						} catch (Exception e) { // was IOException-only — same bug, same fix
							CraftConfigMod.LOGGER.warn("[CraftConfig] Failed to read values for preset '" + name + "': " + e.getMessage());
						}
					}
					ConfigPreset preset = new ConfigPreset(id, name, scope, worlds, values);
					preset.setKeyBind(keyBind);
					presets.add(preset);
				} catch (Exception e) {
					CraftConfigMod.LOGGER.warn("[CraftConfig] Skipping corrupted preset entry: " + e.getMessage());
				}
			}

			ensureIntegrity();
			getActive().ifPresent(this::applyToConfig);

		} catch (Exception e) {
			CraftConfigMod.LOGGER.error("[CraftConfig] Presets file corrupted, backing up and resetting: " + e.getMessage());
			backupCorruptFile(indexFile);
			createDefaultAndSave();
		}
	}

	private void createDefaultAndSave() {
		ConfigPreset def = new ConfigPreset("Default", ConfigPreset.Scope.DEFAULT);
		def.setValues(snapshotCurrentValues());
		presets.add(def);
		activePresetId = def.id();
		saveAll();
	}

	private void backupCorruptFile(Path file) {
		try {
			if (Files.exists(file)) {
				Files.move(file, file.resolveSibling(file.getFileName() + ".corrupt-" + System.currentTimeMillis()),
						StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException ignored) {}
	}
	public void saveAll() {
		try {
			Files.createDirectories(configDir);

			JsonObject root = new JsonObject();
			root.addProperty("activePresetId", activePresetId);

			JsonArray arr = new JsonArray();
			for (ConfigPreset p : presets) {
				JsonObject obj = new JsonObject();
				obj.addProperty("id", p.id());
				obj.addProperty("name", p.name());
				obj.addProperty("scope", p.scope().name());

				if (p.keyBind() != InputConstants.UNKNOWN.getValue()) {
					obj.addProperty("keyBind", p.keyBind());
				}

				JsonArray worlds = new JsonArray();
				for (String w : p.worldIds()) worlds.add(w);
				obj.add("worldIds", worlds);
				arr.add(obj);
			}
			root.add("presets", arr);

			writeJson(indexFile, root);

			for (ConfigPreset p : presets) {
				writeJson(presetFile(p.name()), p.values());
			}
		} catch (IOException e) {
			System.err.println("[CraftConfig] Failed to save presets: " + e.getMessage());
		}
	}

	private void writeJson(Path target, JsonElement json) throws IOException {
		Path tmp = target.resolveSibling(target.getFileName().toString() + ".tmp");
		try (Writer w = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8,
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
			gson.toJson(json, w);
		}
		try {
			Files.move(tmp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
		} catch (AtomicMoveNotSupportedException e) {
			Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	public void saveCurrentValuesIntoPreset(ConfigPreset preset) {
		preset.setValues(snapshotCurrentValues());
		try {
			Files.createDirectories(configDir);
			writeJson(presetFile(preset.name()), preset.values());
		} catch (IOException e) {
			System.err.println("[CraftConfig] Failed to save preset '" + preset.name() + "': " + e.getMessage());
		}
	}

	public void saveCurrentValuesIntoActivePreset() {
		getActive().ifPresent(this::saveCurrentValuesIntoPreset);
	}

	public List<ConfigPreset> presets()  { return Collections.unmodifiableList(presets); }
	public String activePresetId()       { return activePresetId; }

	public Optional<ConfigPreset> getActive() {
		return presets.stream().filter(p -> p.id().equals(activePresetId)).findFirst();
	}

	public Optional<ConfigPreset> getDefault() {
		return presets.stream().filter(ConfigPreset::isDefault).findFirst();
	}

	public void switchTo(ConfigPreset preset) {
		activePresetId = preset.id();
		applyToConfig(preset);
		saveAll();
	}

	public ConfigPreset createNew(String name) {
		String uniqueName = getUniqueName(stripCopySuffix(name));
		ConfigPreset p = new ConfigPreset(uniqueName, ConfigPreset.Scope.WORLD_SPECIFIC);
		p.setValues(snapshotCurrentValues());
		presets.add(p);
		saveAll();
		return p;
	}
	public ConfigPreset duplicate(ConfigPreset source) {
		String uniqueName = getUniqueName(stripCopySuffix(source.name()));
		ConfigPreset copy = source.duplicate(uniqueName);
		presets.add(copy);
		saveAll();
		return copy;
	}

	public boolean delete(ConfigPreset preset) {
		if (presets.size() <= 1) return false;

		// If deleting the only default, promote another preset
		if (preset.isDefault() && presets.stream().filter(ConfigPreset::isDefault).count() <= 1) {
			presets.stream()
					.filter(p -> !p.id().equals(preset.id()))
					.findFirst()
					.ifPresent(p -> p.setScope(ConfigPreset.Scope.DEFAULT));
		}

		try { Files.deleteIfExists(presetFile(preset.name())); }
		catch (IOException ignored) {}

		boolean wasActive = preset.id().equals(activePresetId);
		presets.remove(preset);

		if (wasActive) {
			getDefault().ifPresent(def -> {
				activePresetId = def.id();
				applyToConfig(def);
			});
		}

		saveAll();
		return true;
	}

	public void rename(ConfigPreset preset, String newName) {
		String oldName = preset.name();
		String safeName = sanitizeName(newName);
		if (safeName.isEmpty()) safeName = "Preset";

		if (!safeName.equals(oldName)) {
			safeName = getUniqueNameExcluding(safeName, preset.id());
			try {
				Path oldFile = presetFile(oldName);
				Path newFile = presetFile(safeName);
				if (Files.exists(oldFile)) Files.move(oldFile, newFile, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				CraftConfigMod.LOGGER.warn("[CraftConfig] Failed to rename preset file: " + e.getMessage());
			}
			preset.setName(safeName);
			saveAll();
		}
	}

	public void setAsDefault(ConfigPreset preset) {
		getDefault().ifPresent(old -> {
			if (!old.id().equals(preset.id())) old.setScope(ConfigPreset.Scope.WORLD_SPECIFIC);
		});
		preset.setScope(ConfigPreset.Scope.DEFAULT);
		saveAll();
	}

	public void addWorldToPreset(ConfigPreset preset, String worldId) {
		for (ConfigPreset p : presets) {
			if (!p.id().equals(preset.id())) p.worldIds().remove(worldId);
		}
		if (!preset.worldIds().contains(worldId)) preset.worldIds().add(worldId);
		saveAll();
	}

	public void removeWorldFromPreset(ConfigPreset preset, String worldId) {
		preset.worldIds().remove(worldId);
		saveAll();
	}

	public Optional<ConfigPreset> resolveForWorld(String worldId) {
		return presets.stream()
				.filter(p -> p.scope() == ConfigPreset.Scope.WORLD_SPECIFIC && p.appliesTo(worldId))
				.findFirst()
				.or(this::getDefault);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void applyToConfig(ConfigPreset preset) {
		JsonObject root = preset.values();
		JsonObject keybindsRoot = (root.has("_keybinds") && root.get("_keybinds").isJsonObject())
				? root.getAsJsonObject("_keybinds") : new JsonObject();
		for (ConfigCategory cat : config.categories()) {
			String catKey = JsonSerializer.formatCase(cat.name().getString());
			JsonObject catObj = (root.has(catKey) && root.get(catKey).isJsonObject())
					? root.getAsJsonObject(catKey) : null;
			if (catObj == null) continue;
			for (ConfigSection sec : cat.sections()) {
				String secKey = JsonSerializer.formatCase(sec.name().getString());
				JsonObject secObj = (catObj.has(secKey) && catObj.get(secKey).isJsonObject())
						? catObj.getAsJsonObject(secKey) : null;
				if (secObj == null) continue;
				for (ConfigOption opt : sec.options()) {
					String optKey = JsonSerializer.formatCase(opt.name().getString());
					if (secObj.has(optKey)) {
						try {
							JsonSerializer.fromJson(opt, secObj.get(optKey));
							JsonSerializer.readKeybind(keybindsRoot, optKey, opt);
						} catch (Exception ignored) {}
					}
				}
			}
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private JsonObject snapshotCurrentValues() {
		JsonObject root = new JsonObject();
		JsonObject keybindsRoot = new JsonObject();
		for (ConfigCategory cat : config.categories()) {
			JsonObject catObj = new JsonObject();
			for (ConfigSection sec : cat.sections()) {
				JsonObject secObj = new JsonObject();
				for (ConfigOption<?> opt : sec.options()) {
					try {
						String optKey = JsonSerializer.formatCase(opt.name().getString());
						secObj.add(optKey, JsonSerializer.toJson(opt));
						JsonSerializer.writeKeybind(keybindsRoot, optKey, opt);
					} catch (Exception ignored) {}
				}
				catObj.add(JsonSerializer.formatCase(sec.name().getString()), secObj);
			}
			root.add(JsonSerializer.formatCase(cat.name().getString()), catObj);
		}
		root.add("_keybinds", keybindsRoot);
		return root;
	}
	private void ensureIntegrity() {
		if (presets.isEmpty()) {
			ConfigPreset def = new ConfigPreset("Default", ConfigPreset.Scope.DEFAULT);
			def.setValues(snapshotCurrentValues());
			presets.add(def);
			activePresetId = def.id();
			return;
		}

		long defaultCount = presets.stream().filter(ConfigPreset::isDefault).count();
		if (defaultCount == 0) presets.get(0).setScope(ConfigPreset.Scope.DEFAULT);

		boolean activeExists = presets.stream().anyMatch(p -> p.id().equals(activePresetId));
		if (!activeExists) {
			activePresetId = getDefault().map(ConfigPreset::id).orElse(presets.get(0).id());
		}
	}

	private String stripCopySuffix(String name) {
		return name.replaceAll("(?i)\\s+copy(\\s*\\(\\d+\\))?$", "").trim();
	}

	private Path presetFile(String presetName) {
		return configDir.resolve(JsonSerializer.formatCase(presetName) + ".json");
	}

	private String sanitizeName(String name) {
		return name.replaceAll("[^a-zA-Z0-9 _\\-()]", "").trim();
	}

	private String getUniqueName(String baseName) {
		String safeName = sanitizeName(baseName);
		if (safeName.isEmpty()) safeName = "Preset";

		String finalName = safeName;
		if (presets.stream().noneMatch(p -> p.name().equalsIgnoreCase(finalName))) return safeName;

		int i = 1;
		while (true) {
			String candidate = safeName + " Copy" + (i > 1 ? " (" + i + ")" : "");
			if (presets.stream().noneMatch(p -> p.name().equalsIgnoreCase(candidate))) return candidate;
			i++;
		}
	}

	private String getUniqueNameExcluding(String baseName, String excludeId) {
		if (presets.stream().noneMatch(p -> !p.id().equals(excludeId) && p.name().equalsIgnoreCase(baseName)))
			return baseName;

		int i = 1;
		while (true) {
			String candidate = baseName + " (" + i + ")";
			if (presets.stream().noneMatch(p -> !p.id().equals(excludeId) && p.name().equalsIgnoreCase(candidate)))
				return candidate;
			i++;
		}
	}


}
