package net.phoboss.decobeacon;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.phoboss.decobeacon.blocks.ModBlockEntities;
import net.phoboss.decobeacon.blocks.ModBlocks;
import net.phoboss.decobeacon.client.rendering.ModRendering;
import net.phoboss.decobeacon.items.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DecoBeacon {
    public static final String MOD_ID = "decobeacon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static void init() {
        ModBlocks.registerAll();
        ModBlockEntities.registerAll();
        ModItems.registerAll();

        EnvExecutor.runInEnv(Env.CLIENT, () -> DecoBeacon.Client::initClient);
    }

    @Environment(EnvType.CLIENT)
    public static class Client {
        @Environment(EnvType.CLIENT)
        public static void initClient() {
            ClientLifecycleEvent.CLIENT_STARTED.register((client) ->
                {
                    LOGGER.info("Client starting!");
                    ModRendering.registerAll();
                });
            ClientLifecycleEvent.CLIENT_STOPPING.register((client) ->
                {
                    LOGGER.info("Client stopping!");
                });
        }
    }
}
