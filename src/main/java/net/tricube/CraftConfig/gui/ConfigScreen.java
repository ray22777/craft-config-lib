package net.tricube.CraftConfig.gui;


import net.minecraft.client.gui.GuiGraphics ;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
//?if >=1.21
import com.mojang.blaze3d.systems.RenderSystem;
//?if >=1.21.9
//import net.minecraft.client.input.MouseButtonEvent;
//?if >=1.21.6
//import net.minecraft.client.renderer.RenderPipelines;
//?if <1.21.11
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.tricube.CraftConfig.api.v1.ConfigCategory;
import net.tricube.CraftConfig.api.v1.ConfigOption;
import net.tricube.CraftConfig.api.v1.ConfigSection;
import net.tricube.CraftConfig.api.v1.CraftConfig;
import net.tricube.CraftConfig.gui.components.CategoryListWidget;
import net.tricube.CraftConfig.gui.components.OptionListWidget;
import net.tricube.CraftConfig.gui.subscreens.PresetSettingsScreen;
import net.tricube.CraftConfig.preset.ConfigPreset;
import net.tricube.CraftConfig.preset.PresetManager;
import net.tricube.CraftConfig.util.ComponentUtil;
import net.tricube.CraftConfig.util.ScreenUtils;

import java.util.*;
import static net.tricube.CraftConfig.gui.constants.ColorSchemes.*;
import static net.tricube.CraftConfig.gui.constants.ScreenConstants.*;
public class ConfigScreen extends Screen {

	private final Screen parent;
	private final CraftConfig config;
	private final PresetManager presetManager;

	private int selectedCategory = 0;
	private final Set<String> collapsedSections = new HashSet<>();
	private String searchQuery = "";
	private boolean dropdownOpen = false;
	private Component pendingTooltip;
	private int tooltipX, tooltipY;

	public void setTooltip(Component tooltip, int x, int y) {
		this.pendingTooltip = tooltip;
		this.tooltipX = x;
		this.tooltipY = y;
	}
	private ConfigPreset viewingPreset = null;

	private Map<String, Object> valuesSnapshot;

	private Button btnMid, btnRight;
	private EditBox searchBox;
	private Button presetDropdownButton;
	private OptionListWidget optionList;
	private CategoryListWidget categoryList;

	public ConfigScreen(Screen parent, CraftConfig config) {
		super(config.title());
		this.parent        = parent;
		this.config        = config;
		this.presetManager = config.presetManager();
	}

	@Override
	protected void init() {
		clearWidgets();

		if (valuesSnapshot == null) {
			valuesSnapshot = snapshotValues();
		}

		searchBox = new EditBox(font, PADDING, TITLE_H + 4, 200, 20, Component.literal("Search"));
		searchBox.setHint(Component.literal("Search..."));
		searchBox.setResponder(s -> {
			searchQuery = s.toLowerCase(Locale.ROOT);
			rebuildOptionList();
		});
//		searchBox.setBordered(true);

		addRenderableWidget(searchBox);

		int catWidth = categoryPanelWidth();
		categoryList = new CategoryListWidget(minecraft, catWidth,
				height - HEADER_H - BOTTOM_H, HEADER_H + 1, ROW_H);
		//? if >=1.21 {
		categoryList.setX(0);
		 //? } else {
		/*categoryList.setLeftPos(0);
		*///? }
		categoryList.currentSelectedIndex = selectedCategory;
		for (int i = 0; i < config.categories().size(); i++) {
			final int idx = i;
			ConfigCategory cat = config.categories().get(i);
			categoryList.addEntry(new CategoryListWidget.CategoryEntry(
					cat.name().getString(), idx, categoryList, () -> {
				selectedCategory = idx;
				searchQuery = "";
				searchBox.setValue("");
				searchBox.setFocused(false);
				optionList.setScrollAmount(0);
				rebuildOptionList();
			}));
		}
		addRenderableWidget(categoryList);

		int optionWidth = width - catWidth;
		optionList = new OptionListWidget(minecraft, optionWidth,
				height - HEADER_H - BOTTOM_H, HEADER_H + 1, ROW_H);
		//? if >=1.21 {
		optionList.setX(catWidth);
		 //? } else {
		/*optionList.setLeftPos(catWidth);
		*///? }
		addRenderableWidget(optionList);
		rebuildOptionList();

		buildBottomButtons();

		presetDropdownButton = Button.builder(
				Component.literal(dropdownArrow() + getViewingPresetLabel()),
				btn -> toggleDropdown()
		).bounds(width - 210, TITLE_H + 4, 200, 20).build();
		addRenderableWidget(presetDropdownButton);
	}

