package net.tricube.CraftConfig.platform.fabric;
//~ fabric_cmd
//? fabric {

/*import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.tricube.CraftConfig.api.registry.CraftConfigRegistry;
public class FabricCommands {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        for (CraftConfigRegistry.Entry entry : CraftConfigRegistry.all()) {
            if (!entry.commandEnabled()) continue;

            dispatcher.register(
					ClientCommandManager.literal("craftconfig")
                            .then(ClientCommandManager.literal(entry.modId())
                                    .executes(ctx -> openScreen(entry)))
            );

            if (entry.customCommand() != null && !entry.customCommand().isBlank()) {
                dispatcher.register(
						ClientCommandManager.literal(entry.customCommand())
                                .executes(ctx -> openScreen(entry))
                );
            }
        }
    }

    private static int openScreen(CraftConfigRegistry.Entry entry) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return 0;
		//? if <=1.21.1 {
		mc.tell(() -> mc.setScreen(entry.config().createScreen(mc.screen)));
		//?} else {
		/^mc.schedule(() -> mc.setScreen(entry.config().createScreen(mc.screen)));
		^///?}

        return 1;
    }
}
*///?}
