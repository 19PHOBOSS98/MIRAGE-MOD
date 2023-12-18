package net.phoboss.decobeacon.blocks;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import net.phoboss.decobeacon.DecoBeacon;
import net.phoboss.decobeacon.blocks.decobeacon.DecoBeaconBlockEntity;
import net.phoboss.decobeacon.blocks.omnibeacon.OmniBeaconBlockEntity;


import java.util.function.Supplier;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(DecoBeacon.MOD_ID, Registry.BLOCK_ENTITY_TYPE_KEY);

    public static final RegistrySupplier<BlockEntityType<DecoBeaconBlockEntity>> DECO_BEACON = registerBlockEntities("deco_beacon",
            () ->   BlockEntityType.Builder.create(
                    DecoBeaconBlockEntity::createPlatformSpecific,
                    ModBlocks.DECO_BEACON.get(),
                    ModBlocks.DECO_BEACON_GHOST.get(),
                    ModBlocks.DECO_BEACON_FAKE.get(),
                    ModBlocks.DECO_BEACON_GHOST_FAKE.get()
            ).build(null));


    public static final RegistrySupplier<BlockEntityType<OmniBeaconBlockEntity>> OMNI_BEACON = registerBlockEntities("omni_beacon",
            () ->   BlockEntityType.Builder.create(
                    OmniBeaconBlockEntity::createPlatformSpecific,
                    ModBlocks.OMNI_BEACON.get(),
                    ModBlocks.OMNI_BEACON_GHOST.get()
            ).build(null));

    public static <T extends BlockEntityType> RegistrySupplier<T> registerBlockEntities(String name, Supplier<T> block){
        return BLOCK_ENTITIES.register(name,block);
    }

    public static void registerAll() {
        DecoBeacon.LOGGER.info("Registering Mod BlockEntities for " + DecoBeacon.MOD_ID);
        BLOCK_ENTITIES.register();
    }
}
