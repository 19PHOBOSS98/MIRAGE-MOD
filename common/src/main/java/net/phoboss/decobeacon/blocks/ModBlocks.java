package net.phoboss.decobeacon.blocks;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.phoboss.decobeacon.DecoBeacon;
import net.phoboss.decobeacon.blocks.decobeacon.DecoBeaconBlock;
import net.phoboss.decobeacon.blocks.decobeaconghost.DecoBeaconGhostBlock;
import net.phoboss.decobeacon.blocks.omnibeacon.OmniBeaconBlock;
import net.phoboss.decobeacon.blocks.omnibeaconghost.OmniBeaconGhostBlock;
import net.phoboss.decobeacon.items.ModItemGroups;
import net.phoboss.decobeacon.items.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(DecoBeacon.MOD_ID, Registry.BLOCK_KEY);

    public static final RegistrySupplier<Block> DECO_BEACON = ModBlocks.registerBlock(
            "deco_beacon",
            () -> new DecoBeaconBlock(AbstractBlock
                    .Settings.of(Material.GLASS, MapColor.DIAMOND_BLUE)
                    .luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
                    .nonOpaque()),
            ModItemGroups.DECO_BEACON,
            new ModBlocks.ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacon.notghost.tooltip")
    );

    public static final RegistrySupplier<Block> DECO_BEACON_FAKE = ModBlocks.registerBlock(
            "deco_beacon_fake",
            () -> new DecoBeaconBlock(AbstractBlock
                    .Settings.of(Material.GLASS, MapColor.DIAMOND_BLUE)
                    .luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
                    .nonOpaque()),
            ModItemGroups.DECO_BEACON,
            new ModBlocks.ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacon.notghost.tooltip")
    );

    public static final RegistrySupplier<Block> DECO_BEACON_GHOST = ModBlocks.registerBlock(
            "deco_beacon_ghost",
            () -> new DecoBeaconGhostBlock(AbstractBlock
                    .Settings.of(Material.GLASS, MapColor.DIAMOND_BLUE)
                    .luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
                    .nonOpaque()
                    .noCollision()),
            ModItemGroups.DECO_BEACON,
            new ModBlocks.ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacon.ghost.tooltip")
                    .setTooltipShiftKey("block.decobeacon.ghost.tooltip.shift")
    );

    public static final RegistrySupplier<Block> DECO_BEACON_GHOST_FAKE = ModBlocks.registerBlock(
            "deco_beacon_fake_ghost",
            () -> new DecoBeaconGhostBlock(AbstractBlock
                    .Settings.of(Material.GLASS, MapColor.DIAMOND_BLUE)
                    .luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
                    .nonOpaque()
                    .noCollision()),
            ModItemGroups.DECO_BEACON,
            new ModBlocks.ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacon.ghost.tooltip")
                    .setTooltipShiftKey("block.decobeacon.ghost.tooltip.shift")
    );

    public static final RegistrySupplier<Block> OMNI_BEACON = ModBlocks.registerBlock(
            "omni_beacon",
            () -> new OmniBeaconBlock(AbstractBlock
                    .Settings.of(Material.GLASS, MapColor.DIAMOND_BLUE)
                    .luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
                    .nonOpaque()),
            ModItemGroups.DECO_BEACON,
            new ModBlocks.ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacon.omni_beacon.tooltip")
                    .setTooltipShiftKey("block.decobeacon.omni_beacon.tooltip.shift")
    );

    public static final RegistrySupplier<Block> OMNI_BEACON_GHOST = ModBlocks.registerBlock(
            "omni_beacon_ghost",
            () -> new OmniBeaconGhostBlock(AbstractBlock
                    .Settings.of(Material.GLASS, MapColor.DIAMOND_BLUE)
                    .luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
                    .nonOpaque()
                    .noCollision()),
            ModItemGroups.DECO_BEACON,
            new ModBlocks.ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacon.omni_beacon.tooltip")
                    .setTooltipShiftKey("block.decobeacon.omni_beacon.tooltip.shift")
    );



    public static <T extends Block> RegistrySupplier<T> registerBlock(String name, Supplier<T> block, ItemGroup group){
        RegistrySupplier<T> toReturn = BLOCKS.register(name,block);
        registerBlockItem(name, toReturn, group);
        return toReturn;
    }
    public static <T extends Block> RegistrySupplier<Item> registerBlockItem(String name, RegistrySupplier<T> block, ItemGroup group){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Settings().group(group)));
    }

    public static <T extends Block> RegistrySupplier<T> registerBlock(String name, Supplier<T> block, ItemGroup group, ExtraItemSettings extraItemSettings){
        RegistrySupplier<T> toReturn = BLOCKS.register(name,block);
        registerBlockItem(name, toReturn, group, extraItemSettings);
        return toReturn;
    }
    public static <T extends Block> RegistrySupplier<Item> registerBlockItem(String name, RegistrySupplier<T> block, ItemGroup group, ExtraItemSettings extraItemSettings){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Settings().maxCount(extraItemSettings.stackLimit)
                        .group(group)) {
                    @Override
                    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
                        if (Screen.hasShiftDown()) {
                            if(extraItemSettings.tooltipShiftKey!=null){
                                tooltip.add(new TranslatableText(extraItemSettings.tooltipShiftKey));
                            }
                        } else {
                            if(extraItemSettings.tooltipKey!=null) {
                                tooltip.add(new TranslatableText(extraItemSettings.tooltipKey));
                            }
                        }
                    }
                }
        );
    }


    public static void registerAll() {
        DecoBeacon.LOGGER.info("Registering Mod Blocks for " + DecoBeacon.MOD_ID);
        BLOCKS.register();
    }

    public static class ExtraItemSettings {
        public int stackLimit=64;
        public String tooltipShiftKey;

        public String tooltipKey;

        public ExtraItemSettings setStackLimit(int stackLimit) {
            this.stackLimit = stackLimit;
            return this;
        }


        public ExtraItemSettings setTooltipShiftKey(String tooltipShiftKey) {
            this.tooltipShiftKey = tooltipShiftKey;
            return this;
        }

        public ExtraItemSettings setTooltipKey(String tooltipKey) {
            this.tooltipKey = tooltipKey;
            return this;
        }

        public ExtraItemSettings() {
        }
    }
}
