package net.tricube.CraftConfig.api.controller;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.tricube.CraftConfig.api.v1.ConfigOption;
import net.tricube.CraftConfig.api.v1.entries.SelectionGridEntry;
import net.tricube.CraftConfig.gui.subscreens.SelectionGridScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SelectionGridController implements OptionController<List<String>> {

	private final Supplier<List<SelectionGridEntry>> masterListSupplier;
	private boolean isBlacklist;

	public SelectionGridController(Supplier<List<SelectionGridEntry>> masterListSupplier, boolean isBlacklist) {
		this.masterListSupplier = masterListSupplier;
		this.isBlacklist = isBlacklist;
	}

	public SelectionGridController(Supplier<List<SelectionGridEntry>> masterListSupplier) {
		this.masterListSupplier = masterListSupplier;
		this.isBlacklist = false;
	}

	// keep old constructors for backwards compat
	public SelectionGridController(List<SelectionGridEntry> masterList, boolean isBlacklist) {
		this(() -> masterList, isBlacklist);
	}

	public SelectionGridController(List<SelectionGridEntry> masterList) {
		this(() -> masterList, false);
	}

	@Override
	public AbstractWidget createWidget(ConfigOption<List<String>> option, int x, int y, int w, int h) {
		//?if>=26.1{
		/*if(Minecraft.getInstance().level == null){
			return Button.builder(Component.literal("Enter a world first!").withStyle(ChatFormatting.RED),btn -> {}).bounds(x, y, w, h).build();
		}
		else{
			*///?}
			return Button.builder(Component.literal("Configure…"), btn -> {
				List<SelectionGridEntry> masterList = masterListSupplier.get();
				List<String> savedKeys = option.get() != null ? new ArrayList<>(option.get()) : new ArrayList<>();

				List<SelectionGridEntry> workingEntries = new ArrayList<>();
				for (SelectionGridEntry base : masterList) {
					SelectionGridEntry copy = base.copy();
					copy.enabled = savedKeys.contains(copy.key);
					workingEntries.add(copy);
				}

				Minecraft.getInstance().setScreen(new SelectionGridScreen(
						Minecraft.getInstance().screen,
						option.name(),
						workingEntries,
						selectedEntries -> {
							List<String> newKeys = selectedEntries.stream()
									.filter(SelectionGridEntry::enabled)
									.map(entry -> entry.key)
									.collect(Collectors.toList());
							option.set(newKeys);
						},
						isBlacklist
				));
			}).bounds(x, y, w, h).build();
			//?if>=26.1
		//}

	}
}
