package net.phoboss.decobeacon.blocks;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.entity.BlockEntityType;
import net.phoboss.decobeacon.blocks.decobeacon.DecoBeaconBlockEntity;

public class ModBlockEntities {

    public static final RegistrySupplier<BlockEntityType<DecoBeaconBlockEntity>> DECO_BEACON = ModBlockEntitiesRegistry.registerBlockEntities("deco_beacon",
            () ->   BlockEntityType.Builder.create(
                                    DecoBeaconBlockEntity::createPlatformSpecific,
                                    ModBlocks.DECO_BEACON.get(),
                                    ModBlocks.DECO_BEACON_GHOST.get(),
                                    ModBlocks.DECO_BEACON_FAKE.get(),
                                    ModBlocks.DECO_BEACON_GHOST_FAKE.get()
                                    ).build(null));
}
