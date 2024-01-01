package net.phoboss.mirage.fabric;

import net.phoboss.mirage.Mirage;
import net.fabricmc.api.ModInitializer;

public class MirageFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Mirage.init();
    }
}
