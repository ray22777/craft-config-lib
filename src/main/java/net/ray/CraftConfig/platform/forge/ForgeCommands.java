package net.ray.CraftConfig.platform.forge;
//? forge {
/*import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ray.CraftConfig.ComponentUtils;
import net.ray.CraftConfig.CraftConfig;
import net.ray.CraftConfig.HologramMod;

@Mod.EventBusSubscriber(modid = HologramMod.MOD_ID, value = Dist.CLIENT)
public class ForgeCommands {

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("hologram")
                .then(Commands.literal("create")
                        .then(Commands.argument("text", StringArgumentType.string())
                                .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                                                .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                                        .executes(context -> createHologram(
                                                                context,
                                                                StringArgumentType.getString(context, "text"),
                                                                DoubleArgumentType.getDouble(context, "x"),
                                                                DoubleArgumentType.getDouble(context, "y"),
                                                                DoubleArgumentType.getDouble(context, "z"),
                                                                false
                                                        ))
                                                        .then(Commands.argument("renderOnTop", BoolArgumentType.bool())
                                                                .executes(context -> createHologram(
                                                                        context,
                                                                        StringArgumentType.getString(context, "text"),
                                                                        DoubleArgumentType.getDouble(context, "x"),
                                                                        DoubleArgumentType.getDouble(context, "y"),
                                                                        DoubleArgumentType.getDouble(context, "z"),
                                                                        BoolArgumentType.getBool(context, "renderOnTop")
                                                                ))
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal("clearAll")
                        .executes(ForgeCommands::clearAll))
        );
    }

    private static int clearAll(CommandContext<CommandSourceStack> context) {
        try {
			CraftConfig.clearTagged("command");

            context.getSource().sendSuccess(
                    () -> Component.literal("Cleared all holograms").withStyle(ChatFormatting.GREEN),
                    false
            );

            return 1;

        } catch (Exception e) {
            context.getSource().sendFailure(
                    Component.literal("Error clearing holograms").withStyle(ChatFormatting.RED)
            );
            e.printStackTrace();
            return 0;
        }
    }

    private static int createHologram(CommandContext<CommandSourceStack> context, String text,
                                      double x, double y, double z, boolean renderOnTop) {
        try {
            Component comp = ComponentUtils.parseColorCodes(text);
            CraftConfig.create(comp, x, y, z).renderOnTop(renderOnTop).tag("command");

            context.getSource().sendSuccess(
                    () -> Component.literal("Created hologram: '")
                            .append(Component.literal(text).withStyle(ChatFormatting.AQUA))
                            .append("' at " + String.format("%.1f, %.1f, %.1f", x, y, z))
                            .withStyle(ChatFormatting.GREEN),
                    false
            );

            return 1;

        } catch (Exception e) {
            context.getSource().sendFailure(
                    Component.literal("Error creating hologram").withStyle(ChatFormatting.RED)
            );
            e.printStackTrace();
            return 0;
        }
    }
}
*///?}
