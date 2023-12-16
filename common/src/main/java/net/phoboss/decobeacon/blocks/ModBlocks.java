package net.phoboss.decobeacon.blocks;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.state.property.Properties;
import net.phoboss.decobeacon.blocks.decobeacon.DecoBeaconBlock;
import net.phoboss.decobeacon.blocks.decobeaconghost.DecoBeaconGhostBlock;
import net.phoboss.decobeacon.items.ModItemGroups;

public class ModBlocks {

    public static final RegistrySupplier<Block> DECO_BEACON = ModBlocksRegistry.registerBlock(
            "deco_beacon",
            () -> new DecoBeaconBlock(AbstractBlock
                    .Settings.of(Material.GLASS, MapColor.DIAMOND_BLUE)
                    .luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
                    .nonOpaque()),
            ModItemGroups.DECO_BEACON,
            new ModBlocksRegistry.ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacon.notghost.tooltip")
    );

    public static final RegistrySupplier<Block> DECO_BEACON_FAKE = ModBlocksRegistry.registerBlock(
            "deco_beacon_fake",
            () -> new DecoBeaconBlock(AbstractBlock
                    .Settings.of(Material.GLASS, MapColor.DIAMOND_BLUE)
                    .luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
                    .nonOpaque()),
            ModItemGroups.DECO_BEACON,
            new ModBlocksRegistry.ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacon.notghost.tooltip")
    );

    public static final RegistrySupplier<Block> DECO_BEACON_GHOST = ModBlocksRegistry.registerBlock(
            "deco_beacon_ghost",
            () -> new DecoBeaconGhostBlock(AbstractBlock
                    .Settings.of(Material.GLASS, MapColor.DIAMOND_BLUE)
                    .luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
                    .nonOpaque()
                    .noCollision()),
            ModItemGroups.DECO_BEACON,
            new ModBlocksRegistry.ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacon.ghost.tooltip")
                    .setTooltipShiftKey("block.decobeacon.ghost.tooltip.shift")
    );

    public static final RegistrySupplier<Block> DECO_BEACON_GHOST_FAKE = ModBlocksRegistry.registerBlock(
            "deco_beacon_fake_ghost",
            () -> new DecoBeaconGhostBlock(AbstractBlock
                    .Settings.of(Material.GLASS, MapColor.DIAMOND_BLUE)
                    .luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
                    .nonOpaque()
                    .noCollision()),
            ModItemGroups.DECO_BEACON,
            new ModBlocksRegistry.ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacon.ghost.tooltip")
                    .setTooltipShiftKey("block.decobeacon.ghost.tooltip.shift")
    );

}
