package net.phoboss.decobeacon.forge;

import dev.architectury.platform.forge.EventBuses;
import net.phoboss.decobeacon.DecoBeacon;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DecoBeacon.MOD_ID)
public class DecoBeaconForge {
    public DecoBeaconForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(DecoBeacon.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        DecoBeacon.init();
    }
}
