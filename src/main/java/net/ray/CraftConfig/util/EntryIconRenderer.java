//package net.ray.CraftConfig.util;
//
//import com.mojang.blaze3d.systems.RenderSystem;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiGraphics ;
//import net.minecraft.client.renderer.entity.ItemRenderer;
//import net.minecraft.world.item.ItemStack;
//import org.jetbrains.annotations.Nullable;
//
//public final class EntryIconRenderer {
//
//    private EntryIconRenderer() {}
//
//
//    public static void render(GuiGraphics g, @Nullable ItemStack stack, int x, int y, int iconSize) {
//        if (stack == null || stack.isEmpty()) return;
//
//        Minecraft mc = Minecraft.getInstance();
//        float scale  = iconSize / 16.0f;
//
//        g.pose().pushPose();
//        g.pose().translate(x, y, 0);
//        g.pose().scale(scale, scale, 1.0f);
//
//        g.renderFakeItem(stack, 0, 0);
//
//        if (iconSize >= 16) {
//            g.renderItemDecorations(mc.font, stack, 0, 0);
//        }
//
//        g.pose().popPose();
//    }
//
//
//    public static void renderCentered(GuiGraphics g, @Nullable ItemStack stack,
//                                      int cellX, int cellY, int cellW, int cellH, int iconSize) {
//        if (stack == null || stack.isEmpty()) return;
//        int ox = cellX + (cellW - iconSize) / 2;
//        int oy = cellY + (cellH - iconSize) / 2;
//        render(g, stack, ox, oy, iconSize);
//    }
//
//    public static void renderPlaceholder(GuiGraphics g, int x, int y, int iconSize, int color) {
//        g.fill(x, y, x + iconSize, y + iconSize, color);
//        g.submitOutline(x, y, iconSize, iconSize, 0xFF555555);
//    }
//}
