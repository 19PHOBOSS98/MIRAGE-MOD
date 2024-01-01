package net.phoboss.mirage.items;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.phoboss.mirage.Mirage;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Mirage.MOD_ID, Registry.ITEM_KEY);

    public static void registerAll(){
        Mirage.LOGGER.debug("Registering Mod Items for "+ Mirage.MOD_ID);
        ITEMS.register();
    }
}
