package net.ray.CraftConfig.util;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ComponentUtil {
	public static List<Component> splitTooltipLines(Component tooltip) {
		List<Component> lines = new ArrayList<>();
		String text = tooltip.getString();
		String[] parts = text.split("\\r?\\n");
		for (String part : parts) {
			lines.add(Component.literal(part));
		}
		return lines;
	}
}
