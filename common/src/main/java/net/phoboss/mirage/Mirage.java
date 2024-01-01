package net.phoboss.mirage;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.phoboss.mirage.blocks.ModBlockEntities;
import net.phoboss.mirage.blocks.ModBlocks;
import net.phoboss.mirage.client.rendering.ModRendering;
import net.phoboss.mirage.items.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Mirage {
    public static final String MOD_ID = "mirage";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static void init() {
        ModBlocks.registerAll();
        ModBlockEntities.registerAll();
        ModItems.registerAll();

        EnvExecutor.runInEnv(Env.CLIENT, () -> Mirage.Client::initClient);
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
