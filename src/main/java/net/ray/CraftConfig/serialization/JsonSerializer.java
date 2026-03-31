package net.ray.CraftConfig.serialization;

import com.google.gson.*;
import net.ray.CraftConfig.api.v1.ConfigKeybinds;
import net.ray.CraftConfig.api.v1.ConfigOption;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonSerializer {

	private static final Map<ConfigOption.Type, TypeAdapter<?>> ADAPTERS = new HashMap<>();

	static {
		register(ConfigOption.Type.BOOLEAN, new BooleanAdapter());
		register(ConfigOption.Type.INTEGER, new IntegerAdapter());
		register(ConfigOption.Type.FLOAT,   new FloatAdapter());
		register(ConfigOption.Type.DOUBLE,  new DoubleAdapter());
		register(ConfigOption.Type.STRING,  new StringAdapter());
		register(ConfigOption.Type.COLOR,   new ColorAdapter());
	}

	public static <T> void register(ConfigOption.Type type, TypeAdapter<T> adapter) {
		ADAPTERS.put(type, adapter);
	}



	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <T> JsonElement toJson(ConfigOption<T> option) {
		if (option.type() == ConfigOption.Type.ENUM) {
			return new JsonPrimitive(((Enum) option.get()).name());
		}
		if (option.type() == ConfigOption.Type.LIST) {
			JsonArray arr = new JsonArray();
			for (Object item : (List<?>) option.get()) arr.add(itemToJson(item));
			return arr;
		}
		TypeAdapter<T> adapter = (TypeAdapter<T>) ADAPTERS.get(option.type());
		if (adapter == null) throw new IllegalArgumentException("No adapter for type: " + option.type());
		return adapter.toJson(option.get());
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <T> void fromJson(ConfigOption<T> option, JsonElement element) {
		if (element == null || element.isJsonNull()) return;
		if (option.type() == ConfigOption.Type.ENUM) {
			T current = option.getDefault();
			option.set((T) Enum.valueOf(((Enum) current).getDeclaringClass(), element.getAsString()));
			return;
		}
		if (option.type() == ConfigOption.Type.LIST) {
			List list = new ArrayList();
			for (JsonElement item : element.getAsJsonArray()) list.add(jsonToItem(item));
			option.set((T) list);
			return;
		}
		TypeAdapter<T> adapter = (TypeAdapter<T>) ADAPTERS.get(option.type());
		if (adapter == null) throw new IllegalArgumentException("No adapter for type: " + option.type());
		option.set(adapter.fromJson(element));
	}



	public static JsonElement keybindToJson(ConfigKeybinds keybind) {
		if (keybind == null) return JsonNull.INSTANCE;
		JsonObject obj = new JsonObject();
		obj.addProperty("enabled",         keybind.isEnabled());
		obj.addProperty("defaultKey",      keybind.defaultKey());
		obj.addProperty("mode",            keybind.mode().name());
		obj.addProperty("sendChatMessage", keybind.sendChatMessage());
		return obj;
	}

	public static void keybindFromJson(ConfigKeybinds keybind, JsonElement element) {
		if (keybind == null || element == null || element.isJsonNull()) return;
		JsonObject obj = element.getAsJsonObject();
		if (obj.has("enabled"))         keybind.setEnabled(obj.get("enabled").getAsBoolean());
		if (obj.has("defaultKey"))      keybind.setDefaultKey(obj.get("defaultKey").getAsInt());
		if (obj.has("mode"))            keybind.setMode(ConfigKeybinds.Mode.valueOf(obj.get("mode").getAsString()));
		if (obj.has("sendChatMessage")) keybind.setSendChatMessage(obj.get("sendChatMessage").getAsBoolean());
	}

	public static <T> void writeKeybind(JsonObject keybindsRoot, String key, ConfigOption<T> option) {
		if (option.keybindSettings() == null) return;
		keybindsRoot.add(key, keybindToJson(option.keybindSettings()));
	}
	public static <T> void readKeybind(JsonObject keybindsRoot, String key, ConfigOption<T> option) {
		if (keybindsRoot == null || option.keybindSettings() == null) return;
		if (keybindsRoot.has(key)) {
			keybindFromJson(option.keybindSettings(), keybindsRoot.get(key));
		}
	}
	public static String formatCase(String displayName) {
		return displayName.toLowerCase().replace(' ', '_');
	}
	private static JsonElement itemToJson(Object item) {
		if (item instanceof Boolean  b) return new JsonPrimitive(b);
		if (item instanceof Integer  i) return new JsonPrimitive(i);
		if (item instanceof Double   d) return new JsonPrimitive(d);
		if (item instanceof Float    f) return new JsonPrimitive(f);
		if (item instanceof Enum<?>  e) return new JsonPrimitive(e.name());
		return new JsonPrimitive(item.toString());
	}

	private static Object jsonToItem(JsonElement element) {
		JsonPrimitive p = element.getAsJsonPrimitive();
		if (p.isBoolean()) return p.getAsBoolean();
		if (p.isNumber()) {
			double d = p.getAsDouble();
			if (d == Math.floor(d) && !Double.isInfinite(d)) return p.getAsInt();
			return d;
		}
		return p.getAsString();
	}



	public interface TypeAdapter<T> {
		JsonElement toJson(T value);
		T           fromJson(JsonElement element);
	}

	private static class BooleanAdapter implements TypeAdapter<Boolean> {
		@Override public JsonElement toJson(Boolean v)      { return new JsonPrimitive(v); }
		@Override public Boolean    fromJson(JsonElement e) { return e.getAsBoolean(); }
	}

	private static class IntegerAdapter implements TypeAdapter<Integer> {
		@Override public JsonElement toJson(Integer v)      { return new JsonPrimitive(v); }
		@Override public Integer    fromJson(JsonElement e) { return e.getAsInt(); }
	}

	private static class FloatAdapter implements TypeAdapter<Float> {
		@Override public JsonElement toJson(Float v)        { return new JsonPrimitive(v); }
		@Override public Float      fromJson(JsonElement e) { return e.getAsFloat(); }
	}

	private static class DoubleAdapter implements TypeAdapter<Double> {
		@Override public JsonElement toJson(Double v)       { return new JsonPrimitive(v); }
		@Override public Double     fromJson(JsonElement e) { return e.getAsDouble(); }
	}

	private static class StringAdapter implements TypeAdapter<String> {
		@Override public JsonElement toJson(String v)       { return new JsonPrimitive(v); }
		@Override public String     fromJson(JsonElement e) { return e.getAsString(); }
	}

	private static class ColorAdapter implements TypeAdapter<Color> {
		@Override
		public JsonElement toJson(Color value) {
			return new JsonPrimitive(String.format("#%08X", value.getRGB() & 0xFFFFFFFFL));
		}
		@Override
		public Color fromJson(JsonElement element) {
			String raw = element.getAsString();
			if (raw.startsWith("#")) {
				String hex = raw.substring(1);
				if (hex.length() == 6) hex = "FF" + hex;
				return new Color((int) Long.parseLong(hex, 16), true);
			}
			try {
				return new Color(element.getAsInt(), true);
			} catch (NumberFormatException e) {
				return new Color(0xFFFFFFFF, true); // opaque white
			}
		}
	}
}
