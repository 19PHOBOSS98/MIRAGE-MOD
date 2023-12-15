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
import net.phoboss.decobeacon.items.ModItemGroups;
import net.phoboss.decobeacon.items.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(DecoBeacon.MOD_ID, Registry.BLOCK_KEY);

    public static final RegistrySupplier<Block> DECO_BEACON = registerBlock(
            "deco_beacon",
            () -> new DecoBeaconBlock(AbstractBlock
                    .Settings.of(Material.GLASS, MapColor.DIAMOND_BLUE)
                    .luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
                    .nonOpaque()),
            ModItemGroups.DECO_BEACON,
            "block.decobeacon.notghost.tooltip");

    public static final RegistrySupplier<Block> DECO_BEACON_FAKE = registerBlock(
            "deco_beacon_fake",
            () -> new DecoBeaconBlock(AbstractBlock
                    .Settings.of(Material.GLASS, MapColor.DIAMOND_BLUE)
                    .luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
                    .nonOpaque()),
            ModItemGroups.DECO_BEACON,
            "block.decobeacon.notghost.tooltip");

    public static final RegistrySupplier<Block> DECO_BEACON_GHOST = registerBlock(
            "deco_beacon_ghost",
            () -> new DecoBeaconGhostBlock(AbstractBlock
                    .Settings.of(Material.GLASS, MapColor.DIAMOND_BLUE)
                    .luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
                    .nonOpaque()
                    .noCollision()),
            ModItemGroups.DECO_BEACON,
            "block.decobeacon.ghost.tooltip",
            "block.decobeacon.ghost.tooltip.shift");

    public static final RegistrySupplier<Block> DECO_BEACON_GHOST_FAKE = registerBlock(
            "deco_beacon_fake_ghost",
            () -> new DecoBeaconGhostBlock(AbstractBlock
                    .Settings.of(Material.GLASS, MapColor.DIAMOND_BLUE)
                    .luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
                    .nonOpaque()
                    .noCollision()),
            ModItemGroups.DECO_BEACON,
            "block.decobeacon.ghost.tooltip",
            "block.decobeacon.ghost.tooltip.shift");

    private static <T extends Block> RegistrySupplier<T> registerBlock(String name, Supplier<T> block, ItemGroup group){
        RegistrySupplier<T> toReturn = BLOCKS.register(name,block);
        registerBlockItem(name, toReturn, group);
        return toReturn;
    }
    private static <T extends Block> RegistrySupplier<Item> registerBlockItem(String name, RegistrySupplier<T> block, ItemGroup group){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Settings().group(group)));
    }

    private static <T extends Block> RegistrySupplier<T> registerBlock(String name, Supplier<T> block, ItemGroup group, String tooltipKey, String tooltipShiftKey){
        RegistrySupplier<T> toReturn = BLOCKS.register(name,block);
        registerBlockItem(name, toReturn, group, tooltipKey, tooltipShiftKey);
        return toReturn;
    }
    private static <T extends Block> RegistrySupplier<Item> registerBlockItem(String name, RegistrySupplier<T> block, ItemGroup group, String tooltipKey, String tooltipShiftKey){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Settings().group(group)) {
                    @Override
                    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
                        if (Screen.hasShiftDown()) {
                            tooltip.add(new TranslatableText(tooltipShiftKey));
                        } else {
                            tooltip.add(new TranslatableText(tooltipKey));
                        }
                    }
                }
            );
    }

    private static <T extends Block> RegistrySupplier<T> registerBlock(String name, Supplier<T> block, ItemGroup group, String tooltipKey){
        RegistrySupplier<T> toReturn = BLOCKS.register(name,block);
        registerBlockItem(name, toReturn, group, tooltipKey);
        return toReturn;
    }
    private static <T extends Block> RegistrySupplier<Item> registerBlockItem(String name, RegistrySupplier<T> block, ItemGroup group, String tooltipKey){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Settings().group(group)) {
                    @Override
                    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
                            tooltip.add(new TranslatableText(tooltipKey));
                    }
                }
        );
    }

    public static void registerAll() {
        DecoBeacon.LOGGER.info("Registering Mod Blocks for " + DecoBeacon.MOD_ID);
        BLOCKS.register();
    }
}
