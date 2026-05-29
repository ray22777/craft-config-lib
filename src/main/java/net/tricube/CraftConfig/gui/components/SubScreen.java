package net.tricube.CraftConfig.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
//?if>=1.21.9{
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//?}
import net.minecraft.network.chat.Component;
import net.tricube.CraftConfig.util.ScreenUtils;

import static net.tricube.CraftConfig.gui.constants.ColorSchemes.*;

public abstract class SubScreen extends Screen {

    protected static final int PADDING = 10;
    protected static final int ROW_H   = 20;
    protected static final int TITLE_H = 24;
    protected static final int HEADER_H = TITLE_H + PADDING;

    protected final Screen parent;
    protected int modalX, modalY;
    protected int modalW, modalH;

    protected SubScreen(Component title, Screen parent, int width, int height) {
        super(title);
        this.parent = parent;
        this.modalW = width;
        this.modalH = height;
    }

    @Override
    protected void init() {
        modalX = (width - modalW) / 2;
        modalY = (height - modalH) / 2;

    }
	//~ if >=26.1 'render(' -> 'extractRenderState('{
	@Override
    public void render(GuiGraphics g, int mx, int my, float delta) {//TODO:FIX BUTTONS FROM CLIPPING, RENDER BACKGROUND CONFIG IN 1.21.2+
		//?if =1.21.5{
		/*renderBackground(g,mx,my,delta);
		*///?}else if >=1.21 {
		if (parent != null) parent.render(g, 0, 0, delta);
		//? }else{
			/*renderBackground(g);

		*///?}
        super.render(g, mx, my, delta);
		//?if>=1.21.6{
		g.fill(0, 0, width, height, 0x99000000);
		//?}else{
		/*g.fill(0, 0, width, height, 0x66000000);
		*///?}

        g.fill(modalX, modalY, modalX + modalW, modalY + modalH, COL_BG);
        g.fill(modalX, modalY, modalX + modalW, modalY + TITLE_H, COL_TITLE_BG);
        renderModalBorders(g);
        g.drawCenteredString(font, getTitle().getString(),
                modalX + modalW / 2, modalY + (TITLE_H - font.lineHeight) / 2, COL_TEXT);


        for (GuiEventListener child : children()) {
            if (child instanceof Renderable r) {
                r.render(g, mx, my, delta);
            }
        }
    }
	//~}
    protected void renderModalBorders(GuiGraphics g) {
        ScreenUtils.drawOutline(g,modalX, modalY + TITLE_H, modalW, modalH - TITLE_H, COL_SUB_BORDER);
		ScreenUtils.drawOutline(g,modalX, modalY, modalW, TITLE_H + 1, COL_BORDER_LIGHT);
    }

	//? if >=1.21.9 {
	@Override
	public boolean keyPressed(KeyEvent keyEvent) {
		if (keyEvent.key() == 256) {
			onCloseModal();
			return true;
		}
		return super.keyPressed(keyEvent);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double mouseX = event.x();
		double mouseY = event.y();
		int button = event.button();

		if (mouseX < modalX || mouseX > modalX + modalW ||
				mouseY < modalY || mouseY > modalY + modalH) {
			onCloseModal();
			return true;
		}
		return super.mouseClicked(event, doubleClick);
	}
//? } else {
/*@Override
public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == 256) {
        onCloseModal();
        return true;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
}

@Override
public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (mouseX < modalX || mouseX > modalX + modalW ||
        mouseY < modalY || mouseY > modalY + modalH) {
        onCloseModal();
        return true;
    }
    return super.mouseClicked(mouseX, mouseY, button);
}
*///? }

    protected void onCloseModal() {
        onClose();
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected void renderHoverEffect(GuiGraphics g, int x, int y, int w, int h, boolean hovered) {
        if (hovered) {
            g.fill(x, y, x + w, y + h, COL_HOVER);
        }
    }
}
