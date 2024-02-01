package net.phoboss.mirage.blocks;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import net.phoboss.mirage.Mirage;
import net.phoboss.mirage.blocks.mirageprojector.MirageBlockEntity;
import net.phoboss.mirage.blocks.miragezoetrope.MirageZoetropeBlockEntity;

import java.util.function.Supplier;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Mirage.MOD_ID, Registry.BLOCK_ENTITY_TYPE_KEY);

    public static final RegistrySupplier<BlockEntityType<MirageBlockEntity>> MIRAGE_BLOCK = registerBlockEntities("mirage_block",
            () ->   BlockEntityType.Builder.create(
                    MirageBlockEntity::new,
                    ModBlocks.MIRAGE_BLOCK.get()
            ).build(null));

    public static final RegistrySupplier<BlockEntityType<MirageZoetropeBlockEntity>> MIRAGE_ZOETROPE_BLOCK = registerBlockEntities("mirage_zoetrope_block",
            () ->   BlockEntityType.Builder.create(
                    MirageZoetropeBlockEntity::new,
                    ModBlocks.MIRAGE_ZOETROPE_BLOCK.get()
            ).build(null));

    public static <T extends BlockEntityType> RegistrySupplier<T> registerBlockEntities(String name, Supplier<T> block){
        return BLOCK_ENTITIES.register(name,block);
    }

    public static void registerAll() {
        Mirage.LOGGER.info("Registering Mod BlockEntities for " + Mirage.MOD_ID);
        BLOCK_ENTITIES.register();
    }
}
