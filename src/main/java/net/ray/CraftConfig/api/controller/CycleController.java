package net.ray.CraftConfig.api.controller;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.ray.CraftConfig.api.v1.ConfigKeybinds;
import net.ray.CraftConfig.api.v1.ConfigOption;

import java.util.List;
import java.util.function.Function;

public class CycleController<T> implements OptionController<T> {

    private final List<T> values;
    private final Function<T, String> labelProvider;

    public CycleController(List<T> values) {
        this(values, Object::toString);
    }

    public CycleController(List<T> values, Function<T, String> labelProvider) {
        if (values == null || values.isEmpty())
            throw new IllegalArgumentException();
        this.values        = List.copyOf(values);
        this.labelProvider = labelProvider;
    }

	@Override
	public Button createWidget(ConfigOption<T> option, int x, int y, int w, int h) {

		return Button.builder(label(option.get()), btn -> {
			T next = next(option.get());
			option.set(next);
			btn.setMessage(label(next));
		}).bounds(x, y, w, h).build();
	}

    private T next(T current) {
        int idx = values.indexOf(current);
        return values.get((idx + 1) % values.size());
    }
	@Override
	public void initCycleValues(ConfigOption<T> option, ConfigKeybinds keybinds) {
		keybinds.setCycleValues(values);
	}
    private Component label(T value) {
        return Component.literal(labelProvider.apply(value));
    }
}
