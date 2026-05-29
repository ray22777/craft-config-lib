package net.tricube.CraftConfig.gui.subscreens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics ;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
//?if >=1.21.9
//import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.tricube.CraftConfig.api.v1.entries.SelectionGridEntry;
import net.tricube.CraftConfig.gui.components.SubScreen;
import net.tricube.CraftConfig.util.ScreenUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import static net.tricube.CraftConfig.gui.constants.ColorSchemes.*;

public class SelectionGridScreen extends SubScreen {
	private static final int ICON_SIZE    = 16;
	private static final int CELL_PADDING = 6;
	private static final int CELL_SPACING = 4;
	private static final int SCROLLBAR_W  = 6;
	private static final int FOOTER_H     = 36;
	private static final int CELL_SIZE = ICON_SIZE + (CELL_PADDING * 2);


	private final List<SelectionGridEntry> working;
	private final Consumer<List<SelectionGridEntry>> onSave;
	private final List<SelectionGridEntry> filtered = new ArrayList<>();
	private final boolean isBlacklist;

	private String search = "";

	private int cols;
	private int gridContentWidth;
	private int gridOffsetX;

	private SelectionGridEntry hoveredEntry = null;

	private EditBox searchBox;
	private GridList gridList;

	private boolean isDragging      = false;
	private Boolean dragTargetState = null;


	public SelectionGridScreen(Screen parent, Component title,
							   List<SelectionGridEntry> entries,
							   Consumer<List<SelectionGridEntry>> onSave,
							   boolean isBlacklist) {
		// Default size, will be overridden in init based on screen size
		super(title, parent, 460, 350);
		this.isBlacklist = isBlacklist;
		this.working = new ArrayList<>();
		for (SelectionGridEntry e : entries) this.working.add(e.copy());
		this.onSave = onSave;
	}



	@Override
	protected void init() {
		int maxW = Math.max(320, (int) (this.width * 0.70));
		int maxH = Math.max(240, (int) (this.height * 0.85));

		this.modalW = maxW;
		this.modalH = maxH;

		this.modalX = (this.width - this.modalW) / 2;
		this.modalY = (this.height - this.modalH) / 2;

		super.init();

		int effectivePadding = PADDING + 2;

		int contentX = modalX + effectivePadding;
		int contentW = modalW - (effectivePadding * 2);
		int y = modalY + HEADER_H + 5;

		searchBox = new EditBox(font, contentX, y, contentW, 20, Component.literal("Search"));
		searchBox.setHint(Component.literal("Search..."));
		searchBox.setResponder(s -> {
			if(gridList != null) gridList.setScrollAmount(0.0);
			search = s.toLowerCase(Locale.ROOT);
			rebuildGrid();
		});
		addRenderableWidget(searchBox);
		setInitialFocus(searchBox);

		int usableWidth = contentW - SCROLLBAR_W;
		this.cols = Math.max(1, (usableWidth + CELL_SPACING) / (CELL_SIZE + CELL_SPACING));

		this.gridContentWidth = cols * CELL_SIZE + (cols - 1) * CELL_SPACING;
		this.gridOffsetX = (usableWidth - gridContentWidth) / 2;

		y += 20 + 5;

		int listH = modalH - HEADER_H - 20 - FOOTER_H - (effectivePadding * 2) ;
		if (listH < CELL_SIZE) listH = CELL_SIZE + 10;

		gridList = new GridList(minecraft, contentW, listH, y, CELL_SIZE + CELL_SPACING);
		//? if >=1.21 {
				gridList.setX(contentX);
		//? } else {
				/*gridList.setLeftPos(contentX);
		*///? }
		addRenderableWidget(gridList);

		int footerBtnY = modalY + modalH - FOOTER_H + 8;

		int btnSpacing = 10;
		int btnW;
		if (minecraft.getWindow().getGuiScale() >= 4) {
			btnW = Math.min(80, (modalW - (effectivePadding * 2) - btnSpacing) / 2);
		}else{
			btnW = Math.min(120, (modalW - (effectivePadding * 2) - btnSpacing) / 2);
		}

		int totalBtnW  = (btnW * 2) + btnSpacing;
		int btnStartX  = modalX + (modalW - totalBtnW) / 2;

		addRenderableWidget(Button.builder(Component.literal("Done"), b -> saveAndClose())
				.bounds(btnStartX + btnW + btnSpacing, footerBtnY, btnW, 20).build());

		addRenderableWidget(Button.builder(Component.literal("Reset"), b -> {
			for (SelectionGridEntry e : working) e.enabled = false;
			rebuildGrid();
		}).bounds(btnStartX, footerBtnY, btnW, 20).build());
		rebuildGrid();
	}

