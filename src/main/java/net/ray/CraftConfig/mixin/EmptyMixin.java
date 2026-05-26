package net.ray.CraftConfig.mixin;


import org.spongepowered.asm.mixin.Mixin;


@Mixin(net.minecraft.client.renderer.GameRenderer.class)
public class EmptyMixin {
//this only exists to fix an issue that forge jar file would not build due to missing mixin refmap
}