	private void buildBottomButtons() {
		int buttonY = height - BOTTOM_H + 6;
		int cx      = width / 2;
		int startX  = cx - (BTN_W * 2 + BTN_GAP) / 2;

		btnMid = Button.builder(Component.literal("Reset All"), btn -> {
			if (hasUnsavedChanges()) {
				revertToSnapshot();
				rebuildOptionList();
			} else {
				resetAll();
				rebuildOptionList();
			}
		}).bounds(startX, buttonY, BTN_W, 20).build();
		addRenderableWidget(btnMid);

		btnRight = Button.builder(Component.literal("Done"), btn -> {
			if (hasUnsavedChanges()) {
				saveChanges();
				rebuildOptionList();
			} else {
				exitWithCheck();
			}
		}).bounds(startX + BTN_W + BTN_GAP, buttonY, BTN_W, 20).build();
		addRenderableWidget(btnRight);
	}


	private ConfigPreset getViewingPreset() {
		if (viewingPreset != null) return viewingPreset;
		return presetManager.getActive().orElse(null);
	}

	private boolean isViewingNonActivePreset() {
		if (viewingPreset == null) return false;
		return !viewingPreset.id().equals(presetManager.activePresetId());
	}

	private String getViewingPresetLabel() {
		ConfigPreset active  = presetManager.getActive().orElse(null);
		ConfigPreset viewing = getViewingPreset();

		if (viewing == null) return "None";

		boolean isActive  = viewing.id().equals(presetManager.activePresetId());
		boolean isDefault = viewing.isDefault();
		String namePart   = viewing.name();
		String defaultTag = isDefault ? " §7[Default]§r" : "";

		if (isActive) return namePart + " §a[Active]" + defaultTag;
		return namePart + " §f[Viewing]" + defaultTag;
	}

	private String dropdownArrow() {
		return dropdownOpen ? "§f▼ " : "§f▶ ";
	}

	private void updatePresetButtonText() {
		if (presetDropdownButton != null) {
			presetDropdownButton.setMessage(
					Component.literal(dropdownArrow() + getViewingPresetLabel()));
		}
	}

	private void toggleDropdown() {
		dropdownOpen = !dropdownOpen;
		updatePresetButtonText();
		if (!dropdownOpen) rebuildWidgets();
	}

	private void switchToPresetView(ConfigPreset p) {
		viewingPreset = p;
		presetManager.applyToConfig(p);
		valuesSnapshot = snapshotValues();
		updatePresetButtonText();
		rebuildOptionList();
	}

	private void setPresetActive() {
		ConfigPreset toActivate = getViewingPreset();
		if (toActivate == null) return;
		presetManager.saveCurrentValuesIntoPreset(toActivate);
		presetManager.switchTo(toActivate);
		viewingPreset = null;
		valuesSnapshot = snapshotValues();
		updatePresetButtonText();
		rebuildOptionList();
		rebuildWidgets();
	}

