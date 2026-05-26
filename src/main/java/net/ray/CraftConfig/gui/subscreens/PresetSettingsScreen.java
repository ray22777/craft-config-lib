package net.ray.CraftConfig.gui.subscreens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics ;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
//?if >=1.21.9{

import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//?}

//~ if >=1.21 '.controls.KeyBindsScreen' -> '.options.controls.KeyBindsScreen'
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.ray.CraftConfig.platform.CraftConfigMod;
import net.ray.CraftConfig.preset.ConfigPreset;
import net.ray.CraftConfig.preset.PresetManager;
import net.ray.CraftConfig.gui.components.SubScreen;

import java.util.*;
import static net.ray.CraftConfig.gui.constants.ColorSchemes.*;
public class PresetSettingsScreen extends SubScreen {
	private boolean nameConflict = false;

	private boolean worldsExpanded = true;
	private boolean serversExpanded = true;

	private final PresetManager presetManager;
	private final ConfigPreset preset;
	private final Runnable onChanged;

	private EditBox nameBox;
	private Checkbox defaultCheckbox;
	private Button saveButton;

	private PresetEditList editList;

	private final Map<String, String> worldPreviousOwner = new HashMap<>();
	private final List<String> detectedWorlds = new ArrayList<>();

	public PresetSettingsScreen(Screen parent, PresetManager presetManager,
								ConfigPreset preset, Runnable onChanged) {
		super(Component.literal("Edit Preset"), parent, 400, Minecraft.getInstance().getWindow().getGuiScale() >= 4 ? 250 : 300);
		this.presetManager = presetManager;
		this.preset = preset;
		this.onChanged = onChanged;
		detectWorlds();
		initPreviousOwnerMap();
	}

	private void detectWorlds() {
		detectedWorlds.clear();
		try {
			Minecraft mc = Minecraft.getInstance();
			LevelStorageSource.LevelCandidates candidates = mc.getLevelSource().findLevelCandidates();
			List<LevelSummary> summaries = mc.getLevelSource().loadLevelSummaries(candidates).get();
			for (LevelSummary s : summaries)
				detectedWorlds.add(s.getLevelId());
		} catch (Exception e) {
			CraftConfigMod.LOGGER.warn("[CraftConfig] Could not scan worlds: " + e.getMessage());
		}
	}

	private void initPreviousOwnerMap() {
		worldPreviousOwner.clear();
		for (String worldId : detectedWorlds) {
			presetManager.presets().stream()
					.filter(p -> p.worldIds().contains(worldId))
					.findFirst()
					.ifPresent(p -> worldPreviousOwner.put(worldId, p.id()));
		}
	}

	@Override
	@SuppressWarnings("shadow")
	protected void init() {
		super.init();

		int cx = modalX + PADDING;
		int cw = modalW - PADDING * 2;
		int y  = modalY + HEADER_H;

		nameBox = new EditBox(font, cx, y, cw, ROW_H, Component.literal("Name"));
		nameBox.setValue(preset.name());
		nameBox.setMaxLength(40);
		nameBox.setResponder(value -> {

			String trimmed = value.trim();
			nameConflict = !trimmed.isEmpty()
					&& !trimmed.equalsIgnoreCase(preset.name())
					&& presetManager.presets().stream()
					.anyMatch(p -> !p.id().equals(preset.id()) && p.name().equalsIgnoreCase(trimmed));
			nameBox.setTextColor(nameConflict ? 0xFFFF5555 : 0xFFFFFFFF);
			if (saveButton != null) saveButton.active = !nameConflict;
		});
		addRenderableWidget(nameBox);
		y += ROW_H + PADDING;
		//? if >=1.21 {

		defaultCheckbox = Checkbox.builder(Component.literal("Set as Default Preset"), font)
				.pos(cx, y)
				.selected(preset.isDefault())
				.onValueChange((checkbox, selected) -> {
					if (selected) {
						presetManager.setAsDefault(preset);
					} else {
						if (preset.isDefault()) {
							preset.setScope(ConfigPreset.Scope.WORLD_SPECIFIC);
							presetManager.presets().stream()
									.filter(p -> !p.id().equals(preset.id()))
									.findFirst()
									.ifPresent(p -> presetManager.setAsDefault(p));
						}
					}
					rebuildWidgets();
				})
				.build();
		//? } else {
		/*
		defaultCheckbox = new Checkbox(cx, y, 20, 20, Component.literal("Set as Default Preset"), preset.isDefault(), true) {
			@Override
			public void onPress() {
				super.onPress();
				boolean selected = this.selected();
				if (selected) {
					presetManager.setAsDefault(preset);
				} else {
					if (preset.isDefault()) {
						preset.setScope(ConfigPreset.Scope.WORLD_SPECIFIC);
						presetManager.presets().stream()
								.filter(p -> !p.id().equals(preset.id()))
								.findFirst()
								.ifPresent(p -> presetManager.setAsDefault(p));
					}
				}
				rebuildWidgets();
			}
		};
		addRenderableWidget(defaultCheckbox);
		*///? }
		addRenderableWidget(defaultCheckbox);

		int listTop = modalY + HEADER_H + (ROW_H + PADDING) * 2;
		int listBottom = modalY + modalH - 35;
		int listHeight = listBottom - listTop;

		editList = new PresetEditList(Minecraft.getInstance(), modalW - 20, listHeight, listTop, ROW_H + 2);

		//? if >=1.21 {
				editList.setX(modalX + 10);
		//? } else {
				/*editList.setLeftPos(modalX + 10);
		*///? }
		addRenderableWidget(editList);
		rebuildList();

		saveButton = Button.builder(Component.literal("Done"), btn -> {
			saveAndClose();
		}).bounds(modalX + modalW / 2 - 60, modalY + modalH - 30, 120, ROW_H).build();
		addRenderableWidget(saveButton);
	}

