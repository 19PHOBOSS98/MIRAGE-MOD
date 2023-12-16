package net.phoboss.decobeacon.blocks;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import net.phoboss.decobeacon.DecoBeacon;

import java.util.function.Supplier;

public class ModBlockEntitiesRegistry {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(DecoBeacon.MOD_ID, Registry.BLOCK_ENTITY_TYPE_KEY);

    public static <T extends BlockEntityType> RegistrySupplier<T> registerBlockEntities(String name, Supplier<T> block){
        return BLOCK_ENTITIES.register(name,block);
    }

    public static void registerAll() {
        DecoBeacon.LOGGER.info("Registering Mod Blocks for " + DecoBeacon.MOD_ID);
        BLOCK_ENTITIES.register();
    }
}
