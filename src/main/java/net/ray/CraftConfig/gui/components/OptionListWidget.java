package net.ray.CraftConfig.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
//? if>=1.21.9
import net.minecraft.client.input.CharacterEvent;

import net.minecraft.network.chat.Component;
//? if >=1.21.9 {
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.KeyEvent;
//?}
import net.ray.CraftConfig.api.controller.BooleanController;
import net.ray.CraftConfig.api.controller.EnumController;
import net.ray.CraftConfig.api.controller.InputFieldController;
import net.ray.CraftConfig.api.controller.ListController;
import net.ray.CraftConfig.api.controller.OptionController;
import net.ray.CraftConfig.api.v1.ConfigOption;
import net.ray.CraftConfig.gui.ConfigScreen;
import net.ray.CraftConfig.gui.subscreens.KeybindSettingsScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static net.ray.CraftConfig.gui.constants.ColorSchemes.*;

public class OptionListWidget
		extends AbstractSelectionList<OptionListWidget.Entry> { //TODO:FIX LIST CONTROLLER in >=1.21.9

	private final List<Integer> entryHeights = new ArrayList<>();
	private final int baseItemHeight;

	public OptionListWidget(Minecraft mc, int width, int height, int top, int itemHeight) {
		//? if 1.20.1
		//super(mc, width, height, top, top + height, itemHeight);
		//? if >=1.21
		super(mc, width, height, top, itemHeight);
		this.baseItemHeight = itemHeight;
		this.setFocused(false);
	}

	@Override
	public int addEntry(Entry entry) {
		int idx = super.addEntry(entry);
		int h = (entry instanceof OptionEntry oe) ? oe.entryHeight : baseItemHeight;
		if (idx >= entryHeights.size()) {
			entryHeights.add(h);
		} else {
			entryHeights.set(idx, h);
		}
		return idx;
	}

	public void clearEntries() {
		super.clearEntries();
		entryHeights.clear();
	}

	//? if >=1.21.4 {
	@Override
	protected int contentHeight() {
		//? if >=1.21.9 {
		int total = 2;
		//?} else {
		/*int total = this.headerHeight + 2;
		 *///?}
		int count = this.getItemCount();
		for (int i = 0; i < count; i++) total += getEntryHeight(i);
		return total;
	}
	//? } else {
	/*@Override
	protected int getMaxPosition() {
		int total = this.headerHeight;
		int count = this.getItemCount();
		for (int i = 0; i < count; i++) total += getEntryHeight(i);
		return total;
	}
	*///? }

	public int getEntryHeight(int index) {
		Entry e = (index >= 0 && index < children().size()) ? children().get(index) : null;
		if (e instanceof OptionEntry oe) {
			return oe.controller.rowHeight((ConfigOption) oe.option, baseItemHeight);
		}
		return baseItemHeight;
	}

	@Override
	public int getRowTop(int index) {
		//? if >=1.21.9 {
		int top = this.getY() + 4 - (int) this.scrollAmount();
		//?} else if >=1.21.4 {
		/*int top = this.getY() + 4 - (int) this.scrollAmount() + this.headerHeight;
		 *///? } else if >=1.21 {
		/*int top = this.getY() + 4 - (int) this.getScrollAmount() + this.headerHeight;
		 *///? } else {
		/*int top = this.y0 + 4 - (int) this.getScrollAmount() + this.headerHeight;
		 *///? }

		for (int i = 0; i < index; i++) {
			top += getEntryHeight(i);
		}
		return top;
	}

	@Override
	public int getRowBottom(int index) {
		return getRowTop(index) + getEntryHeight(index);
	}

	private int getEntryIndexAt(double mx, double my) {
		int x = this.getRowLeft();
		int w = this.getRowWidth();
		if (mx < x || mx >= x + w) return -1;
		int count = this.getItemCount();
		for (int i = 0; i < count; i++) {
			int top    = this.getRowTop(i);
			int height = getEntryHeight(i);
			if (my >= top && my < top + height) return i;
		}
		return -1;
	}

	//? if >=1.21.9 {
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double mx = event.x();
		double my = event.y();
		if (this.scrollBarX() <= mx && mx < this.scrollBarX() + 6) {
			return super.mouseClicked(event, doubleClick);
		}
		if (!this.isMouseOver(mx, my)) return false;
		int idx = getEntryIndexAt(mx, my);
		if (idx < 0) return false;
		Entry e = this.children().get(idx);
		this.setFocused(e);
		if (e.mouseClicked(event, doubleClick)) return true;
		return false;
	}

	//? } else {
	/*@Override
	public boolean mouseClicked(double mx, double my, int button) {
		//? if >=1.21.4 {
		if (this.scrollBarX() <= mx && mx < this.scrollBarX() + 6) {
			//? } else {
			/^if (this.getScrollbarPosition() <= mx && mx < this.getScrollbarPosition() + 6) {
			^///? }
			return super.mouseClicked(mx, my, button);
		}
		if (!this.isMouseOver(mx, my)) return false;
		int idx = getEntryIndexAt(mx, my);
		if (idx < 0) return false;
		Entry e = this.getEntry(idx);
		this.setFocused(e);
		if (e.mouseClicked(mx, my, button)) return true;
		return false;
	}
	*///? }

	//? if >=1.21.9 {
	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		double mx = event.x();
		double my = event.y();
		int idx = getEntryIndexAt(mx, my);
		if (idx >= 0) {
//? if >=1.21.9 {
			Entry e = this.children().get(idx);
//? } else {
			/*Entry e = this.getEntry(idx);
			 *///? }
			if (e.mouseReleased(event)) return true;
		}
		return super.mouseReleased(event);
	}
	//? } else {
	/*@Override
	public boolean mouseReleased(double mx, double my, int button) {
		int idx = getEntryIndexAt(mx, my);
		if (idx >= 0) {
			Entry e = this.getEntry(idx);
			if (e.mouseReleased(mx, my, button)) return true;
		}
		return super.mouseReleased(mx, my, button);
	}
	*///? }

	//? if >=1.21.9 {
	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
		double mx = event.x();
		double my = event.y();
		if (this.scrollBarX() <= mx && mx < this.scrollBarX() + 6) {
			return super.mouseDragged(event, dx, dy);
		}
		//?if>=26.1{
		/*Entry e = getFocused();
		if (e != null) {
		*///?}else{
			if (getFocused() instanceof Entry e) {
		//?}
		//~ if >=26.1 'getFocused() instanceof Entry e' -> 'Entry e = getFocused(); if (e != null)'

			return e.mouseDragged(event, dx, dy);
		}
		return false;
	}
	//? } else {
	/*@Override
	public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
		//? if >=1.21.4 {
		if (this.scrollBarX() <= mx && mx < this.scrollBarX() + 6) {
			//? } else {
			/^if (this.getScrollbarPosition() <= mx && mx < this.getScrollbarPosition() + 6) {
			^///? }
			return super.mouseDragged(mx, my, button, dx, dy);
		}
		//? if >=1.21 {
		if (getFocused() instanceof Entry e) {
			return e.mouseDragged(mx, my, button, dx, dy);
		}
		//? } else {
		/^if (getFocused() instanceof Entry) {
			Entry e = (Entry) getFocused();
			return e.mouseDragged(mx, my, button, dx, dy);
		}
		^///? }
		return false;
	}
	*///? }

	//? if >=1.21.9 {
	@Override
	public boolean mouseScrolled(double mx, double my, double hScroll, double vScroll) {
		//?if>=26.1{
		/*Entry e = getFocused();
		if (e != null) {
			*///?}else{
				if (getFocused() instanceof Entry e) {
			//?}
			if (e instanceof OptionEntry oe && oe.controller.ownsRow()) {
				if (e.mouseScrolled(mx, my, vScroll)) return true;
			}
		}
		return super.mouseScrolled(mx, my, hScroll, vScroll);
	}
	//? } else {
	/*public boolean mouseScrolled(double mx, double my, double scroll) {
		//? if >=1.21 {
		if (getFocused() instanceof Entry e) {
			if (e instanceof OptionEntry oe && oe.controller.ownsRow()) {
				if (e.mouseScrolled(mx, my, scroll)) return true;
			}
		}
		return super.mouseScrolled(mx, my, 0, scroll);
		//? } else {
		/^if (getFocused() instanceof Entry) {
			Entry e = (Entry) getFocused();
			if (e instanceof OptionEntry oe && oe.controller.ownsRow()) {
				if (e.mouseScrolled(mx, my, scroll)) return true;
			}
		}
		return super.mouseScrolled(mx, my, scroll);
		^///? }
	}
	*///? }

	//? if >=1.21.9 {
	@Override
	public boolean keyPressed(KeyEvent keyEvent) {
		//?if>=26.1{
		/*Entry e = getFocused();
		if (e != null)
			*///?}else{
				if (getFocused() instanceof Entry e)
			//?}
			return e.keyPressed(keyEvent);
		return false;
	}
	//? } else if >=1.21 {
	/*@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (getFocused() instanceof Entry e) return e.keyPressed(keyCode, scanCode, modifiers);
		return false;
	}
	*///? } else {
	/*@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (getFocused() instanceof Entry) {
			Entry e = (Entry) getFocused();
			return e.keyPressed(keyCode, scanCode, modifiers);
		}
		return false;
	}
	*///? }

	//? if >=1.21.9 {
	@Override
	public boolean keyReleased(KeyEvent keyEvent) {
		//?if>=26.1{
		/*Entry e = getFocused();
		if (e != null)
			*///?}else{
				if (getFocused() instanceof Entry e)
			//?}
			return e.keyReleased(keyEvent);
		return false;
	}

	@Override
	public boolean charTyped(CharacterEvent characterEvent) {
		//?if>=26.1{
		/*Entry e = getFocused();
		if (e != null)
			*///?}else{
				if (getFocused() instanceof Entry e)
			//?}
			return e.charTyped(characterEvent);
		return false;
	}
	//? } else if >=1.21 {
	/*@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		if (getFocused() instanceof Entry e) return e.keyReleased(keyCode, scanCode, modifiers);
		return false;
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (getFocused() instanceof Entry e) return e.charTyped(chr, modifiers);
		return false;
	}
	*///? } else {
	/*@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		if (getFocused() instanceof Entry) {
			Entry e = (Entry) getFocused();
			return e.keyReleased(keyCode, scanCode, modifiers);
		}
		return false;
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (getFocused() instanceof Entry) {
			Entry e = (Entry) getFocused();
			return e.charTyped(chr, modifiers);
		}
		return false;
	}
	*///? }

	//? if >=1.21 {
	//~ if >=26.1 'render' -> 'extract'
	@Override protected void renderListSeparators(GuiGraphics g) {}
	//? }

	//? if >=1.21.9 {
	//? } else {
	/*@Override
	protected void renderDecorations(GuiGraphics g, int mx, int my) {}
	*///? }

	// renderSelection signature changed in 1.21.9 to take (Gui, E entry, int color)
	//? if >=1.21.9 {
	//~ if >=26.1 'render' -> 'extract'
	@Override protected void renderSelection(GuiGraphics g, Entry entry, int color) {}
	//? } else {
	/*@Override
	protected void renderSelection(GuiGraphics g, int top, int w, int h, int a, int b) {}
	*///? }

	@Override
	public int getRowLeft() {
		//? if >=1.21 {
		return getX() + 8;
		//? } else {
		/*return x0 + 8;
		 *///? }
	}

	@Override
	public int getRowWidth() {
		return width - 16;
	}

	//? if >=1.21.4 {
	@Override
	protected int scrollBarX() {
		return getRight() - 6;
	}
	//? } else {
	/*@Override
	protected int getScrollbarPosition() {
		//? if >=1.21 {
		return getRight() - 6;
		//? } else {
		/^return x1 - 6;
		^///? }
	}
	*///? }

	//? if >=1.21 {
	@Override protected void updateWidgetNarration(NarrationElementOutput out) {}
	//? } else{
	/*@Override public void updateNarration(NarrationElementOutput narrationElementOutput) {}
	*///?}



	abstract static class Entry extends AbstractSelectionList.Entry<Entry> {
		abstract void doRender(GuiGraphics g, int idx, int y, int x, int w, int h,
							   int mx, int my, boolean hovered, float delta);

		//? if >=1.21.9 {
		//~ if >=26.1 'render' -> 'extract'
		@Override public void renderContent(GuiGraphics g, int mx, int my, boolean hovered, float delta) {
			doRender(g, 0, this.getY(), this.getX(), this.getWidth(), this.getHeight(),
					mx, my, hovered, delta);
		}
		//? } else {
		/*@Override
		public void render(GuiGraphics g, int idx, int y, int x, int w, int h,
						   int mx, int my, boolean hovered, float delta) {
			doRender(g, idx, y, x, w, h, mx, my, hovered, delta);
		}
		*///? }

		// Mouse event abstracts — signature differs per version
		//? if >=1.21.9 {
		public abstract boolean mouseClicked(MouseButtonEvent event, boolean doubleClick);
		public abstract boolean mouseReleased(MouseButtonEvent event);
		public abstract boolean mouseDragged(MouseButtonEvent event, double dx, double dy);
		//? } else {
		/*public abstract boolean mouseClicked(double mx, double my, int button);
		public abstract boolean mouseReleased(double mx, double my, int button);
		public abstract boolean mouseDragged(double mx, double my, int button, double dx, double dy);
		*///? }

		public abstract boolean mouseScrolled(double mx, double my, double scroll);
	}

	public static class SectionEntry extends Entry {
		private final String name;
		private final boolean collapsed;
		private final Runnable onToggle;

		public SectionEntry(String name, boolean collapsed, Runnable onToggle) {
			this.name = name; this.collapsed = collapsed; this.onToggle = onToggle;
		}

		@Override
		void doRender(GuiGraphics g, int idx, int y, int x, int w, int h,
					  int mx, int my, boolean hovered, float delta) {
			if (hovered) g.fill(x, y, x + w, y + h, COL_BG_HOVER);
			g.drawString(Minecraft.getInstance().font,
					(collapsed ? "▶ " : "▼ ") + name,
					x + 4, y + (h - 8) / 2, COL_ACCENT, false);
			g.fill(x, y + h - 1, x + w, y + h, COL_BORDER_DARK);
		}

		//? if >=1.21.9 {
		@Override
		public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
			onToggle.run(); return true;
		}

		@Override public boolean mouseReleased(MouseButtonEvent event) { return false; }
		@Override public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) { return false; }
		//? } else {
		/*@Override
		public boolean mouseClicked(double mx, double my, int button) {
			onToggle.run(); return true;
		}

		@Override public boolean mouseReleased(double mx, double my, int button) { return false; }
		@Override public boolean mouseDragged(double mx, double my, int button, double dx, double dy) { return false; }
		*///? }

		@Override
		public boolean mouseScrolled(double mx, double my, double scroll) { return false; }
	}

	public static class GroupHeaderEntry extends Entry {
		private final String breadcrumb;
		public GroupHeaderEntry(String bc) { this.breadcrumb = bc; }

		@Override
		void doRender(GuiGraphics g, int idx, int y, int x, int w, int h,
					  int mx, int my, boolean hovered, float delta) {
			g.drawString(Minecraft.getInstance().font, breadcrumb,
					x + 4, y + 6, COL_TEXT_GRAY, false);
			g.fill(x, y + h - 1, x + w, y + h, COL_BORDER_DARK);
		}

		//? if >=1.21.9 {
		@Override public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) { return false; }
		@Override public boolean mouseReleased(MouseButtonEvent event) { return false; }
		@Override public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) { return false; }
		//? } else {
		/*@Override public boolean mouseClicked(double mx, double my, int b) { return false; }
		@Override public boolean mouseReleased(double mx, double my, int b) { return false; }
		@Override public boolean mouseDragged(double mx, double my, int b, double dx, double dy) { return false; }
		*///? }

		@Override public boolean mouseScrolled(double mx, double my, double scroll) { return false; }
	}

	public static class OptionEntry extends Entry {
		final ConfigOption<?> option;
		private final Runnable onReset;
		private final Screen parentScreen;
		final OptionController<?> controller;

		int entryHeight;

		private final AbstractWidget optionWidget;
		private final Button resetButton;
		private final Button keybindButton;

		private static final int WIDGET_W    = 150;
		private static final int RESET_BTN_W = 50;
		private static final int KB_BTN_W    = 20;
		private static final int BASE_ROW_H  = 24;
		int lastRenderX, lastRenderY, lastRenderW, lastRenderH;

		@SuppressWarnings({"unchecked", "rawtypes"})
		public OptionEntry(ConfigOption option, Runnable onReset, Screen parentScreen) {
			this.option       = option;
			this.onReset      = onReset;
			this.parentScreen = parentScreen;
			this.controller   = resolveController(option);

			this.entryHeight = controller.rowHeight(option, BASE_ROW_H);

			if (controller.ownsRow()) {
				this.optionWidget  = null;
				this.resetButton   = null;
				this.keybindButton = null;
			} else {
				this.optionWidget = controller.createWidget(option, 0, 0, WIDGET_W, 20);
				this.resetButton  = Button.builder(Component.literal("Reset"),
								btn -> onReset.run())
						.bounds(0, 0, RESET_BTN_W, 20).build();

				if (option.type() == ConfigOption.Type.BOOLEAN
						&& option.keybindSettings() != null) {
					this.keybindButton = Button.builder(
							Component.literal("\uD83C\uDFAE"),
							btn -> Minecraft.getInstance().setScreen(
									new KeybindSettingsScreen(parentScreen,
											(ConfigOption<Boolean>) option, null))
					).bounds(0, 0, KB_BTN_W, 20).build();
				} else {
					this.keybindButton = null;
				}
			}
		}

		@SuppressWarnings({"unchecked", "rawtypes"})
		@Override
		void doRender(GuiGraphics g, int idx, int y, int x, int w, int h,
					  int mx, int my, boolean hovered, float delta) {
			lastRenderX = x; lastRenderY = y; lastRenderW = w; lastRenderH = h;

			if (controller.ownsRow()) {
				controller.renderRow(g, (ConfigOption) option, x, y, w, h, mx, my, hovered, delta);
				return;
			}

			if (hovered && !isEditing())
				g.fill(x, y, x + w, y + h, COL_BG_HOVER);

			if (hovered && option.description() != null && parentScreen instanceof ConfigScreen cs) {
				cs.setTooltip(option.description(), mx, my);
			}

			int resetX  = x + w - RESET_BTN_W - 4;
			int widgetX = resetX - WIDGET_W - 4;
			int kbX     = (keybindButton != null) ? widgetX - KB_BTN_W - 2 : widgetX;
			if (keybindButton != null) widgetX = kbX + KB_BTN_W + 2;

			int wY     = y + (h - 20) / 2;
			int labelY = y + (h - Minecraft.getInstance().font.lineHeight) / 2;

			optionWidget.setX(widgetX); optionWidget.setY(wY);
			resetButton.setX(resetX);   resetButton.setY(wY);

			controller.renderLabel(g, Minecraft.getInstance().font, (ConfigOption) option,
					x + 4, labelY, hovered ? COL_TEXT : COL_TEXT_GRAY);
			//~ if >=26.1 'render' -> 'extractRenderState'{
			if (keybindButton != null) {
				keybindButton.setX(kbX); keybindButton.setY(wY);
				keybindButton.setAlpha(option.keybindSettings().isEnabled() ? 1f : 0.4f);
				keybindButton.render(g, mx, my, delta);
			}
			optionWidget.render(g, mx, my, delta);
			resetButton.active = !Objects.equals(option.get(), option.getDefault());
			resetButton.render(g, mx, my, delta);
			//~}
		}

		private boolean isEditing() {
			return optionWidget instanceof EditBox eb && eb.isFocused();
		}

		//? if >=1.21.9 {
		@SuppressWarnings({"unchecked", "rawtypes"})
		@Override
		public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
			double mx = event.x();
			double my = event.y();
			int button = event.button();
			if (controller.ownsRow()) {
				return controller.handleMouseClicked((ConfigOption) option, event, doubleClick,
						lastRenderX, lastRenderY, lastRenderW, lastRenderH);
			}
			if (keybindButton != null && keybindButton.isMouseOver(mx, my))
				return keybindButton.mouseClicked(event, doubleClick);
			if (resetButton != null && resetButton.isMouseOver(mx, my))
				return resetButton.mouseClicked(event, doubleClick);
			if (optionWidget != null && optionWidget.isMouseOver(mx, my)) {
				if (optionWidget instanceof EditBox eb) eb.setFocused(true);
				return optionWidget.mouseClicked(event, doubleClick);
			}
			if (optionWidget instanceof EditBox eb) eb.setFocused(false);
			return false;
		}

		@Override
		public boolean mouseReleased(MouseButtonEvent event) {
			double mx = event.x();
			double my = event.y();
			if (controller.ownsRow()) {
				return controller.handleMouseReleased((ConfigOption) option, event);
			}
			if (optionWidget != null && optionWidget.isMouseOver(mx, my)) {
				return optionWidget.mouseReleased(event);
			}
			return false;
		}

		@Override
		public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
			double mx = event.x();
			double my = event.y();
			if (controller.ownsRow()) {
				return controller.handleMouseDragged((ConfigOption) option, event, dx, dy);
			}
			if (optionWidget != null && optionWidget.isMouseOver(mx, my)) {
				return optionWidget.mouseDragged(event, dx, dy);
			}
			return false;
		}
		//? } else {
		/*@SuppressWarnings({"unchecked", "rawtypes"})
		@Override
		public boolean mouseClicked(double mx, double my, int button) {
			if (controller.ownsRow()) {
				return controller.handleMouseClicked((ConfigOption) option, mx, my, button,
						lastRenderX, lastRenderY, lastRenderW, lastRenderH);
			}
			if (keybindButton != null && keybindButton.isMouseOver(mx, my))
				return keybindButton.mouseClicked(mx, my, button);
			if (resetButton != null && resetButton.isMouseOver(mx, my))
				return resetButton.mouseClicked(mx, my, button);
			if (optionWidget != null && optionWidget.isMouseOver(mx, my)) {
				if (optionWidget instanceof EditBox eb) eb.setFocused(true);
				return optionWidget.mouseClicked(mx, my, button);
			}
			if (optionWidget instanceof EditBox eb) eb.setFocused(false);
			return false;
		}

		@Override
		public boolean mouseReleased(double mx, double my, int button) {
			if (controller.ownsRow()) {
				return controller.handleMouseReleased((ConfigOption) option, mx, my, button);
			}
			if (optionWidget != null && optionWidget.isMouseOver(mx, my)) {
				return optionWidget.mouseReleased(mx, my, button);
			}
			return false;
		}

		@Override
		public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
			if (controller.ownsRow()) {
				return controller.handleMouseDragged((ConfigOption) option, mx, my, button, dx, dy);
			}
			if (optionWidget != null && optionWidget.isMouseOver(mx, my)) {
				return optionWidget.mouseDragged(mx, my, button, dx, dy);
			}
			return false;
		}
		*///? }

		@Override
		public boolean mouseScrolled(double mx, double my, double scroll) {
			if (controller.ownsRow()) {
				return controller.handleMouseScrolled((ConfigOption) option, mx, my, scroll);
			}
			if (optionWidget != null && optionWidget.isMouseOver(mx, my)) {
				return optionWidget.mouseScrolled(mx, my/*? if >=1.21 {*/ , 0/*?}*/, scroll);
			}
			return false;
		}