	private void rebuildList() {
		//?if>=1.21.9{
		editList.replaceEntries(new ArrayList<>());
		//?}else{
		/*editList.children().clear();
		*///?}
		if (preset.isDefault()) {
			editList.addEntry(new NoteEntry("§7Default preset applies to all worlds."));
			return;
		}

		editList.addEntry(new SectionHeaderEntry("Local Worlds", worldsExpanded, isExpanded -> {
			worldsExpanded = isExpanded;
			rebuildList();
		}));

		if (worldsExpanded) {
			for (String worldId : detectedWorlds) {
				editList.addEntry(new WorldEntry(worldId, preset, presetManager, worldPreviousOwner, this::rebuildList));
			}
		}

		editList.addEntry(new SectionHeaderEntry("Servers", serversExpanded, isExpanded -> {
			serversExpanded = isExpanded;
			rebuildList();
		}));

		if (serversExpanded) {
			editList.addEntry(new AddServerEntry(preset, presetManager, this::rebuildList));

			for (String worldId : new ArrayList<>(preset.worldIds())) {
				if (!detectedWorlds.contains(worldId)) {
					editList.addEntry(new ServerEntry(worldId, preset, presetManager, this::rebuildList));
				}
			}
		}
		if (preset.getKeyMapping() != null) {
			String keyName = preset.getKeyMapping().getTranslatedKeyMessage().getString();
			editList.addEntry(new NoteEntry( "§aBound to: §f" + keyName));
		}
		else{
			editList.addEntry(new NoteEntry( "§cRestart to enable keybinds"));
		}

		editList.addEntry(new ButtonEntry("Edit Keybind", () ->
				Minecraft.getInstance().setScreen(new KeyBindsScreen(this, Minecraft.getInstance().options))));
	}

	private void saveAndClose() {
		if (nameConflict) return;
		applyName();
		presetManager.saveAll();
		if (onChanged != null) onChanged.run();
		onClose();
	}

	private void applyName() {
		String n = nameBox.getValue().trim();
		if (!n.isEmpty() && !n.equals(preset.name()))
			presetManager.rename(preset, n);
	}

	private class PresetEditList extends AbstractSelectionList<PresetEditList.Entry> {
		PresetEditList(Minecraft mc, int width, int height, int top, int itemHeight) {
			//? if >=1.21 {
			super(mc, width, height, top, itemHeight);
			//? } else {
			/*super(mc, width, height, top, top + height, itemHeight);
			this.setRenderBackground(false);
			this.setRenderTopAndBottom(false);
			*///? }
		}
		public int addEntry(Entry entry) {
			return super.addEntry(entry);
		}
		//?if>=26.1{
		 /*@Override protected void extractSelection(GuiGraphics graphics, Entry entry, int outlineColor) {}
		*///?}else if >=1.21.9{
		@Override protected void renderSelection(GuiGraphics guiGraphics, Entry entry, int i) {}
		//?}else{
		/*@Override protected void renderSelection(GuiGraphics g, int top, int width, int height, int a, int b) {}
		*///?}

