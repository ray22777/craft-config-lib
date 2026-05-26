package net.ray.CraftConfig.platform.fabric;

//? fabric {

/*import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import net.ray.CraftConfig.api.registry.CraftConfigRegistry;
import net.ray.CraftConfig.example.ExampleConfig;
import net.ray.CraftConfig.platform.CraftConfigMod;
import net.ray.CraftConfig.preset.PresetRegisteration;

@Entrypoint("client")
public class FabricClientEntrypoint implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        FabricKeybindRegistry.init();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
			FabricCommands.register(dispatcher));
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			PresetRegisteration.checkPreset();
        });
    }
}
*///?}