	private void saveChanges() {
		ConfigPreset target = getViewingPreset();
		if (target != null) {
			presetManager.saveCurrentValuesIntoPreset(target);
		}
		valuesSnapshot = snapshotValues();
	}
	public void refreshOptionList() {
		rebuildOptionList();
	}
	@SuppressWarnings("unchecked")
	private void revertToSnapshot() {
		for (ConfigCategory cat : config.categories())
			for (ConfigSection sec : cat.sections())
				for (ConfigOption<?> opt : sec.options()) {
					String key = cat.name().getString() + "." + sec.name().getString() + "." + opt.name().getString();
					Object val = valuesSnapshot.get(key);
					if (val != null) ((ConfigOption<Object>) opt).set(val);
				}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> snapshotValues() {
		Map<String, Object> snap = new HashMap<>();
		for (ConfigCategory cat : config.categories())
			for (ConfigSection sec : cat.sections())
				for (ConfigOption<?> opt : sec.options())
					snap.put(cat.name().getString() + "." + sec.name().getString() + "." + opt.name().getString(), opt.get());
		return snap;
	}

	private boolean hasUnsavedChanges() {
		return valuesSnapshot != null && !snapshotValues().equals(valuesSnapshot);
	}

	private boolean isChanged() {
		for (ConfigCategory cat : config.categories())
			for (ConfigSection sec : cat.sections())
				for (ConfigOption<?> opt : sec.options())
					if (!Objects.equals(opt.get(), opt.getDefault())) return true;
		return false;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private void resetOption(ConfigOption opt) {
		opt.set(opt.getDefault());
		rebuildOptionList();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private void resetAll() {
		for (ConfigCategory cat : config.categories())
			for (ConfigSection sec : cat.sections())
				for (ConfigOption<?> opt : sec.options())
					resetOption(opt);
	}

	private void exitWithCheck() {
		if (hasUnsavedChanges()) {
			minecraft.setScreen(new ConfirmScreen(
					confirmed -> {
						if (confirmed) { saveChanges(); onClose(); }
						else             onClose();
					},
					Component.literal("Unsaved Changes"),
					Component.literal("You have unsaved changes. Save before exiting?"),
					Component.literal("Save & Exit"),
					Component.literal("Discard & Exit")
			));
		} else {
			onClose();
		}
	}

	private void rebuildOptionList() {
		optionList.clearEntries();
		if (config.categories().isEmpty()) return;

		if (!searchQuery.isEmpty()) {
			Map<String, List<ConfigOption<?>>> grouped = new LinkedHashMap<>();
			for (ConfigCategory cat : config.categories())
				for (ConfigSection sec : cat.sections())
					for (ConfigOption<?> opt : sec.options())
						if (matchesSearch(opt))
							grouped.computeIfAbsent(
									cat.name().getString() + " › " + sec.name().getString(),
									k -> new ArrayList<>()).add(opt);
			for (Map.Entry<String, List<ConfigOption<?>>> e : grouped.entrySet()) {
				optionList.addEntry(new OptionListWidget.GroupHeaderEntry(e.getKey()));
				for (ConfigOption<?> opt : e.getValue())
					optionList.addEntry(new OptionListWidget.OptionEntry(opt,
							() -> resetOption(opt), this));
			}
		} else {
			ConfigCategory cat = config.categories().get(selectedCategory);
			for (ConfigSection sec : cat.sections()) {
				boolean collapsed = collapsedSections.contains(sec.name().getString());
				optionList.addEntry(new OptionListWidget.SectionEntry(sec.name().getString(), collapsed, () -> {
					if (collapsed) collapsedSections.remove(sec.name().getString());
					else           collapsedSections.add(sec.name().getString());
					rebuildOptionList();
				}));
				if (!collapsed)
					for (ConfigOption<?> opt : sec.options())
						optionList.addEntry(new OptionListWidget.OptionEntry(opt,
								() -> resetOption(opt), this));
			}
		}
	}

	private boolean matchesSearch(ConfigOption<?> opt) {
		return opt.name().getString().toLowerCase(Locale.ROOT).contains(searchQuery) ||
				(opt.description() != null &&
						opt.description().getString().toLowerCase(Locale.ROOT).contains(searchQuery));
	}

	private void updateButtonStates() {
		boolean dirty = hasUnsavedChanges();

		if (btnMid != null) {
			if (dirty) {
				btnMid.setMessage(Component.literal("Undo"));
				btnMid.active = true;
			} else {
				btnMid.setMessage(Component.literal("Reset All"));
				btnMid.active = isChanged();
			}
		}

		if (btnRight != null) {
			btnRight.setMessage(Component.literal(dirty ? "Save" : "Done"));
			btnRight.active = true;
		}
	}

	// 	//~ if >=26.1 'render' -> 'extract'
	//~ if >=26.1 'render' -> 'extractRenderState'
	@Override public void render(GuiGraphics g, int mx, int my, float delta) {
		//? if <=1.21.5
		renderBackground(g /*? if >=1.21 {*/ , mx, my, delta /*?}*/);

		int panelW      = categoryPanelWidth();
		int panelBottom = height - BOTTOM_H;
		//? if <1.21 {
		/*g.blit(Screen.BACKGROUND_LOCATION, 0, panelBottom, 0, panelBottom, width, BOTTOM_H, 32, 32);
		g.fill(0, panelBottom, width, height, 0xCC000000);
		*///? }
		g.fill(panelW, HEADER_H, panelW + 1, panelBottom + 1, COL_BORDER);
		g.fill(0, TITLE_H, width, HEADER_H, COL_BG_DARK);
		g.fill(0, HEADER_H, width, HEADER_H + 1, COL_BORDER);
		//? if >=1.21 {
				ResourceLocation topSep = minecraft.level == null
						? Screen.HEADER_SEPARATOR : Screen.INWORLD_HEADER_SEPARATOR;
				ResourceLocation botSep = minecraft.level == null
						? Screen.FOOTER_SEPARATOR : Screen.INWORLD_FOOTER_SEPARATOR;//TODO:FIX FOR 1.21.2
				//? if >=1.21.6 {
				/*g.blit(RenderPipelines.GUI_TEXTURED, topSep, 0, TITLE_H - 1, 0.0F, 0.0F, width, 2, 32, 2);
				g.blit(RenderPipelines.GUI_TEXTURED, botSep, 0, panelBottom + 1, 0.0F, 0.0F, width, 2, 32, 2);
				*///?}else{
				//?if <1.21.5
				RenderSystem.enableBlend();
				g.blit(/*? if >= 1.21.2 {*/ /*RenderType::guiTextured, *//*?}*/topSep, 0, TITLE_H - 1, 0.0F, 0.0F, width, 2, 32, 2);
				g.blit(/*? if >= 1.21.2 {*/ /*RenderType::guiTextured, *//*?}*/botSep, 0, panelBottom + 1, 0.0F, 0.0F, width, 2, 32, 2);
				//?if <1.21.5
				RenderSystem.disableBlend();

				//? }
		//? }

		g.drawCenteredString(font, title, width / 2, (TITLE_H - font.lineHeight) / 2, COL_ACCENT);
		g.drawString(font, "Preset:", width - 210 - font.width("Preset:") - 4,
				TITLE_H + 4 + (20 - font.lineHeight) / 2, COL_TEXT_GRAY, false);
		updateButtonStates();
		//?if<1.21{
		/*for (GuiEventListener child : children()) {
			if (child instanceof Renderable r && child != searchBox) r.render(g, mx, my, delta);
		}
		searchBox.render(g,mx,my,delta);

		*///?}else{
			for (GuiEventListener child : children()) {
				//~ if >=26.1 'render' -> 'extractRenderState'
				if (child instanceof Renderable r) r.render(g, mx, my, delta);
			}
			//?}
		if (dropdownOpen) {
			//? if >= 1.21.6{
			/*g.pose().pushMatrix();
			 *///?}else{
			g.pose().pushPose();
			//?}

			//?if >= 1.21.6{
			/*g.pose().translate(0, 0);
			 *///?}else{
			g.pose().translate(0, 0, 200);
			//?}

			renderPresetDropdown(g, mx, my);
			//? if >= 1.21.6{
			/*g.pose().popMatrix();
			 *///?}else{
			g.pose().popPose();
			//?}
		}
		if (pendingTooltip != null) {
			List<Component> lines = ComponentUtil.splitTooltipLines(pendingTooltip);
			//?if >= 1.21.6{
			/*g.setComponentTooltipForNextFrame(font, lines, tooltipX, tooltipY);
			 *///?}else{
			g.renderComponentTooltip(font, lines, tooltipX, tooltipY);
			//?}
			pendingTooltip = null;
		}
	}

	private void renderPresetDropdown(GuiGraphics g, int mx, int my) {
		List<ConfigPreset> presets = presetManager.presets();
		String activeId    = presetManager.activePresetId();
		ConfigPreset viewP = getViewingPreset();
		String viewingId   = viewP != null ? viewP.id() : activeId;

		int ddX = width - 210, ddY = TITLE_H + 24, ddW = 200;

		int visibleCount = Math.min(presets.size(), DD_MAX_VISIBLE);
		int listH = visibleCount * (DD_ENTRY_H + DD_ENTRY_GAP);
		if (visibleCount > 0) listH -= DD_ENTRY_GAP;

		int totalH = DD_PAD + listH + DD_PAD + 1 + DD_ACTION_H + DD_PAD;

		g.fill(ddX, ddY, ddX + ddW, ddY + totalH, COL_BG_BLACK);
		//? if >= 1.21.9{
		/*ScreenUtils.drawOutline(g,ddX, ddY, ddW, totalH, COL_BORDER);
		 *///?}else{
		ScreenUtils.drawOutline(g,ddX, ddY, ddW, totalH, COL_BORDER);
		//?}


		int listStartY = ddY + DD_PAD;
		int listEndY   = listStartY + listH;
		g.enableScissor(ddX, listStartY, ddX + ddW, listEndY);

		int ey = listStartY;
		for (ConfigPreset p : presets) {
			boolean active   = p.id().equals(activeId);
			boolean viewing  = p.id().equals(viewingId);
			boolean hovered  = mx >= ddX && mx < ddX + ddW && my >= ey && my < ey + DD_ENTRY_H;

			if (hovered)  g.fill(ddX + 2, ey, ddX + ddW - 2, ey + DD_ENTRY_H, 0xFF333333);
			if (viewing)  g.fill(ddX + 2, ey, ddX + 4,       ey + DD_ENTRY_H, COL_ACCENT);

			String dot = active ? "§a● " : (p.isDefault() ? "§7● §r" : "  ");
			int tc = (active || hovered || viewing) ? COL_TEXT : COL_TEXT_GRAY;
			renderScrollingText(g, dot + p.name(), ddX + 8, ey + 5, ddW - 16, tc);
			ey += DD_ENTRY_H + DD_ENTRY_GAP;
		}

		g.disableScissor();

		int divY    = ey + DD_PAD / 2;
		int actionY = divY + 2;
		g.fill(ddX + 4, divY, ddX + ddW - 4, divY + 1, COL_BORDER);

		boolean hasPendingSwitch = isViewingNonActivePreset();
		boolean canCreateMore   = presets.size() < MAX_PRESETS;

		String[] labels   = { "Apply", "Edit", "New", "Copy", "Del" };
		boolean[] disabled = {
				!hasPendingSwitch,
				false,
				!canCreateMore,
				!canCreateMore,
				presets.size() <= 1
		};

		int quarters = (ddW - DD_PAD * 2) / 5;
		for (int i = 0; i < 5; i++) {
			int ax  = ddX + DD_PAD + i * quarters;
			boolean dis = disabled[i];
			boolean hov = !dis && mx >= ax && mx < ax + quarters - 2
					&& my >= actionY && my < actionY + DD_ACTION_H;

			if (hov) g.fill(ax, actionY, ax + quarters - 2, actionY + DD_ACTION_H, 0xFF333333);
			int tc = dis ? COL_TEXT_DARK : (hov ? COL_TEXT : COL_TEXT_GRAY);
			g.drawCenteredString(font, labels[i], ax + quarters / 2, actionY + 4, tc);
		}
	}

	private void renderScrollingText(GuiGraphics g, String text, int x, int y, int maxW, int color) {
		int tw = font.width(text);
		if (tw <= maxW) { g.drawString(font, text, x, y, color, false); return; }
		int offset = (int) ((System.currentTimeMillis() / 50) % (tw + 20));
		g.enableScissor(x, y - 2, x + maxW, y + 12);
		g.drawString(font, text, x - offset, y, color, false);
		if (x - offset + tw < x + maxW)
			g.drawString(font, text, x - offset + tw + 20, y, color, false);
		g.disableScissor();
	}
	//? if >=1.21.9{
	/*@Override public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
		double mx = event.x();
		double my = event.y();
	*///?}else{

	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		//?}
		if (presetDropdownButton != null && presetDropdownButton.isMouseOver(mx, my)) {
			toggleDropdown();
			return true;
		}

		if (dropdownOpen) {
			if (handleDropdownClick(mx, my)) return true;
			dropdownOpen = false;
			updatePresetButtonText();
		}
		//? if >=1.21.9{
		/*return super.mouseClicked(event, bl);
		 *///?}else{
		return super.mouseClicked(mx, my, button);
		//?}
	}


	private boolean handleDropdownClick(double mx, double my) {
		List<ConfigPreset> presets = presetManager.presets();
		String activeId = presetManager.activePresetId();

		int ddX = width - 210, ddY = TITLE_H + 24, ddW = 200;
		int visibleCount = Math.min(presets.size(), DD_MAX_VISIBLE);
		int listH = visibleCount * (DD_ENTRY_H + DD_ENTRY_GAP);
		if (visibleCount > 0) listH -= DD_ENTRY_GAP;
		int totalH = DD_PAD + listH + DD_PAD + 1 + DD_ACTION_H + DD_PAD;

		if (mx < ddX || mx >= ddX + ddW || my < ddY || my >= ddY + totalH) return false;

		int ey = ddY + DD_PAD;
		for (ConfigPreset p : presets) {
			if (my >= ey && my < ey + DD_ENTRY_H) {
				if (hasUnsavedChanges()) {
					minecraft.setScreen(new ConfirmScreen(confirmed -> {
						if (confirmed) {
							saveChanges();
							switchToPresetView(p);
						}
						minecraft.setScreen(this);
					},
							Component.literal("Unsaved Changes"),
							Component.literal("Save changes before switching preset?"),
							Component.literal("Save & Switch"),
							Component.literal("Cancel")));
				} else {
					switchToPresetView(p);
				}
				return true;
			}
			ey += DD_ENTRY_H + DD_ENTRY_GAP;
		}

		int divY    = ey + DD_PAD / 2;
		int actionY = divY + 2;
		if (my >= actionY && my < actionY + DD_ACTION_H) {
			int quarters   = (ddW - DD_PAD * 2) / 5;
			int relX       = (int) (mx - (ddX + DD_PAD));
			if (relX < 0) return true;
			int col = relX / quarters;

			ConfigPreset viewing = getViewingPreset();

			switch (col) {
				case 0 -> {
					if (isViewingNonActivePreset()) {
						setPresetActive();
					}
				}
				case 1 -> {
					if (viewing != null) {
						minecraft.setScreen(new PresetSettingsScreen(
								this, presetManager, viewing, this::updatePresetButtonText));
					}
				}
				case 2 -> {
					if (presets.size() < MAX_PRESETS) {
						ConfigPreset newP = presetManager.createNew("New Preset");
						switchToPresetView(newP);
					}
				}
				case 3 -> {
					if (viewing != null && presets.size() < MAX_PRESETS) {
						ConfigPreset copy = presetManager.duplicate(viewing);
						switchToPresetView(copy);
					}
				}
				case 4 -> {
					if (viewing != null && presets.size() > 1) {
						presetManager.delete(viewing);
						viewingPreset = null;
						getViewingPreset();
						valuesSnapshot = snapshotValues();
						updatePresetButtonText();
						rebuildOptionList();
					}
				}
			}
			return true;
		}

		return true;
	}

	@Override
	public void onClose() {
		if (hasUnsavedChanges()) {
			minecraft.setScreen(new ConfirmScreen(
					confirmed -> {
						if (confirmed) {
							saveChanges();
						} else {
							revertToSnapshot();
						}
						viewingPreset = null;
						minecraft.setScreen(parent);
					},
					Component.literal("Unsaved Changes"),
					Component.literal("You have unsaved changes. Save before exiting?"),
					Component.literal("Save & Exit"),
					Component.literal("Discard")
			));
		} else {
			viewingPreset = null;
			minecraft.setScreen(parent);
		}
	}

	private int categoryPanelWidth() { return Math.max(100, width / 5); }

}
