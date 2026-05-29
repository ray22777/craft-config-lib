package net.tricube.CraftConfig.util;

import net.minecraft.client.gui.GuiGraphics;

public class ScreenUtils {
	public static void drawOutline(GuiGraphics g, int x, int y, int w, int h, int color) {
		//?if >=26.1{
		/*g.outline(x,y,w,h,color);
		*///?}else if >= 1.21.9{
		g.fill(x,y,x + w,y + 1,color);
		g.fill(x,y + h- 1, x + w,y + h,color);
		g.fill(x,y,x + 1,y + h,color);
		g.fill(x + w - 1, y,x + w,y + h,color);
		//?}else{
		/*g.submitOutline(x,y,w,h,color);
		*///?}


	}
}
