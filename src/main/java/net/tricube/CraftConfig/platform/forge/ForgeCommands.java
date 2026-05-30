package net.tricube.CraftConfig.platform.forge;
//? forge {
/*import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.tricube.CraftConfig.api.registry.CraftConfigRegistry;

public class ForgeCommands {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		for (CraftConfigRegistry.Entry entry : CraftConfigRegistry.all()) {
			if (!entry.commandEnabled()) continue;

			dispatcher.register(
					Commands.literal("craftconfig")
							.then(Commands.literal(entry.modId())
									.executes(ctx -> openScreen(entry)))
			);

			if (entry.customCommand() != null && !entry.customCommand().isBlank()) {
				dispatcher.register(
						Commands.literal(entry.customCommand())
								.executes(ctx -> openScreen(entry))
				);
			}
		}
	}

	private static int openScreen(CraftConfigRegistry.Entry entry) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) return 0;
		//~ if >=1.21.2 '.tell' -> '.schedule'
		mc.tell(() -> mc.setScreen(entry.config().createScreen(mc.screen)));
		return 1;
	}
}
*///?}
