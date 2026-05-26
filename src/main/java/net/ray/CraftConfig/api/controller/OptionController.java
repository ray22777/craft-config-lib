package net.ray.CraftConfig.api.controller;

import net.minecraft.client.gui.GuiGraphics ;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.ray.CraftConfig.api.v1.ConfigOption;
import org.jetbrains.annotations.Nullable;

//? if >=1.21.9 {
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;
//? }

public interface OptionController<T> {

	AbstractWidget createWidget(ConfigOption<T> option, int x, int y, int w, int h);

	default @Nullable AbstractWidget createAuxWidget(ConfigOption<T> option,
													 int x, int y, int h) {
		return null;
	}

	default void renderLabel(GuiGraphics g, Font font, ConfigOption<T> option,
							 int labelX, int labelY, int color) {
		g.drawString(font, option.name(), labelX, labelY, color, false);
	}

	default boolean ownsRow() { return false; }


	default void renderRow(GuiGraphics g, ConfigOption<T> option,
						   int x, int y, int w, int h,
						   int mx, int my, boolean hovered, float delta) {}

	default int rowHeight(ConfigOption<T> option, int baseRowHeight) {
		return baseRowHeight;
	}


//?if >=1.21.9{

	default boolean handleMouseClicked(ConfigOption<T> option,
									   MouseButtonEvent event, boolean doubleClick,
									   int rowX, int rowY, int rowW, int rowH) {
		return handleMouseClicked(option,
				event.x(), event.y(), event.button(),
				rowX, rowY, rowW, rowH);
	}

	default boolean handleMouseReleased(ConfigOption<T> option,
										MouseButtonEvent event) {
		return handleMouseReleased(option,
				event.x(), event.y(), event.button());
	}

	default boolean handleMouseDragged(ConfigOption<T> option,
									   MouseButtonEvent event, double dx, double dy) {
		return handleMouseDragged(option,
				event.x(), event.y(), event.button(), dx, dy);
	}

	default boolean handleKeyPressed(ConfigOption<T> option,
									 KeyEvent keyEvent) {
		return handleKeyPressed(option,
				keyEvent.key(), 0, keyEvent.modifiers());
	}

	default boolean handleKeyReleased(ConfigOption<T> option,
									  KeyEvent keyEvent) {
		return handleKeyReleased(option,
				keyEvent.key(), 0, keyEvent.modifiers());
	}

	default boolean handleCharTyped(ConfigOption<T> option,
									CharacterEvent characterEvent) {
		return handleCharTyped(option,
				(char) characterEvent.codepoint(),
				//?if>=26.1{
				/*characterEvent.codepoint()
				*///?}else{
								characterEvent.modifiers()
				//?}

		);
	}
	//?}
	default boolean handleMouseScrolled(ConfigOption<T> option,
										double mx, double my, double scroll) {
		return false;
	}
	default boolean handleMouseClicked(ConfigOption<T> option,
									   double mx, double my, int button,
									   int rowX, int rowY, int rowW, int rowH) {
		return false;
	}

	default boolean handleMouseReleased(ConfigOption<T> option,
										double mx, double my, int button) {
		return false;
	}

	default boolean handleMouseDragged(ConfigOption<T> option,
									   double mx, double my, int button,
									   double dx, double dy) {
		return false;
	}

	default boolean handleKeyPressed(ConfigOption<T> option,
									 int keyCode, int scanCode, int modifiers) {
		return false;
	}

	default boolean handleCharTyped(ConfigOption<T> option, char chr, int modifiers) {
		return false;
	}


	default boolean handleKeyReleased(ConfigOption<T> option,
									  int keyCode, int scanCode, int modifiers) {
		return false;
	}

}
