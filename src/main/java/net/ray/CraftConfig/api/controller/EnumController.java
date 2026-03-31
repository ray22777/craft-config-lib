package net.ray.CraftConfig.api.controller;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.ray.CraftConfig.api.v1.ConfigOption;

public class EnumController<E extends Enum<E>> implements OptionController<E> {

    @Override
    @SuppressWarnings("unchecked")
    public Button createWidget(ConfigOption<E> option, int x, int y, int w, int h) {
        return Button.builder(
                label(option),
                btn -> {
                    E[] values = (E[]) option.get().getClass().getEnumConstants();
                    int next = (option.get().ordinal() + 1) % values.length;
                    option.set(values[next]);
                    btn.setMessage(label(option));
                })
                .bounds(x, y, w, h)
                .build();
    }

    private static <E extends Enum<E>> Component label(ConfigOption<E> opt) {
        String raw = opt.get().name().replace('_', ' ');
        String pretty = Character.toUpperCase(raw.charAt(0))
                + raw.substring(1).toLowerCase();
        return Component.literal(pretty);
    }
}
