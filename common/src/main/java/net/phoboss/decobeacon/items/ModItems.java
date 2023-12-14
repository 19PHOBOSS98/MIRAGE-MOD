package net.phoboss.decobeacon.items;

import dev.architectury.registry.fuel.FuelRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.phoboss.decobeacon.DecoBeacon;

import java.util.function.Consumer;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(DecoBeacon.MOD_ID, Registry.ITEM_KEY);

    public static void registerAll(){
        DecoBeacon.LOGGER.debug("Registering Mod Items for "+ DecoBeacon.MOD_ID);
        ITEMS.register();
    }
}
