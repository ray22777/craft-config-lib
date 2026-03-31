package net.ray.CraftConfig.gui.subscreens;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics ;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
//? if >=1.21.2 <1.21.11
//import net.minecraft.client.renderer.RenderType;
//?if >=1.21.6
//import net.minecraft.client.renderer.RenderPipelines;
//?if >=1.21.9
//import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
//?if >= 26.1{
/*import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.ChatFormatting;
*///?}
import net.minecraft.resources.ResourceLocation;

import net.minecraft.util.Mth;
import net.ray.CraftConfig.api.v1.ConfigOption;
import net.ray.CraftConfig.gui.components.ColorSlider;
import net.ray.CraftConfig.gui.components.SubScreen;
import net.ray.CraftConfig.util.ColorUtils;
import net.ray.CraftConfig.util.ScreenUtils;

import java.awt.*;
import java.util.function.Consumer;

public class ColorPickerScreen extends SubScreen {

	private static final int MODAL_WIDTH = 360;
	private static final int MODAL_HEIGHT = 240;
	private static final int COLOR_DISC_SIZE = 134;
	private static final int SLIDER_WIDTH = 140;
	private static final int SLIDER_HEIGHT = 16;

	private static DynamicTexture DISC_TEXTURE;
	private static ResourceLocation DISC_RL;
	private static boolean TEXTURE_INITIALIZED = false;

	private final Consumer<Color> onConfirm;
	private int finalColor;
	private final int originalColor;

	private float wheelHue = 0f;
	private float wheelSat = 1f;
	private int baseR = 255, baseG = 255, baseB = 255;
	private int brightness = 255;
	private int alpha = 255;
	private int sliderAlpha = 255;
	private EditBox hexInput;
	private Button applyBtn, cancelBtn;
	private ColorSlider redSlider, greenSlider, blueSlider, brightnessSlider, alphaSlider;
	private ConfigOption option;
	private boolean draggingDisc = false;
	private boolean hexDirty = true;
	private boolean hexWasFocused = false;

	public ColorPickerScreen(Screen parent, Component title, Color initialColor, Consumer<Color> onConfirm) {
		super(title, parent, MODAL_WIDTH, MODAL_HEIGHT);
		this.onConfirm = onConfirm;  // This is already correct
		this.originalColor = initialColor.getRGB();
		this.finalColor = initialColor.getRGB();

		alpha = initialColor.getAlpha();
		sliderAlpha = alpha;
		baseR = initialColor.getRed();
		baseG = initialColor.getGreen();
		baseB = initialColor.getBlue();
		float[] hsv = ColorUtils.rgbToHsv(baseR, baseG, baseB);
		wheelHue = hsv[0];
		wheelSat = hsv[1];

		brightness = 255;

		computeFinalColor();
	}

