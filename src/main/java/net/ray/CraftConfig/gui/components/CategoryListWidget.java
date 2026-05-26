package net.ray.CraftConfig.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics ;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import static net.ray.CraftConfig.gui.constants.ColorSchemes.*;
//?if >=1.21.9
import net.minecraft.client.input.MouseButtonEvent;


public class CategoryListWidget
			extends AbstractSelectionList<CategoryListWidget.CategoryEntry> {
		public int currentSelectedIndex = 0;

		public CategoryListWidget(Minecraft mc, int width, int height, int top, int itemHeight) {
			//? if 1.20.1
			//super(mc, width, height, top, top + height, itemHeight);
			//? if >=1.21
			super(mc, width, height, top, itemHeight);
			this.setFocused(true);
		}

		//		@Override protected void renderListSeparators(GuiGraphics g) {}
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
		/*@Override protected int getScrollbarPosition() {
			//? if >=1.21 {
			return getRight() - 6;
			//? } else {
			/^return x1 - 6;
			^///? }
		}
		*///? }
		//?if 1.20.1
		//@Override public void updateNarration(NarrationElementOutput narrationElementOutput) {}
		//?if >=1.21
		@Override
		protected void updateWidgetNarration(NarrationElementOutput out) {
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

			CategoryEntry entry = this.getEntryAtPosition(mx, my);
			if (entry != null) {
				if (entry.mouseClicked(event, doubleClick)) {
					return true;
				}
			}

			return super.mouseClicked(event, doubleClick);
		}
		//? } else {

		/*@Override
		public boolean mouseClicked(double mx, double my, int button) {
			//?if>=1.21.9{
			if (this.getScrollbarPosition() <= mx && mx < this.getScrollbarPosition() + 6) {
			//?}else if >=1.21.4{
			/^if (this.scrollBarX() <= mx && mx < scrollBarX()  + 6) {
			^///?} else{
			/^if (this.getScrollbarPosition() <= mx && mx < this.getScrollbarPosition() + 6) {
			^///? }
				return super.mouseClicked(mx, my, button);

			}

			if (!this.isMouseOver(mx, my)) return false;

			CategoryEntry entry = this.getEntryAtPosition(mx, my);
			if (entry != null) {
				if (entry.mouseClicked(mx, my, button)) {
					return true;
				}
			}

			return super.mouseClicked(mx, my, button);
		}
		*///? }
		//?if < 1.21.9
		//@Override
		protected void renderDecorations(GuiGraphics g, int mx, int my) {
		}

		//? if >=1.21 {
		//~ if >=26.1 'render' -> 'extract'
		@Override protected void renderListSeparators(GuiGraphics g) {
		}

		//? }
		@Override
		public int addEntry(CategoryEntry entry) {
			int index = super.addEntry(entry);
			if (index == currentSelectedIndex) {
				setSelected(entry);
				centerScrollOn(entry);
			}
			return index;
		}


		public static class CategoryEntry extends AbstractSelectionList.Entry<CategoryEntry> {
			private final String name;
			private final int index;
			private final CategoryListWidget parent;
			private final Runnable onClick;

			public CategoryEntry(String name, int index, CategoryListWidget parent, Runnable onClick) {
				this.name = name;
				this.index = index;
				this.parent = parent;
				this.onClick = onClick;
			}

			//? if >=1.21.9 {
			//~ if >=26.1 'render' -> 'extract'
			@Override public void renderContent(GuiGraphics g, int mx, int my, boolean hovered, float delta) {
				boolean selected = parent.currentSelectedIndex == index;
				if (hovered) {
					g.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), COL_BG_HOVER);
				}
				int color = (selected || hovered) ? COL_TEXT : COL_TEXT_GRAY;
				g.drawString(Minecraft.getInstance().font, name,
						getContentX() + 8, getContentY() + (getContentHeight() - 8) / 2, color, false);
			}
			//? } else {
			/*@Override
			public void render(GuiGraphics g, int idx, int y, int x, int w, int h,
							   int mx, int my, boolean hovered, float delta) {
				boolean selected = parent.currentSelectedIndex == index;
				if (hovered) g.fill(x, y - 1, x + w, y + h + 1, COL_BG_HOVER);
				int color = (selected || hovered) ? COL_TEXT : COL_TEXT_GRAY;
				g.drawString(Minecraft.getInstance().font, name,
						x + 8, y + (h - 8) / 2, color, false);
			}
			*///? }

			//? if >=1.21.9 {
			@Override
			public boolean mouseClicked(MouseButtonEvent event, boolean bool) {
				parent.currentSelectedIndex = index;
				parent.setSelected(this);
				onClick.run();
				return true;
			}
			//? } else {
			/*@Override
			public boolean mouseClicked(double mx, double my, int button) {
				parent.currentSelectedIndex = index;
				parent.setSelected(this);
				onClick.run();
				return true;
			}
			*///? }
		}
	}
