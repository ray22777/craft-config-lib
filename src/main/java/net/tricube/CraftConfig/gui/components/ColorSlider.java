package net.tricube.CraftConfig.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.tricube.CraftConfig.util.ScreenUtils;

import java.util.function.Consumer;
public class ColorSlider extends AbstractSliderButton {
		private final String label;
		private final Consumer<Integer> onChange;
		private final int thumbColor;
		private final boolean simple;

		public ColorSlider(int x, int y, int w, int h, String label, int value, int thumbColor, Consumer<Integer> onChange) {
			super(x, y, w, h,
					Component.literal(label + ": " + value), value / 255.0);
			this.label = label;
			this.onChange = onChange;
			this.thumbColor = thumbColor;
			this.simple = false;
		}

		// Constructor for simple sliders (brightness/alpha)
		public ColorSlider(int x, int y, int width, int height, Component message, double value) {
			super(x, y, width, height, message, value);
			this.label = null;
			this.onChange = null;
			this.thumbColor = 0xFFFFFFFF;
			this.simple = true;
		}

		public void setSliderValue(double newValue) {
			this.value = Mth.clamp(newValue, 0.0, 1.0);
			this.applyValue();
			this.updateMessage();
		}

		@Override protected void updateMessage() {
			if (!simple && label != null) {
				setMessage(Component.literal(label + ": " + (int)(value * 255)));
			}
		}

		@Override protected void applyValue() {
			if (!simple && onChange != null) {
				onChange.accept((int) (this.value * 255));
			}
		}

		//~ if >=26.1 'renderWidget' -> 'extractWidgetRenderState'{
		@Override public void renderWidget(GuiGraphics g, int mx, int my, float pt) {
			super.renderWidget(g, mx, my, pt);
			if (!simple && label != null) {
				int trackX = getX() + 40;
				int thumbX = trackX + (int) (value * width / 255f) - 5;
				g.fill(thumbX+1, getY() + 4, thumbX + 8, getY() + height - 4, thumbColor);
				ScreenUtils.drawOutline(g,thumbX+1, getY() + 4, 8, height - 8, 0xFF000000);

			}
		}
		//~}
	}