	private static void initTexture() {
		if (TEXTURE_INITIALIZED) return;

		int size = COLOR_DISC_SIZE;
		int radius = size / 2;
		NativeImage img = new NativeImage(size, size, true);

		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				int dx = x - radius, dy = y - radius;
				float dist = (float) Math.sqrt(dx * dx + dy * dy) / radius;
				if (dist > 1.0f) {
					//?>=1.21.2{
					/*img.setPixel(x, y, 0);
					*///?}else{
					img.setPixelRGBA(x, y, 0);
					//?}
					continue;
				}
				float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
				if (angle < 0) angle += 360;
				int rgb = ColorUtils.hsvToRgb(angle, dist, 1.0f);

				// NativeImage expects ABGR format
				int a = 0xFF, r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
				//?>=1.21.2{
				/*img.setPixel(x, y, (a << 24) | (r << 16) | (g << 8) | b);
				*///?}else{
				img.setPixelRGBA(x, y, (a << 24) | (b << 16) | (g << 8) | r);
				//?}

			}
		}

		//? if >=1.21.5{
		/*DISC_RL = ResourceLocation.fromNamespaceAndPath("craftconfig", "color_picker_disc");
		ResourceLocation discRl = DISC_RL;
		NativeImage img2 = img;
		Minecraft.getInstance().execute(() -> {
			DISC_TEXTURE = new DynamicTexture(() -> "craftconfig:color_picker_disc", img2);
			Minecraft.getInstance().getTextureManager().register(discRl, DISC_TEXTURE);
		});
		*///?} else if >=1.21.4 {
		/*DISC_TEXTURE = new DynamicTexture(img);
		RenderSystem.recordRenderCall(() -> {
			DISC_TEXTURE.upload();
			DISC_RL = ResourceLocation.fromNamespaceAndPath("craftconfig", "color_picker_disc");
			Minecraft.getInstance().getTextureManager().register(DISC_RL, DISC_TEXTURE);
		});
		*///? } else if >=1.21 {
		/*DISC_TEXTURE = new DynamicTexture(img);
		DISC_RL = Minecraft.getInstance().getTextureManager().register(ResourceLocation.fromNamespaceAndPath("craftconfig", "color_picker_disc").getPath(), DISC_TEXTURE);
		Minecraft.getInstance().getTextureManager().register(DISC_RL.getPath(), DISC_TEXTURE);
		*///? } else {
		DISC_TEXTURE = new DynamicTexture(img);
		DISC_RL = Minecraft.getInstance().getTextureManager().register(
		new ResourceLocation("craftconfig", "color_picker_disc").getPath(), DISC_TEXTURE);
		//? }

		TEXTURE_INITIALIZED = true;
	}

	@Override
	protected void init() {
		super.init();
		initTexture();

		int discX = modalX + 20;
		int discY = modalY + HEADER_H + 15;
		int rightX = discX + COLOR_DISC_SIZE + 25;
		int rightY = discY;

		// RGB Sliders - modify baseR/G/B only
		redSlider = new ColorSlider(rightX, rightY,SLIDER_WIDTH,SLIDER_HEIGHT, "R", baseR, 0xFFFF5555, v -> {
			baseR = v; syncWheelFromRgb(); computeFinalColor();
		});
		addRenderableWidget(redSlider);

		rightY += SLIDER_HEIGHT + 5;
		greenSlider = new ColorSlider(rightX, rightY,SLIDER_WIDTH,SLIDER_HEIGHT,"G", baseG, 0xFF55FF55, v -> {
			baseG = v; syncWheelFromRgb(); computeFinalColor();
		});
		addRenderableWidget(greenSlider);

		rightY += SLIDER_HEIGHT + 5;
		blueSlider = new ColorSlider(rightX, rightY,SLIDER_WIDTH,SLIDER_HEIGHT, "B", baseB, 0xFF5555FF, v -> {
			baseB = v; syncWheelFromRgb(); computeFinalColor();
		});
		addRenderableWidget(blueSlider);

		rightY += SLIDER_HEIGHT + 10;
		brightnessSlider = new ColorSlider(rightX, rightY, SLIDER_WIDTH, SLIDER_HEIGHT,
				Component.literal("Brightness: " + brightness), brightness / 255.0) {
			@Override protected void updateMessage() {
				setMessage(Component.literal("Brightness: " + brightness));
			}
			@Override protected void applyValue() {
				brightness = (int) (this.value * 255);
				computeFinalColor(); // Runs immediately on drag
			}
		};
		addRenderableWidget(brightnessSlider);

		rightY += SLIDER_HEIGHT + 5;
		alphaSlider = new ColorSlider(rightX, rightY, SLIDER_WIDTH, SLIDER_HEIGHT,
				Component.literal("Alpha: " + sliderAlpha), sliderAlpha / 255.0) {
			@Override protected void updateMessage() {
				setMessage(Component.literal("Alpha: " + sliderAlpha));
			}
			@Override protected void applyValue() {
				sliderAlpha = (int) (this.value * 255);
				computeFinalColor();
			}
		};
		addRenderableWidget(alphaSlider);

		rightY += SLIDER_HEIGHT + 12;
		hexInput = new EditBox(font, rightX, rightY, 117, 18, Component.literal("Hex"));
		hexInput.setHint(Component.literal("#AARRGGBB"));
		hexInput.setBordered(true);
		hexInput.setMaxLength(9);
		//?if>=26.1{
		/*hexInput.addFormatter((text, offset) -> {
			if (text.matches("[0-9A-Fa-f#]*")) {
				return null;
			}
			return FormattedCharSequence.forward(text, Style.EMPTY.withColor(ChatFormatting.RED));
		});
		*///?}else{
				hexInput.setFilter(s -> s.matches("[0-9A-Fa-f#]*"));
		//?}

		hexInput.setValue(toHex(finalColor));
		hexInput.setResponder(this::onHexChanged);
		addRenderableWidget(hexInput);

		int bottomY = modalY + MODAL_HEIGHT - 35;
		int buttonWidth = 120;
		int spacing = 10;
		int totalWidth = (buttonWidth * 2) + spacing;
		int startX = modalX + (MODAL_WIDTH - totalWidth) / 2;

		cancelBtn = Button.builder(Component.literal("Cancel"), b -> onClose())
				.pos(startX, bottomY)
				.size(buttonWidth, 20)
				.build();

		applyBtn = Button.builder(Component.literal("Done"), b -> done())
				.pos(startX + buttonWidth + spacing, bottomY)
				.size(buttonWidth, 20)
				.build();

		addRenderableWidget(cancelBtn);
		addRenderableWidget(applyBtn);
	}



	//~ if >=26.1 'render' -> 'extractRenderState'{
	@Override
	public void render(GuiGraphics g, int mx, int my, float delta) {
		super.render(g, mx, my, delta);

		int discX = modalX + 20;
		int discY = modalY + HEADER_H + 15;
		int radius = COLOR_DISC_SIZE / 2;
		int centerX = discX + radius;
		int centerY = discY + radius;

		if (DISC_TEXTURE != null && DISC_RL != null) {
			//? if <=1.21.1 {
			g.blit(DISC_RL, discX, discY, 0, 0, COLOR_DISC_SIZE, COLOR_DISC_SIZE, COLOR_DISC_SIZE, COLOR_DISC_SIZE);
			//?} else if < 1.21.6{
			/*g.blit(RenderType::guiTextured, DISC_RL, discX, discY, 0, 0, COLOR_DISC_SIZE, COLOR_DISC_SIZE, COLOR_DISC_SIZE, COLOR_DISC_SIZE);
			*///?} else{
			/*g.blit(RenderPipelines.GUI_TEXTURED, DISC_RL, discX, discY, 0, 0, COLOR_DISC_SIZE, COLOR_DISC_SIZE, COLOR_DISC_SIZE, COLOR_DISC_SIZE);
			*///?}
		}
		ScreenUtils.drawOutline(g,discX, discY, COLOR_DISC_SIZE + 1, COLOR_DISC_SIZE + 1, 0xFF555555);

		double angleRad = Math.toRadians(wheelHue);
		int selDist = (int) (wheelSat * radius);
		int selX = centerX + (int) (Math.cos(angleRad) * selDist);
		int selY = centerY + (int) (Math.sin(angleRad) * selDist);
		ScreenUtils.drawOutline(g,selX - 1, selY - 1, 2, 2, 0xFFFFFFFF);
		ScreenUtils.drawOutline(g,selX - 2, selY - 2, 4, 4, 0xFF000000);

		int previewSize = 18;
		int previewX = hexInput.getX() + hexInput.getWidth() + 5;
		int previewY = hexInput.getY();
		drawPreview(g, previewX, previewY, previewSize, finalColor);
		ScreenUtils.drawOutline(g,previewX, previewY, previewSize, previewSize, 0xFF888888);
		if (hexInput != null && hexWasFocused && !hexInput.isFocused()) {
			flushHexUpdate();
		}
		hexWasFocused = hexInput != null && hexInput.isFocused();
	}
	//~}
	private void drawPreview(GuiGraphics g, int x, int y, int size, int color) {
		int alpha = (color >> 24) & 0xFF;
		int r = (color >> 16) & 0xFF;
		int gv = (color >> 8) & 0xFF;
		int b = color & 0xFF;
		float a = alpha / 255f;

		int cell = 9;

		for (int py = 0; py < size; py += cell) {
			for (int px = 0; px < size; px += cell) {
				boolean light = ((px / cell) + (py / cell)) % 2 == 0;
				int check = light ? 0x44 : 0x22;

				int fr = Mth.clamp((int) (r * a + check * (1 - a)), 0, 255);
				int fg = Mth.clamp((int) (gv * a + check * (1 - a)), 0, 255);
				int fb = Mth.clamp((int) (b * a + check * (1 - a)), 0, 255);
				g.fill(x + px, y + py, x + Math.min(px + cell, size), y + Math.min(py + cell, size),
						0xFF000000 | (fr << 16) | (fg << 8) | fb);
			}
		}
	}


	//? if >=1.21.9 {
	/*@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double mx = event.x();
		double my = event.y();
		int button = event.button();

		if (button != 0) return super.mouseClicked(event, doubleClick);
		int cx = modalX + 20 + COLOR_DISC_SIZE / 2;
		int cy = modalY + HEADER_H + 15 + COLOR_DISC_SIZE / 2;
		int r = COLOR_DISC_SIZE / 2;
		double dx = mx - cx, dy = my - cy;
		double dist = Math.sqrt(dx * dx + dy * dy);
		if (dist <= r) {
			draggingDisc = true;
			updateWheelFromMouse(mx, my, cx, cy, r);
			return true;
		}
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
		double mx = event.x();
		double my = event.y();
		int button = event.button();

		if (button != 0 || !draggingDisc) return super.mouseDragged(event, dx, dy);
		int cx = modalX + 20 + COLOR_DISC_SIZE / 2;
		int cy = modalY + HEADER_H + 15 + COLOR_DISC_SIZE / 2;
		updateWheelFromMouse(mx, my, cx, cy, COLOR_DISC_SIZE / 2);
		return true;
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		draggingDisc = false;
		flushHexUpdate();
		return super.mouseReleased(event);
	}
*///? } else {
@Override
public boolean mouseClicked(double mx, double my, int button) {
    if (button != 0) return super.mouseClicked(mx, my, button);
    int cx = modalX + 20 + COLOR_DISC_SIZE / 2;
    int cy = modalY + HEADER_H + 15 + COLOR_DISC_SIZE / 2;
    int r = COLOR_DISC_SIZE / 2;
    double dx = mx - cx, dy = my - cy;
    double dist = Math.sqrt(dx * dx + dy * dy);
    if (dist <= r) {
        draggingDisc = true;
        updateWheelFromMouse(mx, my, cx, cy, r);
        return true;
    }
    return super.mouseClicked(mx, my, button);
}