		@Override
		public int getRowLeft() {
			//? if >=1.21 {
			return getX() + 4;
			//? } else {
			/*return x0 + 4;
			*///? }
		}

		//? if >=1.21.4 {
		@Override protected int scrollBarX() { return getRight() - 6; }
		//? } else if >=1.21 {
				/*@Override protected int getScrollbarPosition() { return getRight() - 6; }
		*///? } else {
				/*@Override protected int getScrollbarPosition() { return x1 - 6; }
		*///? }

		@Override
		public int getRowWidth() {
			return width - 10;
		}



		//? if >=1.21{
		@Override public void updateWidgetNarration(NarrationElementOutput out) {}
		//?}else{
		/*@Override public void updateNarration(NarrationElementOutput narrationElementOutput) {}
		*///?}
		abstract static class Entry extends ObjectSelectionList.Entry<Entry> {}
	}

	private class SectionHeaderEntry extends PresetEditList.Entry {
		private final String title;
		private final boolean isExpanded;
		private final java.util.function.Consumer<Boolean> onToggle;

		SectionHeaderEntry(String title, boolean isExpanded, java.util.function.Consumer<Boolean> onToggle) {
			this.title = title;
			this.isExpanded = isExpanded;
			this.onToggle = onToggle;
		}

		//? if >=1.21.9 {
		//~ if >=26.1 'render' -> 'extract'
		@Override public void renderContent(GuiGraphics g, int mx, int my, boolean hovered, float delta) {
			int x = getX();
			int y = getY();
			int w = getWidth();
			int h = getHeight();
			renderHoverEffect(g, x, y, w, h, hovered);
			String arrow = isExpanded ? "▼" : "▶";
			g.drawString(font, arrow + " " + title, getContentX(), getContentY() + 4, COL_TEXT, false);
		}

		@Override
		public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
			if (onToggle != null) {
				onToggle.accept(!isExpanded);
			}
			return true;
		}
		//? } else {
		/*@Override
		public void render(GuiGraphics g, int idx, int y, int x, int w, int h, int mx, int my, boolean hovered, float delta) {
			renderHoverEffect(g, x, y, w, h, hovered);
			String arrow = isExpanded ? "▼" : "▶";
			g.drawString(font, arrow + " " + title, x + 4, y + 4, COL_TEXT, false);
		}


		@Override
		public boolean mouseClicked(double mx, double my, int button) {
			if (onToggle != null) {
				onToggle.accept(!isExpanded);
			}
			return true;
		}
		*///? }

