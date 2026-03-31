package net.ray.CraftConfig.gui.subscreens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
//~ if >=1.21 '.controls.KeyBindsScreen' -> '.options.controls.KeyBindsScreen'
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import net.ray.CraftConfig.api.v1.ConfigOption;
import net.ray.CraftConfig.api.v1.ConfigKeybinds;
import net.ray.CraftConfig.gui.components.SubScreen;
import static net.ray.CraftConfig.gui.constants.ColorSchemes.*;
public class KeybindSettingsScreen extends SubScreen {

    private static final int GAP         = 6;
    private static final int INFO_LINE_H = 13;
	private int infoY;
    private final ConfigOption<Boolean> option;
    private final ConfigKeybinds kb;
    private final Runnable onDone;

    public KeybindSettingsScreen(Screen parent, ConfigOption<Boolean> option, Runnable onDone) {
        super(Component.literal("Keybind Settings"), parent, 200, 190);
        this.option = option;
        this.kb     = option.keybindSettings();
        this.onDone = onDone;
    }

    @Override
    protected void init() {
        super.init();

        int cx = modalX + PADDING;
        int cw = modalW - PADDING * 2;
        int y  = modalY + HEADER_H;
//? if >=1.21.11 {
		/*addRenderableWidget(CycleButton.<Boolean>builder(
						v -> Component.literal(v ? "§aEnabled" : "§7Disabled"),
						kb::isEnabled)
				.withValues(true, false)
				.create(cx, y, cw, ROW_H,
						Component.literal("Keybind"),
						(btn, val) -> kb.setEnabled(val)));
*///?} else {
addRenderableWidget(CycleButton.<Boolean>builder(
        v -> Component.literal(v ? "§aEnabled" : "§7Disabled"))
        .withValues(true, false)
        .withInitialValue(kb.isEnabled())
        .create(cx, y, cw, ROW_H,
                Component.literal("Keybind"),
                (btn, val) -> kb.setEnabled(val)));
//?}
		y += ROW_H + GAP;

//? if >=1.21.11 {
		/*addRenderableWidget(CycleButton.<ConfigKeybinds.Mode>builder(
						mode -> switch (mode) {
							case TOGGLE        -> Component.literal("Toggle");
							case HOLD          -> Component.literal("Hold");
							case HOLD_INVERTED -> Component.literal("Hold Inverted");
						}, kb::mode)
				.withValues(ConfigKeybinds.Mode.values())
				.create(cx, y, cw, ROW_H,
						Component.literal("Mode"),
						(btn, val) -> kb.setMode(val)));
*///?} else {
addRenderableWidget(CycleButton.<ConfigKeybinds.Mode>builder(mode -> switch (mode) {
    case TOGGLE        -> Component.literal("Toggle");
    case HOLD          -> Component.literal("Hold");
    case HOLD_INVERTED -> Component.literal("Hold Inverted");
})
        .withValues(ConfigKeybinds.Mode.values())
        .withInitialValue(kb.mode())
        .create(cx, y, cw, ROW_H,
                Component.literal("Mode"),
                (btn, val) -> kb.setMode(val)));
//?}
		y += ROW_H + GAP;

//? if >=1.21.11 {
		/*addRenderableWidget(CycleButton.<Boolean>builder(
						v -> Component.literal(v ? "§aYes" : "§7No"),
						kb::sendChatMessage)
				.withValues(true, false)
				.create(cx, y, cw, ROW_H,
						Component.literal("Announce change"),
						(btn, val) -> kb.setSendChatMessage(val)));
*///?} else {
addRenderableWidget(CycleButton.<Boolean>builder(
        v -> Component.literal(v ? "§aYes" : "§7No"))
        .withValues(true, false)
        .withInitialValue(kb.sendChatMessage())
        .create(cx, y, cw, ROW_H,
                Component.literal("Announce change"),
                (btn, val) -> kb.setSendChatMessage(val)));
//?}

		y += ROW_H + GAP * 2;

		infoY = y;
		y += INFO_LINE_H + 2;

		addRenderableWidget(Button.builder(Component.literal("Edit Keybind"), btn -> {
			Minecraft.getInstance().setScreen(new KeyBindsScreen(this, Minecraft.getInstance().options));
		}).bounds(cx + cw / 2 - 40, y, 80, 16).build());

		y += 12 + GAP * 2;

		addRenderableWidget(Button.builder(Component.literal("Done"), btn -> {
			if (onDone != null) onDone.run();
			onClose();
		}).bounds(cx + cw / 2 - 60, y, 120, ROW_H).build());
    }
	//~ if >=26.1 'render' -> 'extractRenderState'{
    @Override public void render(GuiGraphics g, int mx, int my, float delta) {
        super.render(g, mx, my, delta);


		if (kb.keyMapping() != null) {
			String keyName = kb.keyMapping().getTranslatedKeyMessage().getString();
			boolean isBound = !keyName.equals("key.keyboard.unknown");
			g.drawCenteredString(font,
					Component.literal(isBound ? "§aBound to: §f" + keyName : "§cUnbound"),
					modalX + modalW / 2, infoY, COL_TEXT);
		}
    }
	//~}
}
