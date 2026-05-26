// net/ray/CraftConfig/api/controller/ListController.java
package net.ray.CraftConfig.api.controller;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics ;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
//? if >=1.21.9{
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//?}
import net.minecraft.network.chat.Component;
import net.ray.CraftConfig.api.v1.ConfigOption;
import net.ray.CraftConfig.gui.ConfigScreen;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ListController<T> implements OptionController<List<T>> {
	private Button resetButton = null;

	private static final int BASE_H       = 24;
	private static final int ITEM_H       = 18;
	private static final int ADD_ROW_H    = 18;
	private static final int DELETE_BTN_W = 14;
	private static final int PAD          = 2;
	private static final int LPAD          = 3;
	private static final int COL_BG_HOVER   = 0x20FFFFFF;
	private static final int COL_BORDER_DIM = 0xFF4A4A4A;
	private static final int COL_TEXT       = 0xFFFFFFFF;
	private static final int COL_TEXT_GRAY  = 0xFFAAAAAA;
	private static final int COL_TEXT_ERROR = 0xFFFF6060;
	private static final int COL_EDIT_BG    = 0x30FFFFFF;
	private static final int COL_EDIT_ERR   = 0x40FF4040;

	public enum ElementType { STRING, INTEGER, LONG, FLOAT, DOUBLE }
	private final ElementType elementType;

	private boolean expanded = false;
	private boolean dirty    = true;

	private final List<EditBox> editBoxes     = new ArrayList<>();
	private final List<Button>  deleteButtons = new ArrayList<>();
	private final List<Boolean> parseErrors   = new ArrayList<>();
	private Button addButton = null;

	public ListController() { this.elementType = null; }
	public ListController(ElementType elementType) { this.elementType = elementType; }

	@Override
	public AbstractWidget createWidget(ConfigOption<List<T>> option, int x, int y, int w, int h) {
		return new EditBox(Minecraft.getInstance().font, x, y, w, h, Component.empty());
	}

	@Override public boolean ownsRow() { return true; }

	@Override
	public int rowHeight(ConfigOption<List<T>> option, int baseRowHeight) {
		if (!expanded) return BASE_H;
		return BASE_H + (ITEM_H * safeList(option).size()) + ADD_ROW_H;
	}

	@Override
	public void renderRow(GuiGraphics g, ConfigOption<List<T>> option,
						  int x, int y, int w, int h,
						  int mx, int my, boolean hovered, float delta) {
		Font font = Minecraft.getInstance().font;

		boolean headerHovered = my >= y && my < y + BASE_H && mx >= x && mx < x + w;
		if (headerHovered) g.fill(x, y, x + w, y + BASE_H, COL_BG_HOVER);

		g.drawString(font, (expanded ? "▽ " : "▷ ") + option.name().getString(),
				x + PAD + LPAD, y + (BASE_H - font.lineHeight) / 2,
				hovered ? COL_TEXT : COL_TEXT_GRAY, false);

		List<?> list = safeList(option);
		ElementType et = resolvedType(option);


		if (expanded) {
			g.fill(x, y + BASE_H - 1, x + w, y + BASE_H, COL_BORDER_DIM);
		}
		if (!expanded) return;

		if (dirty) rebuildWidgets(option);

		for (int i = 0; i < editBoxes.size(); i++) {
			int rowY       = y + BASE_H + i * ITEM_H;
			int widgetY    = rowY + (ITEM_H - 14) / 2;
			boolean rowHov = my >= rowY && my < rowY + ITEM_H && mx >= x && mx < x + w;
			boolean error  = i < parseErrors.size() && parseErrors.get(i);

			if (rowHov) g.fill(x, rowY, x + w, rowY + ITEM_H, COL_BG_HOVER);

			EditBox eb = editBoxes.get(i);
			Button db = deleteButtons.get(i);
			int deleteX = x + w - DELETE_BTN_W - PAD;
			int editW = w - DELETE_BTN_W - PAD * 3;

			//? if >=1.21 {
						eb.setX(x + PAD + LPAD);
						eb.setY(widgetY);
						eb.setWidth(editW);
						eb.setHeight(14);

						db.setX(deleteX);
						db.setY(widgetY);
						db.setWidth(DELETE_BTN_W);
						db.setHeight(14);
			//? } else {
						/*eb.setX(x + PAD + LPAD);
						eb.setY(widgetY);
						eb.setWidth(editW);

						db.setX(deleteX);
						db.setY(widgetY);
						db.setWidth(DELETE_BTN_W);
			*///? }
			eb.setTextColor(error ? COL_TEXT_ERROR : COL_TEXT);

			g.fill(x + PAD + LPAD - 1, widgetY - 1, x + PAD + editW + 1, widgetY + 15,
					error ? COL_EDIT_ERR : COL_EDIT_BG);
			//~ if >=26.1 'render' -> 'extractWidgetRenderState'
			eb.render(g, mx, my, delta);
			//~ if >=26.1 'render' -> 'extractRenderState'
			db.render(g, mx, my, delta);
		}
		int resetX = x + w - 50 - PAD;
		int resetY = y + (BASE_H - 14) / 2;
		if (resetButton == null) {
			resetButton = Button.builder(Component.literal("Reset"), btn -> {
				option.set(option.getDefault());
				dirty = true;
			}).bounds(0, 0, 50, 20).build();
		}
		resetButton.setX(resetX-2);
		resetButton.setY(resetY-3);
		resetButton.active = !Objects.equals(option.get(), option.getDefault());
		//~ if >=26.1 'render' -> 'extractRenderState'
		resetButton.render(g, mx, my, delta);
		String badge = "§8[" + StringUtils.capitalize(resolvedType(option).name().toLowerCase()) + "]§r";
		g.drawString(font, badge, resetX - font.width(badge) - PAD * 3, y + (BASE_H - font.lineHeight) / 2, COL_TEXT_GRAY, false);
		if (addButton != null) {
			int addRowY = y + BASE_H + editBoxes.size() * ITEM_H;
			int widgetY = addRowY + (ADD_ROW_H - 14) / 2;
			boolean addRowHov = my >= addRowY && my < addRowY + ADD_ROW_H && mx >= x && mx < x + w;
			if (addRowHov) g.fill(x - 4, addRowY, x + w, addRowY + ADD_ROW_H, COL_BG_HOVER);

			addButton.setX(x + PAD + LPAD); addButton.setY(widgetY);
			addButton.setWidth(40);
			//? if >=1.21
			addButton.setHeight(14); //fix for 1.20.1
			//~ if >=26.1 'render' -> 'extractRenderState'
			addButton.render(g, mx, my, delta);
		}
	}
	//? if >=1.21.9 {
	@Override
	public boolean handleMouseClicked(ConfigOption<List<T>> option,
									  MouseButtonEvent event, boolean doubleClick,
									  int rowX, int rowY, int rowW, int rowH) {
		double mx = event.x();
		double my = event.y();
		int button = event.button();

		if (my >= rowY && my < rowY + BASE_H) {
			if (resetButton != null && resetButton.isMouseOver(mx, my)) {
				return resetButton.mouseClicked(event, doubleClick);
			}
			if (expanded) editBoxes.forEach(eb -> eb.setFocused(false));
			expanded = !expanded;
			if (Minecraft.getInstance().screen instanceof ConfigScreen) {
				ConfigScreen cs = (ConfigScreen) Minecraft.getInstance().screen;
				cs.refreshOptionList();
			}
			dirty = true;
			return true;
		}
		if (!expanded) return false;

		if (addButton != null && addButton.isMouseOver(mx, my))
			return addButton.mouseClicked(event, doubleClick);

		for (Button db : deleteButtons)
			if (db.isMouseOver(mx, my)) return db.mouseClicked(event, doubleClick);

		boolean consumed = false;
		for (EditBox eb : editBoxes) {
			if (eb.isMouseOver(mx, my)) {
				eb.setFocused(true);
				eb.mouseClicked(event, doubleClick);
				consumed = true;
			} else {
				eb.setFocused(false);
			}
		}
		return consumed;
	}

	@Override
	public boolean handleMouseReleased(ConfigOption<List<T>> option, MouseButtonEvent event) {
		double mx = event.x();
		double my = event.y();
		for (EditBox eb : editBoxes)
			if (eb.isMouseOver(mx, my)) {
				eb.mouseReleased(event);
				return true;
			}
		return false;
	}

	@Override
	public boolean handleKeyPressed(ConfigOption<List<T>> option, KeyEvent keyEvent) {
		for (EditBox eb : editBoxes)
			if (eb.isFocused()) return eb.keyPressed(keyEvent);
		return false;
	}

	@Override
	public boolean handleKeyReleased(ConfigOption<List<T>> option, KeyEvent keyEvent) {
		for (EditBox eb : editBoxes)
			if (eb.isFocused()) return eb.keyReleased(keyEvent);
		return false;
	}

	@Override
	public boolean handleCharTyped(ConfigOption<List<T>> option, CharacterEvent characterEvent) {
		for (EditBox eb : editBoxes)
			if (eb.isFocused()) return eb.charTyped(characterEvent);
		return false;
	}
	//? } else {
	/*@Override
	public boolean handleMouseClicked(ConfigOption<List<T>> option,
									  double mx, double my, int button,
									  int rowX, int rowY, int rowW, int rowH) {
		if (my >= rowY && my < rowY + BASE_H) {
			if (resetButton != null && resetButton.isMouseOver(mx, my)) {
				return resetButton.mouseClicked(mx, my, button);
			}
			if (expanded) editBoxes.forEach(eb -> eb.setFocused(false));
			expanded = !expanded;
			if (Minecraft.getInstance().screen instanceof ConfigScreen) {
				ConfigScreen cs = (ConfigScreen) Minecraft.getInstance().screen;
				cs.refreshOptionList();
			}
			dirty = true;
			return true;
		}
		if (!expanded) return false;

		if (addButton != null && addButton.isMouseOver(mx, my))
			return addButton.mouseClicked(mx, my, button);

		for (Button db : deleteButtons)
			if (db.isMouseOver(mx, my)) return db.mouseClicked(mx, my, button);

		boolean consumed = false;
		for (EditBox eb : editBoxes) {
			if (eb.isMouseOver(mx, my)) { eb.setFocused(true); eb.mouseClicked(mx, my, button); consumed = true; }
			else eb.setFocused(false);
		}
		return consumed;
	}

	@Override public boolean handleMouseReleased(ConfigOption<List<T>> option, double mx, double my, int button) {
		for (EditBox eb : editBoxes)
			if (eb.isMouseOver(mx, my)) { eb.mouseReleased(mx, my, button); return true; }
		return false;
	}

	@Override public boolean handleKeyPressed(ConfigOption<List<T>> option, int keyCode, int scanCode, int modifiers) {
		for (EditBox eb : editBoxes) if (eb.isFocused()) return eb.keyPressed(keyCode, scanCode, modifiers);
		return false;
	}

	@Override public boolean handleKeyReleased(ConfigOption<List<T>> option, int keyCode, int scanCode, int modifiers) {
		for (EditBox eb : editBoxes) if (eb.isFocused()) return eb.keyReleased(keyCode, scanCode, modifiers);
		return false;
	}

	@Override public boolean handleCharTyped(ConfigOption<List<T>> option, char chr, int modifiers) {
		for (EditBox eb : editBoxes) if (eb.isFocused()) return eb.charTyped(chr, modifiers);
		return false;
	}
	@Override public boolean handleMouseScrolled(ConfigOption<List<T>> option, double mx, double my, double scroll) {
		return false;
	}
	*///?}


	private void rebuildWidgets(ConfigOption<List<T>> option) {
		editBoxes.clear();
		deleteButtons.clear();
		parseErrors.clear();

		List<Object> list = (List<Object>) (List<?>) safeList(option);
		Font font = Minecraft.getInstance().font;
		ElementType et = resolvedType(option);

		for (int i = 0; i < list.size(); i++) {
			final int idx = i;
			Object val = list.get(i);

			EditBox eb = new EditBox(font, 0, 0, 100, 14, Component.empty()) {
				//? if>= 1.21
				@Override public boolean isBordered() {return false;}
			};
			//? if = 1.20.1
			//eb.setBordered(false);
			eb.setValue(val == null ? "" : val.toString());
			eb.setMaxLength(Integer.MAX_VALUE);
			eb.setTextColor(COL_TEXT);
			eb.setTextColorUneditable(COL_TEXT_GRAY);
			parseErrors.add(false);

			eb.setResponder(s -> {
				Object parsed = tryParse(s, et);
				boolean error = parsed == null && !s.isEmpty();
				while (parseErrors.size() <= idx) parseErrors.add(false);
				parseErrors.set(idx, error);
				if (!error) {
					List<Object> current = (List<Object>)safeList(option);
					if (idx < current.size()) {
						current.set(idx, parsed == null ? s : parsed);
						option.set((List<T>) current);
					}
				}
			});
			editBoxes.add(eb);

			// Compact delete button
			Button db = Button.builder(Component.literal("×"), btn -> {
				flushEditBoxes(option, et);
				List<Object> current = new ArrayList<>(safeList(option));
				if (idx < current.size()) current.remove(idx);
				if (Minecraft.getInstance().screen instanceof ConfigScreen) {
					ConfigScreen cs = (ConfigScreen) Minecraft.getInstance().screen;
					cs.refreshOptionList();
				}
				option.set((List<T>) current);
				dirty = true;
			}).bounds(0, 0, DELETE_BTN_W, 14).build();
			deleteButtons.add(db);
		}

		// Compact add button
		addButton = Button.builder(Component.literal("+"), btn -> {
			flushEditBoxes(option, et);
			List<Object> current = new ArrayList<>(safeList(option));
			current.add(defaultValue(et));
			if (Minecraft.getInstance().screen instanceof ConfigScreen) {
				ConfigScreen cs = (ConfigScreen) Minecraft.getInstance().screen;
				cs.refreshOptionList();
			}
			option.set((List<T>) current);
			dirty = true;
		}).bounds(0, 0, 50, 14).build();

		dirty = false;
	}

	private ElementType resolvedType(ConfigOption<List<T>> option) {
		if (elementType != null) return elementType;
		for (Object o : safeList(option)) {
			if (o == null) continue;
			if (o instanceof Integer) return ElementType.INTEGER;
			if (o instanceof Long)    return ElementType.LONG;
			if (o instanceof Float)   return ElementType.FLOAT;
			if (o instanceof Double)  return ElementType.DOUBLE;
			return ElementType.STRING;
		}
		return ElementType.STRING;
	}

	private static Object tryParse(String s, ElementType et) {
		if (s == null || s.isEmpty()) return null;
		if (et == null || et == ElementType.STRING) return s;
		try {
			return switch (et) {
				case INTEGER -> Integer.parseInt(s.trim());
				case LONG    -> Long.parseLong(s.trim());
				case FLOAT   -> Float.parseFloat(s.trim());
				case DOUBLE  -> Double.parseDouble(s.trim());
				case STRING  -> s;
			};
		} catch (Exception e) { return null; }
	}

	private static Object defaultValue(ElementType et) {
		if (et == null) return "";
		return switch (et) {
			case INTEGER -> 0;
			case LONG    -> 0L;
			case FLOAT   -> 0.0f;
			case DOUBLE  -> 0.0;
			case STRING  -> "";
		};
	}

	private static List<?> safeList(ConfigOption<?> option) {
		Object val = option.get();
		return val instanceof List<?> l ? new ArrayList<>(l) : new ArrayList<>();
	}
	private boolean hasChangesFromDefault(ConfigOption<List<T>> option) {
		List<?> list = safeList(option);
		if (list.isEmpty()) return false;
		if (list.size() == 1) {
			return !Objects.equals(list.get(0), defaultValue(resolvedType(option)));
		}
		return true;
	}
	private void flushEditBoxes(ConfigOption<List<T>> option, ElementType et) {
		List<Object> current = (List<Object>) (List<?>) safeList(option);
		for (int i = 0; i < Math.min(editBoxes.size(), current.size()); i++) {
			String text   = editBoxes.get(i).getValue();
			Object parsed = tryParse(text, et);
			current.set(i, parsed != null ? parsed : text);
		}
		option.set((List<T>) current);
	}
}
