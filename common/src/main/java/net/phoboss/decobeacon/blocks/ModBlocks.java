package net.phoboss.decobeacon.blocks;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.phoboss.decobeacon.DecoBeacon;
import net.phoboss.decobeacon.blocks.decobeacon.DecoBeaconBlock;

import net.phoboss.decobeacon.blocks.decobeaconghost.DecoBeaconGhostBlock;
import net.phoboss.decobeacon.blocks.fakelamp.FakeLamp;
import net.phoboss.decobeacon.items.ModItemGroups;
import net.phoboss.decobeacon.items.ModItems;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(DecoBeacon.MOD_ID, Registry.BLOCK_KEY);

    public static final RegistrySupplier<Block> FAKE_LAMP = registerBlock("fake_lamp",
            () -> new FakeLamp(AbstractBlock
                    .Settings.of(Material.GLASS)
                    .luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
                    .nonOpaque()
                    .noCollision()),
            ModItemGroups.DECO_BEACON);

    public static final RegistrySupplier<Block> DECO_BEACON = registerBlock(
            "deco_beacon",
            () -> new DecoBeaconBlock(AbstractBlock
                    .Settings.of(Material.GLASS)
                    .luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
                    .nonOpaque()),
            ModItemGroups.DECO_BEACON);

    private static boolean never(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return false;
    }

    public static final RegistrySupplier<Block> DECO_BEACON_FAKE = registerBlock(
            "deco_beacon_fake",
            () -> new DecoBeaconBlock(AbstractBlock
                    .Settings.of(Material.GLASS, MapColor.DIAMOND_BLUE)
                    .luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
                    .nonOpaque()),
            ModItemGroups.DECO_BEACON);

    public static final RegistrySupplier<Block> DECO_BEACON_GHOST = registerBlock(
            "deco_beacon_ghost",
            () -> new DecoBeaconGhostBlock(AbstractBlock
                    .Settings.of(Material.GLASS, MapColor.DIAMOND_BLUE)
                    .luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
                    .nonOpaque()
                    .noCollision()),
            ModItemGroups.DECO_BEACON);

    public static final RegistrySupplier<Block> DECO_BEACON_GHOST_FAKE = registerBlock(
            "deco_beacon_fake_ghost",
            () -> new DecoBeaconGhostBlock(AbstractBlock
                    .Settings.of(Material.GLASS, MapColor.DIAMOND_BLUE)
                    .luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
                    .nonOpaque()
                    .noCollision()),
            ModItemGroups.DECO_BEACON);

    private static <T extends Block> RegistrySupplier<T> registerBlock(String name, Supplier<T> block, ItemGroup group){
        RegistrySupplier<T> toReturn = BLOCKS.register(name,block);
        registerBlockItem(name, toReturn, group);
        return toReturn;
    }
    private static <T extends Block> RegistrySupplier<Item> registerBlockItem(String name, RegistrySupplier<T> block, ItemGroup group){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Settings().group(group)));
    }

    public static void registerAll() {
        DecoBeacon.LOGGER.info("Registering Mod Blocks for " + DecoBeacon.MOD_ID);
        BLOCKS.register();
    }
}