	private void rebuildGrid() {
		filtered.clear();
		String q = search.toLowerCase(Locale.ROOT);
		for (SelectionGridEntry e : working)
			if (q.isEmpty() || e.label.toLowerCase(Locale.ROOT).contains(q))
				filtered.add(e);
		if (gridList != null) {
			gridList.rebuild(filtered);
		}
	}


//	@Override
//	public void render(GuiGraphics g, int mx, int my, float delta) { //TODO:FIX dirt bg in 1.20.1
//		hoveredEntry = null;
//		super.render(g, mx, my, delta);
//		int fy = modalY + modalH - FOOTER_H;
//
//		g.fill(modalX, fy, modalX + modalW, modalY + modalH, COL_FOOTER_BG);
//		g.fill(modalX, fy, modalX + modalW, fy + 1, COL_BORDER);
//
//		long on = working.stream().filter(SelectionGridEntry::enabled).count();
//		g.drawString(font, on + " / " + working.size(),
//				modalX + PADDING + 2, fy + 14, COL_DIM, false);
//
//		for (var child : children()) {
//			if (child instanceof AbstractWidget w && w.getY() >= fy) {
//				w.render(g, mx, my, delta);
//			}
//		}
//
//
//		if (hoveredEntry != null && !hoveredEntry.label.isEmpty()) {
//			g.renderComponentTooltip(font,
//					List.of(Component.literal(hoveredEntry.label)), mx, my);
//		}
//	}
	//~ if >=26.1 'render' -> 'extractRenderState'{
	@Override
	public void render(GuiGraphics g, int mx, int my, float delta) { //TODO:FIX dirt bg in 1.20.1
		hoveredEntry = null;
		super.render(g, mx, my, delta);

		int fy = modalY + modalH - FOOTER_H;
		long on = working.stream().filter(SelectionGridEntry::enabled).count();
		g.drawString(font, on + " / " + working.size(),
				modalX + PADDING + 2, fy + 14, COL_DIM, false);

		if (hoveredEntry != null && !hoveredEntry.label.isEmpty()) {
			//?if>=1.21.6{
			/*g.setComponentTooltipForNextFrame(font, List.of(Component.literal(hoveredEntry.label)), mx, my);
			*///?}else{
			g.renderComponentTooltip(font, List.of(Component.literal(hoveredEntry.label)), mx, my);
			//?}
		}
	}
	//~}
	@Override
	protected void renderModalBorders(GuiGraphics g) {
		super.renderModalBorders(g);
		int fy = modalY + modalH - FOOTER_H;
		g.fill(modalX + 1, fy, modalX + modalW - 1, modalY + modalH - 1, COL_FOOTER_BG);
		g.fill(modalX + 1, fy, modalX + modalW - 1, fy + 1, COL_BORDER);
	}

