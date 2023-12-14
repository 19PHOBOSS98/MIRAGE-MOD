package net.phoboss.decobeacon.fabric;

import net.phoboss.decobeacon.DecoBeacon;
import net.fabricmc.api.ModInitializer;

public class DecoBeaconFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        DecoBeacon.init();
    }
}