		@Override
		public Component getNarration() {
			return Component.empty();
		}

	}
	private class ButtonEntry extends PresetEditList.Entry {
		private final Button button;

		ButtonEntry(String label, Runnable onClick) {
			this.button = Button.builder(Component.literal(label), btn -> onClick.run())
					.bounds(0, 0, 80, 16).build();
		}

		//? if >=1.21.9 {
		//~ if >=26.1 'render' -> 'extract'
		@Override public void renderContent(GuiGraphics g, int mx, int my, boolean hovered, float delta) {
			button.setX(getX() + getWidth() / 2 - 40);
			button.setY(getY());
			button.setHeight(16);
			//~ if >=26.1 'render' -> 'extractRenderState'
			button.render(g, mx, my, 0);
		}

		@Override
		public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
			return button.mouseClicked(event, doubleClick);
		}
		//? } else {
    /*@Override
    public void render(GuiGraphics g, int idx, int y, int x, int w, int h, int mx, int my, boolean hovered, float delta) {
        button.setX(x + w / 2 - 40);
        button.setY(y);
        button.render(g, mx, my, 0);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        return this.button.mouseClicked(mx, my, button);
    }
    *///? }

		@Override public Component getNarration() { return Component.empty(); }
	}
	private class NoteEntry extends PresetEditList.Entry {
		private final String text;
		NoteEntry(String text) {
			this.text = text;
		}
		//? if >=1.21.9 {
		//~ if >=26.1 'render' -> 'extract'
		@Override public void renderContent(GuiGraphics g, int mx, int my, boolean hovered, float delta) {
			int x = getX();
			int y = getY();
			int w = getWidth();
			g.drawCenteredString(font, text, x + w / 2, y + 4, COL_DIM);
		}
		@Override public boolean isMouseOver(double mx, double my) { return false; }
		//? } else {
		/*@Override
		public void render(GuiGraphics g, int idx, int y, int x, int w, int h, int mx, int my, boolean hovered, float delta) {
			g.drawCenteredString(font, text, x + w / 2, y + 4, COL_DIM);
		}
		*///? }
		@Override
		public Component getNarration() {
			return Component.empty();
		}
	}

	private class WorldEntry extends PresetEditList.Entry {
		private final String worldId;
		private final ConfigPreset preset;
		private final PresetManager presetManager;
		private final Map<String, String> worldPreviousOwner;
		private final Runnable rebuildCallback;

		WorldEntry(String worldId, ConfigPreset preset, PresetManager presetManager,
				   Map<String, String> worldPreviousOwner, Runnable rebuildCallback) {
			this.worldId = worldId;
			this.preset = preset;
			this.presetManager = presetManager;
			this.worldPreviousOwner = worldPreviousOwner;
			this.rebuildCallback = rebuildCallback;
		}

		private void renderWorldEntryContent(GuiGraphics g, int x, int y, int w, int h, boolean hovered) {
			boolean assignedHere = preset.worldIds().contains(worldId);
			Optional<ConfigPreset> ownerOpt = findOwner(worldId);
			boolean takenByOther = ownerOpt.isPresent() && !ownerOpt.get().id().equals(preset.id());
			String ownerName = ownerOpt.map(ConfigPreset::name).orElse("");

			int textColor = COL_DIM;
			String suffix = "";
			if (assignedHere) {
				textColor = COL_GREEN;
				suffix = " §7[Current]";
			} else if (takenByOther) {
				textColor = COL_RED;
				suffix = " §c[" + ownerName + "]";
			}

			renderHoverEffect(g, x, y, w, h, hovered);
			g.drawString(font, worldId + suffix, x + 4, y + 4, textColor, false);
		}

		private boolean handleWorldMouseClick() {
			boolean assignedHere = preset.worldIds().contains(worldId);

			if (assignedHere) {
				String prevOwner = worldPreviousOwner.get(worldId);
				presetManager.removeWorldFromPreset(preset, worldId);

				if (prevOwner != null && !prevOwner.equals(preset.id())) {
					presetManager.presets().stream()
							.filter(p -> p.id().equals(prevOwner))
							.findFirst()
							.ifPresent(p -> presetManager.addWorldToPreset(p, worldId));
				}
				worldPreviousOwner.remove(worldId);
			} else {
				Optional<ConfigPreset> currentOwner = findOwner(worldId);
				currentOwner.ifPresent(p -> worldPreviousOwner.put(worldId, p.id()));
				presetManager.addWorldToPreset(preset, worldId);
			}

			rebuildCallback.run();
			return true;
		}
		//?if>=1.21.9
		@Override public int getHeight() {return 16;}
		//? if >=1.21.9 {
		//~ if >=26.1 'render' -> 'extract'
		@Override public void renderContent(GuiGraphics g, int mx, int my, boolean hovered, float delta) {
			renderWorldEntryContent(g, getX(), getY(), getWidth(), getHeight(), hovered);
		}

		@Override
		public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
			return handleWorldMouseClick();
		}