	//? if >=1.21.9 {
	/*@Override
	public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
		double mx = event.x();
		double my = event.y();
		int btn = event.button();

		if (isDragging && dragTargetState != null) {
			GridCell cell = gridList.getCellAt((int) mx, (int) my);
			if (cell != null && cell.entry.enabled != dragTargetState)
				cell.entry.enabled = dragTargetState;
			return true;
		}
		return super.mouseDragged(event, dx, dy);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		isDragging = false;
		dragTargetState = null;
		return super.mouseReleased(event);
	}
*///? } else {
@Override
public boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
    if (isDragging && dragTargetState != null) {
        GridCell cell = gridList.getCellAt((int) mx, (int) my);
        if (cell != null && cell.entry.enabled != dragTargetState)
            cell.entry.enabled = dragTargetState;
        return true;
    }
    return super.mouseDragged(mx, my, btn, dx, dy);
}

@Override
public boolean mouseReleased(double mx, double my, int btn) {
    isDragging = false;
    dragTargetState = null;
    return super.mouseReleased(mx, my, btn);
}
//? }



	private void saveAndClose() {
		List<SelectionGridEntry> snap = new ArrayList<>();
		for (SelectionGridEntry e : working) snap.add(e.copy());
		if (onSave != null) onSave.accept(Collections.unmodifiableList(snap));
		onClose();
	}

	private class GridList extends ObjectSelectionList<GridList.GridRow> {
		private final List<GridCell> allCells = new ArrayList<>();

		GridList(Minecraft mc, int width, int height, int top, int rowHeight) {
			//? if >=1.21 {
			super(mc, width, height, top, rowHeight);
			//? } else {
			/*super(mc, width, height, top, top + height, rowHeight);
			this.setRenderBackground(false);
			this.setRenderTopAndBottom(false);
			*///? }
		}

		void rebuild(List<SelectionGridEntry> entries) {
			clearEntries();
			allCells.clear();
			if (entries == null || entries.isEmpty()) return;

			int rows = (int) Math.ceil((double) entries.size() / cols);
			for (int r = 0; r < rows; r++) {
				List<GridCell> rowCells = new ArrayList<>();
				for (int c = 0; c < cols; c++) {
					int idx = r * cols + c;
					if (idx < entries.size()) {
						GridCell cell = new GridCell(entries.get(idx), c);
						rowCells.add(cell);
						allCells.add(cell);
					}
				}
				if (!rowCells.isEmpty()) addEntry(new GridRow(rowCells));
			}
		}
		//? if >=1.21.9 {
		/*//~ if >=26.1 'render' -> 'extract'
		@Override protected void renderSelection(GuiGraphics g, GridRow entry, int color) {}
		//~ if >=26.1 'render' -> 'extract'
		@Override protected void renderListItems(GuiGraphics g, int mx, int my, float delta) {
			int searchBottom = modalY + HEADER_H + 5 + 20 + 5; // below search box
			int footerTop = modalY + modalH - FOOTER_H;
			g.enableScissor(getX(), searchBottom, getRight(), footerTop);
			//~ if >=26.1 'render' -> 'extract'
			super.renderListItems(g, mx, my, delta);
			g.disableScissor();
		}
		*///? }
		GridCell getCellAt(int mouseX, int mouseY) {
			for (GridCell cell : allCells)
				if (cell.contains(mouseX, mouseY)) return cell;
			return null;
		}
		//? if >=1.21 {
		//~ if >=26.1 'render' -> 'extract'
		@Override protected void renderListBackground(GuiGraphics g) {}
		//~ if >=26.1 'render' -> 'extract'
		@Override protected void renderListSeparators(GuiGraphics g) {}
		//? }
		//?if< 1.21.9{
		@Override protected void renderDecorations(GuiGraphics g, int mx, int my) {}
		@Override protected void renderSelection(GuiGraphics g, int top, int w, int h, int a, int b) {}
		//?}
		//? if >=1.21.4 {
		/*@Override protected int scrollBarX() {
			return getRight() - 6;
		}
		*///? } else if >=1.21 {
		@Override protected int getScrollbarPosition() { return getRight() - 6;}
		//? } else {
		/*@Override protected int getScrollbarPosition() {
			return x1 - 6;
		}
		*///? }
		@Override public int getRowWidth() { return width - 8; }
		public int getListX() {
			//? if >=1.21 {
			return getX();
			//? } else {
			/*return x0;
			*///? }
		}
		class GridRow extends Entry<GridRow> {
			final List<GridCell> cells;

			GridRow(List<GridCell> cells) { this.cells = cells; }

			//? if >=1.21.9 {
			/*//~ if >=26.1 'render' -> 'extract'
			@Override public void renderContent(GuiGraphics g, int mx, int my, boolean hovered, float delta) {
				for (GridCell cell : cells) cell.render(g, getY(), mx, my);
			}

			@Override
			public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
				double mx = event.x();
				double my = event.y();
				int b = event.button();

				for (GridCell cell : cells) {
					if (cell.contains((int) mx, (int) my)) {
						cell.toggle();
						isDragging = true;
						dragTargetState = cell.entry.enabled;
						return true;
					}
				}
				return false;
			}
*///? } else {
@Override
public void render(GuiGraphics g, int idx, int y, int x, int w, int h,
                   int mx, int my, boolean hovered, float delta) {
    for (GridCell cell : cells) cell.render(g, y, mx, my);
}

@Override
public boolean mouseClicked(double mx, double my, int b) {
    for (GridCell cell : cells) {
        if (cell.contains((int) mx, (int) my)) {
            cell.toggle();
            isDragging = true;
            dragTargetState = cell.entry.enabled;
            return true;
        }
    }
    return false;
}
//? }

			@Override public Component getNarration() {return Component.empty();}
		}
	}

	private class GridCell {
		final SelectionGridEntry entry;
		final int col;
		int x, y;

		GridCell(SelectionGridEntry entry, int col) {
			this.entry = entry;
			this.col   = col;
		}

		void calculatePosition(int rowY) {
			//? if >=1.21 {
						this.x = gridList.getX() + gridOffsetX + col * (CELL_SIZE + CELL_SPACING);
			//? } else {
					/*this.x = gridList.getListX() + gridOffsetX + col * (CELL_SIZE + CELL_SPACING);
			*///? }
			this.y = rowY;
		}

		boolean contains(int mouseX, int mouseY) {
			return mouseX >= x && mouseX < x + CELL_SIZE
					&& mouseY >= y && mouseY < y + CELL_SIZE;
		}

		void toggle() { entry.enabled = !entry.enabled; }

		void render(GuiGraphics g, int rowY, int mx, int my) {
			calculatePosition(rowY);

			boolean hovered = contains(mx, my);
			if (hovered) SelectionGridScreen.this.hoveredEntry = entry;

			int bg, border;
			if (entry.enabled) {
				bg = hovered ? (isBlacklist ? COL_CELL_BLOCKED_HOV : COL_CELL_ENABLED_HOV)
						: (isBlacklist ? COL_CELL_BLOCKED : COL_CELL_ENABLED);
				border = isBlacklist ? COL_CELL_BLOCKED_BORDER : COL_CELL_ENABLED_BORDER;
			} else {
				bg = hovered ? COL_CELL_HOV : COL_CELL_BG;
				border = COL_CELL_BORDER_OFF;
			}

			g.fill(x, y, x + CELL_SIZE, y + CELL_SIZE, bg);
			//? if >=1.21.9{
			/*ScreenUtils.drawOutline(g, x, y, CELL_SIZE, CELL_SIZE, border);
			*///?}else{
			ScreenUtils.drawOutline(g,x, y, CELL_SIZE, CELL_SIZE, border);
			//?}
			ItemStack icon = entry.getIcon();
			if (icon != null && !icon.isEmpty())
				//~ if >=26.1 'renderItem' -> 'item'
				g.renderItem(icon, x + CELL_PADDING, y + CELL_PADDING);

		}
	}
}