//? if >=1.21.9 {
		@SuppressWarnings({"unchecked", "rawtypes"})
		@Override
		public boolean keyPressed(KeyEvent keyEvent) {
			if (controller.ownsRow()) {
				return controller.handleKeyPressed((ConfigOption) option, keyEvent);
			}
			if (optionWidget instanceof EditBox eb && eb.isFocused()) {
				return eb.keyPressed(keyEvent);
			}
			return false;
		}

		@SuppressWarnings({"unchecked", "rawtypes"})
		@Override
		public boolean keyReleased(KeyEvent keyEvent) {
			if (controller.ownsRow()) {
				return controller.handleKeyReleased((ConfigOption) option, keyEvent);
			}
			if (optionWidget instanceof EditBox eb && eb.isFocused()) {
				return eb.keyReleased(keyEvent);
			}
			return false;
		}

		@SuppressWarnings({"unchecked", "rawtypes"})
		@Override
		public boolean charTyped(CharacterEvent characterEvent) {
			if (controller.ownsRow()) {
				return controller.handleCharTyped((ConfigOption) option, characterEvent);
			}
			if (optionWidget instanceof EditBox eb && eb.isFocused()) {
				return eb.charTyped(characterEvent);
			}
			return false;
		}
		//? } else {
		/*@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (controller.ownsRow()) {
				return controller.handleKeyPressed((ConfigOption) option, keyCode, scanCode, modifiers);
			}
			if (optionWidget instanceof EditBox eb && eb.isFocused()) {
				return eb.keyPressed(keyCode, scanCode, modifiers);
			}
			return false;
		}

		@SuppressWarnings({"unchecked", "rawtypes"})
		@Override
		public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
			if (controller.ownsRow()) {
				return controller.handleKeyReleased((ConfigOption) option, keyCode, scanCode, modifiers);
			}
			if (optionWidget instanceof EditBox eb && eb.isFocused()) {
				return eb.keyReleased(keyCode, scanCode, modifiers);
			}
			return false;
		}

		@SuppressWarnings({"unchecked", "rawtypes"})
		@Override
		public boolean charTyped(char chr, int modifiers) {
			if (controller.ownsRow()) {
				return controller.handleCharTyped((ConfigOption) option, chr, modifiers);
			}
			if (optionWidget instanceof EditBox eb && eb.isFocused()) {
				return eb.charTyped(chr, modifiers);
			}
			return false;
		}
		*///? }

		@Override
		public void setFocused(boolean focused) {
			super.setFocused(focused);
			if (!focused && optionWidget instanceof EditBox eb) {
				eb.setFocused(false);
			}
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static OptionController resolveController(ConfigOption<?> opt) {
		if (opt.controller() != null) return opt.controller();
		return switch (opt.type()) {
			case BOOLEAN -> new BooleanController();
			case INTEGER, DOUBLE, FLOAT, STRING, COLOR -> new InputFieldController();
			case ENUM -> new EnumController();
			case LIST -> new ListController();
		};
	}
}
