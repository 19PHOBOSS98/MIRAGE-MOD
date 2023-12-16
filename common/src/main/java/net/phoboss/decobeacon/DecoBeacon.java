package net.phoboss.decobeacon;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.phoboss.decobeacon.blocks.ModBlockEntitiesRegistry;
import net.phoboss.decobeacon.blocks.ModBlocksRegistry;
import net.phoboss.decobeacon.client.rendering.ModRenderingRegistry;
import net.phoboss.decobeacon.items.ModItemsRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DecoBeacon {
    public static final String MOD_ID = "decobeacon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static void init() {
        ModBlocksRegistry.registerAll();
        ModBlockEntitiesRegistry.registerAll();
        ModItemsRegistry.registerAll();
        EnvExecutor.runInEnv(Env.CLIENT, () -> DecoBeacon.Client::initClient);
    }

    @Environment(EnvType.CLIENT)
    public static class Client {
        @Environment(EnvType.CLIENT)
        public static void initClient() {
            ClientLifecycleEvent.CLIENT_STARTED.register((client) ->
                {
                    LOGGER.info("Client starting!");
                    ModRenderingRegistry.registerAll();
                });
            ClientLifecycleEvent.CLIENT_STOPPING.register((client) ->
                {
                    LOGGER.info("Client stopping!");
                });
        }
    }
}