@Override
public boolean mouseDragged(double mx, double my, int button, double dragX, double dragY) {
    if (button != 0 || !draggingDisc) return super.mouseDragged(mx, my, button, dragX, dragY);
    int cx = modalX + 20 + COLOR_DISC_SIZE / 2;
    int cy = modalY + HEADER_H + 15 + COLOR_DISC_SIZE / 2;
    updateWheelFromMouse(mx, my, cx, cy, COLOR_DISC_SIZE / 2);
    return true;
}

@Override
public boolean mouseReleased(double mx, double my, int button) {
    draggingDisc = false;
    flushHexUpdate();
    return super.mouseReleased(mx, my, button);
}
//? }

	private void updateWheelFromMouse(double mx, double my, int cx, int cy, int radius) {
		double dx = mx - cx, dy = my - cy;
		double dist = Math.min(Math.sqrt(dx * dx + dy * dy), radius);
		double angle = Math.toDegrees(Math.atan2(dy, dx));
		if (angle < 0) angle += 360;

		wheelHue = (float) angle;
		wheelSat = (float) (dist / radius);

		wheelSat = Mth.clamp(wheelSat, 0f, 1f);

		int rgb = ColorUtils.hsvToRgb(wheelHue, wheelSat, 1.0f);
		baseR = (rgb >> 16) & 0xFF;
		baseG = (rgb >> 8) & 0xFF;
		baseB = rgb & 0xFF;

		if (redSlider != null) redSlider.setSliderValue(baseR / 255.0);
		if (greenSlider != null) greenSlider.setSliderValue(baseG / 255.0);
		if (blueSlider != null) blueSlider.setSliderValue(baseB / 255.0);

		computeFinalColor();
	}
	private void syncWheelFromRgb() {
		float[] hsv = ColorUtils.rgbToHsv(baseR, baseG, baseB);
		wheelHue = hsv[0];
		wheelSat = hsv[1];
	}

	private void computeFinalColor() {
		int r = Mth.clamp((baseR * brightness) / 255, 0, 255);
		int g = Mth.clamp((baseG * brightness) / 255, 0, 255);
		int b = Mth.clamp((baseB * brightness) / 255, 0, 255);
		finalColor = (sliderAlpha << 24) | (r << 16) | (g << 8) | b;
		hexDirty = true;
	}

	private void flushHexUpdate() {
		if (hexDirty && hexInput != null && !hexInput.isFocused()) {
			hexInput.setValue(toHex(finalColor));
			hexDirty = false;
		}
	}

	private void onHexChanged(String text) {
		if (text == null || text.isEmpty()) return;
		try {
			String s = text.replace("#", "").toUpperCase();
			if (s.length() == 6) s = "FF" + s;
			if (s.length() != 8) return;

			long val = Long.parseLong(s, 16);
			if (val > 0xFFFFFFFFL) return;

			finalColor = (int) val;

			alpha = (finalColor >> 24) & 0xFF;
			int r = (finalColor >> 16) & 0xFF;
			int g = (finalColor >> 8) & 0xFF;
			int b = finalColor & 0xFF;


			if (!draggingDisc) {
				if (alphaSlider != null)alphaSlider.setSliderValue(alpha / 255.0);
				if (brightnessSlider != null)  brightnessSlider.setSliderValue(brightness / 255.0);
				if (redSlider != null) redSlider.setSliderValue(baseR / 255.0);
				if (greenSlider != null) greenSlider.setSliderValue(baseG / 255.0);
				if (blueSlider != null) blueSlider.setSliderValue(baseB / 255.0);
			}

			syncWheelFromRgb();
			hexDirty = false;
		} catch (NumberFormatException ignored) {}
	}
	private void done() {
		flushHexUpdate();
		if (onConfirm != null) {
			onConfirm.accept(new Color(finalColor,true));
		}
		onClose();
	}

	private static String toHex(int c) {
		return String.format("#%02X%02X%02X%02X",
				(c >> 24) & 0xFF, (c >> 16) & 0xFF, (c >> 8) & 0xFF, c & 0xFF);
	}
}
