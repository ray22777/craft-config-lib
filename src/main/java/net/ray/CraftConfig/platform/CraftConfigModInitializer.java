package net.ray.CraftConfig.platform;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.ray.CraftConfig.example.ExampleConfig;
import org.slf4j.Logger;

import java.util.List;


public class CraftConfigModInitializer {
    private static Logger log;
    protected static void setLogger(Logger l){
        log = l;
    }
    protected static Logger getLogger(){
        return log;
    }
    public static void onInit(Logger log){
        setLogger(log);
        CraftConfigModInitializer.getLogger().warn(" initialized.");



    }
}