//? } else {
/*@Override
public void render(GuiGraphics g, int idx, int y, int x, int w, int h, int mx, int my, boolean hovered, float delta) {
    renderWorldEntryContent(g, x, y, w, h, hovered);
}

@Override
public boolean mouseClicked(double mx, double my, int button) {
    return handleWorldMouseClick();
}
*///? }

		@Override
		public Component getNarration() {
			return Component.empty();
		}

		private Optional<ConfigPreset> findOwner(String worldId) {
			return presetManager.presets().stream()
					.filter(p -> p.worldIds().contains(worldId))
					.findFirst();
		}
	}

	private class ServerEntry extends PresetEditList.Entry {
		private final String serverIp;
		private final ConfigPreset preset;
		private final PresetManager presetManager;
		private final Runnable rebuildCallback;

		ServerEntry(String serverIp, ConfigPreset preset, PresetManager presetManager, Runnable rebuildCallback) {
			this.serverIp = serverIp;
			this.preset = preset;
			this.presetManager = presetManager;
			this.rebuildCallback = rebuildCallback;
			this.setFocused(false);
		}

		private void renderServerEntryContent(GuiGraphics g, int x, int y, int w, int h, int mx, int my, boolean hovered) {
			renderHoverEffect(g, x, y, w, h, hovered);
			g.drawString(font, serverIp, x + 4, y + 4, COL_TEXT, false);

			int btnX = x + w - 20;
			boolean btnHovered = mx >= btnX && mx <= btnX + 16 && my >= y + 2 && my < y + 18;
			g.drawString(font, "✖", btnX + 4, y + 4, btnHovered ? COL_RED : COL_DIM, false);
		}

		private boolean handleServerMouseClick(double mx, double my) {
			int w = editList.getRowWidth();
			int x = editList.getRowLeft();
			int btnX = x + w - 20;
			if (mx >= btnX && mx <= btnX + 16) {
				presetManager.removeWorldFromPreset(preset, serverIp);
				rebuildCallback.run();
				return true;
			}
			return false;
		}

		//? if >=1.21.9 {
		//~ if >=26.1 'render' -> 'extract'
		@Override public void renderContent(GuiGraphics g, int mx, int my, boolean hovered, float delta) {
			renderServerEntryContent(g, getX(), getY(), getWidth(), getHeight(), mx, my, hovered);
		}
		@Override
		public int getHeight() {
			return 16;
		}
		@Override
		public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
			return handleServerMouseClick(event.x(), event.y());
		}
		//? } else {
		/*@Override
		public void render(GuiGraphics g, int idx, int y, int x, int w, int h, int mx, int my, boolean hovered, float delta) {
			renderServerEntryContent(g, x, y, w, h, mx, my, hovered);
		}

		@Override
		public boolean mouseClicked(double mx, double my, int button) {
			return handleServerMouseClick(mx, my);
		}
		*///? }

		@Override
		public Component getNarration() {
			return Component.empty();
		}
	}

	private class AddServerEntry extends PresetEditList.Entry {
		private final ConfigPreset preset;
		private final PresetManager presetManager;
		private final Runnable rebuildCallback;

		private final EditBox inputBox;
		private final Button addButton;
		private boolean wasFocused = false;

		AddServerEntry(ConfigPreset preset, PresetManager presetManager, Runnable rebuildCallback) {
			this.preset = preset;
			this.presetManager = presetManager;
			this.rebuildCallback = rebuildCallback;

			int editHeight = 16;
			this.inputBox = new EditBox(font, 0, 0, 100, editHeight, Component.literal("Server IP"));
			this.inputBox.setHint(Component.literal("Enter Server IP..."));
			this.inputBox.setCanLoseFocus(true);

			this.addButton = Button.builder(Component.literal("Add"), btn -> {
				String val = inputBox.getValue().trim();
				String sanitized = sanitizeServerAddress(val);
				if (!sanitized.isEmpty()) {
					presetManager.addWorldToPreset(preset, sanitized);
					inputBox.setValue("");
					rebuildCallback.run();
				}
			}).bounds(0, 0, 40, 18).build();
		}
		@Override
		public boolean isFocused() {
			return false;
		}
		private String sanitizeServerAddress(String address) {
			return address.replaceAll("[^a-zA-Z0-9._:\\-()]", "").trim();
		}

		private void renderAddServerContent(GuiGraphics g, int x, int y, int w, int h, int mx, int my, float delta) {
			int btnW = 40;
			int gap = 6;
			int pad = 4;

			int editHeight = 16;
			int editY = y + (h - editHeight) / 2;
			int inputW = w - btnW - gap - (pad * 2);
			int btnX = x + w - btnW - pad;
			int inputX = x + pad;

				inputBox.setX(inputX);
				inputBox.setY(editY);
				inputBox.setWidth(inputW);
			//? if >=1.21
				inputBox.setHeight(editHeight);
				addButton.setX(btnX);
				addButton.setY(editY - 1);
				addButton.setWidth(btnW);
			//? if >=1.21
				addButton.setHeight(editHeight + 2);
			//~ if >=26.1 'render' -> 'extractWidgetRenderState'
			inputBox.render(g, mx, my, delta);
			//~ if >=26.1 'render' -> 'extractRenderState'
			addButton.render(g, mx, my, delta);

			if (inputBox.isFocused() && !wasFocused) {
				wasFocused = true;
			} else if (!inputBox.isFocused() && wasFocused) {
				wasFocused = false;
			}
		}

		//? if >=1.21.9 {
		//~ if >=26.1 'render' -> 'extract'
		@Override public void renderContent(GuiGraphics g, int mx, int my, boolean hovered, float delta) {
			renderAddServerContent(g, getX(), getY(), getWidth(), getHeight(), mx, my, delta);
		}
		//? } else {
		/*@Override
		public void render(GuiGraphics g, int idx, int y, int x, int w, int h, int mx, int my, boolean hovered, float delta) {
			renderAddServerContent(g, x, y, w, h, mx, my, delta);
		}
		*///? }

		@Override
		public Component getNarration() {
			return Component.empty();
		}

		//? if >=1.21.9 {
		@Override
		public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
			double mx = event.x();
			double my = event.y();
			if (mx >= inputBox.getX() && mx <= inputBox.getX() + inputBox.getWidth() &&
					my >= inputBox.getY() && my <= inputBox.getY() + inputBox.getHeight()) {

				inputBox.setFocused(true);
				inputBox.setEditable(true);

				if (editList != null) {
					for (ObjectSelectionList.Entry entry : editList.children()) {
						if (entry != this && entry instanceof AddServerEntry other) {
							other.inputBox.setFocused(false);
						}
					}
				}

				return inputBox.mouseClicked(event, doubleClick);
			}

			inputBox.setFocused(false);

			if (mx >= addButton.getX() && mx <= addButton.getX() + addButton.getWidth() &&
					my >= addButton.getY() && my <= addButton.getY() + addButton.getHeight()) {
				return addButton.mouseClicked(event, doubleClick);
			}




			return true;
		}

		@Override
		public boolean mouseReleased(MouseButtonEvent event) {
			if (inputBox.isFocused()) {
				return inputBox.mouseReleased(event);
			}
			return true;
		}
		@Override
		public boolean keyPressed(KeyEvent keyEvent) {
			if (inputBox.isFocused()) {
				int keyCode = keyEvent.key();

				if (keyCode == 257 || keyCode == 335) {
					String val = inputBox.getValue().trim();
					String sanitized = sanitizeServerAddress(val);
					if (!sanitized.isEmpty()) {
						presetManager.addWorldToPreset(preset, sanitized);
						inputBox.setValue("");
						rebuildCallback.run();
						inputBox.setFocused(true);
					}
					return true;
				}
				if (keyCode == 256) {
					inputBox.setFocused(false);
					return true;
				}
				return inputBox.keyPressed(keyEvent);
			}
			return false;
		}

		@Override
		public boolean charTyped(CharacterEvent characterEvent) {
			if (inputBox.isFocused()) {
				return inputBox.charTyped(characterEvent);
			}
			return false;
		}
		//? } else {
		/*@Override
		public boolean mouseClicked(double mx, double my, int button) {
			if (mx >= inputBox.getX() && mx <= inputBox.getX() + inputBox.getWidth() &&
					my >= inputBox.getY() && my <= inputBox.getY() + inputBox.getHeight()) {

				inputBox.setFocused(true);
				inputBox.setEditable(true);

				if (editList != null) {
					for (ObjectSelectionList.Entry entry : editList.children()) {
						if (entry != this && entry instanceof AddServerEntry other) {
							other.inputBox.setFocused(false);
						}
					}
				}

				return inputBox.mouseClicked(mx, my, button);
			}

			inputBox.setFocused(false);

			if (mx >= addButton.getX() && mx <= addButton.getX() + addButton.getWidth() &&
					my >= addButton.getY() && my <= addButton.getY() + addButton.getHeight()) {
				return addButton.mouseClicked(mx, my, button);
			}

			return false;
		}

		@Override
		public boolean mouseReleased(double mx, double my, int button) {
			if (inputBox.isFocused()) {
				return inputBox.mouseReleased(mx, my, button);
			}
			return false;
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (inputBox.isFocused()) {
				if (keyCode == 257 || keyCode == 335) {
					String val = inputBox.getValue().trim();
					String sanitized = sanitizeServerAddress(val);
					if (!sanitized.isEmpty()) {
						presetManager.addWorldToPreset(preset, sanitized);
						inputBox.setValue("");
						rebuildCallback.run();
						inputBox.setFocused(true);
					}
					return true;
				}
				if (keyCode == 256) {
					inputBox.setFocused(false);
					return true;
				}
				return inputBox.keyPressed(keyCode, scanCode, modifiers);
			}
			return false;
		}

		@Override
		public boolean charTyped(char chr, int modifiers) {
			if (inputBox.isFocused()) {
				return inputBox.charTyped(chr, modifiers);
			}
			return false;
		}
*///? }


	}
}
