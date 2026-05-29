package net.tricube.CraftConfig.preset;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

import java.util.*;

public class ConfigPreset {

	public enum Scope { DEFAULT, WORLD_SPECIFIC }

	private final String id;
	private String name;
	private Scope scope;
	private final List<String> worldIds;
	private JsonObject values;
	private int keyBind = InputConstants.UNKNOWN.getValue();
	private transient KeyMapping keyMapping;
	public ConfigPreset(String name, Scope scope) {
		this.id       = UUID.randomUUID().toString();
		this.name     = name;
		this.scope    = scope;
		this.worldIds = new ArrayList<>();
		this.values   = new JsonObject();
	}

	public ConfigPreset(String id, String name, Scope scope, List<String> worldIds, JsonObject values) {
		this.id       = id;
		this.name     = name;
		this.scope    = scope;
		this.worldIds = new ArrayList<>(worldIds);
		this.values   = values != null ? values : new JsonObject();
	}
	public int keyBind() { return keyBind; }
	public void setKeyBind(int key) { this.keyBind = key; }
	public KeyMapping getKeyMapping() { return keyMapping; }
	public void setKeyMapping(KeyMapping mapping) { this.keyMapping = mapping; }
	public String id()                  { return id; }
	public String name()                { return name; }
	public void setName(String n)       { this.name = n; }
	public Scope scope()                { return scope; }
	public void setScope(Scope s)       { this.scope = s; }
	public List<String> worldIds()      { return worldIds; }
	public JsonObject values()          { return values; }
	public void setValues(JsonObject v) { this.values = v; }
	public boolean isDefault()          { return scope == Scope.DEFAULT; }

	public boolean appliesTo(String worldId) {
		return scope == Scope.DEFAULT || worldIds.contains(worldId);
	}

	public ConfigPreset duplicate(String newName) {
		ConfigPreset copy = new ConfigPreset(UUID.randomUUID().toString(), newName,
				Scope.WORLD_SPECIFIC, new ArrayList<>(worldIds), values.deepCopy());
		return copy;
	}
}
