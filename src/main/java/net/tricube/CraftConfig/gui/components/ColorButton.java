package net.tricube.CraftConfig.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?if>=1.21.9
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.tricube.CraftConfig.util.ScreenUtils;

public class ColorButton extends AbstractButton {
	private int thumbColor = 0xFFFFFFFF;
	private boolean showThumb = true;
	private int squareX = 4;
	private int squareY = 3;
	private int squareSize = -1; // -1 means auto-calculate
	private final Runnable onPress;

	public ColorButton(int x, int y, int w, int h, Component message, Runnable onPress) {
		super(x, y, w, h, message);
		this.onPress = onPress;
	}

	public void setThumbColor(int color) {
		this.thumbColor = color;
	}

	public void setShowThumb(boolean show) {
		this.showThumb = show;
	}

	public void setSquarePosition(int x, int y) {
		this.squareX = x;
		this.squareY = y;
	}

	public void setSquareSize(int size) {
		this.squareSize = size;
	}



	public static Builder colorBuilder(Component message, Runnable onPress) {
		return new Builder(message, onPress);
	}

	//? if >=1.21 {
	@Override protected void updateWidgetNarration(NarrationElementOutput out) {}
	//? } else{
	/*@Override protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
	*///?}


	public static class Builder {
		private final Component message;
		private final Runnable onPress;
		private int x = 0;
		private int y = 0;
		private int width = 150;
		private int height = 20;
		private int thumbColor = 0xFFFFFFFF;
		private int squareX = 4;
		private int squareY = 3;
		private int squareSize = -1;

		public Builder(Component message, Runnable onPress) {
			this.message = message;
			this.onPress = onPress;
		}

		public Builder bounds(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			return this;
		}

		public Builder previewColor(int color) {
			this.thumbColor = color;
			return this;
		}

		public Builder previewPos(int x, int y) {
			this.squareX = x;
			this.squareY = y;
			return this;
		}

		public Builder previewSize(int size) {
			this.squareSize = size;
			return this;
		}

		public ColorButton build() {
			ColorButton btn = new ColorButton(x, y, width, height, message, onPress);
			btn.setThumbColor(thumbColor);
			btn.setSquarePosition(squareX, squareY);
			if (squareSize != -1) {
				btn.setSquareSize(squareSize);
			}
			return btn;
		}
	}

	@Override
	public void onPress(
			//?if>=1.21.9
			InputWithModifiers inputWithModifiers
	) {
		if (onPress != null) {
			onPress.run();
		}
	}

	//?if = 1.21.11{
	@Override protected void renderContents(GuiGraphics g, int mx, int my, float pt) {
		//?}else{

		/*//~ if >=26.1 'renderWidget' -> 'extractContents'
		@Override public void renderWidget (GuiGraphics g,int mx, int my, float pt){
		*///?}
		//?if>=1.21.11{
		//~ if >=26.1 'render' -> 'extract'{
		this.renderDefaultSprite(g);
		this.renderDefaultLabel(g.textRenderer());
		//~}
		//?}else{
		/*super.renderWidget(g, mx, my, pt);
		*///?}


			if (showThumb) {
				int size = squareSize != -1 ? squareSize : height - 6;
				int x = getX() + squareX;
				int y = getY() + squareY;

				g.fill(x, y, x + size, y + size, thumbColor);
				ScreenUtils.drawOutline(g, x, y, size, size, 0xFF000000);
			}
		}

}
