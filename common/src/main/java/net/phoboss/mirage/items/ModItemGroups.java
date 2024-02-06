package net.phoboss.mirage.items;

import dev.architectury.registry.CreativeTabRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.phoboss.mirage.blocks.ModBlocks;


public class ModItemGroups {

    public static ItemGroup MIRAGE  = CreativeTabRegistry.create(
            new Identifier("mirage", "mirage"),
            () -> new ItemStack(
                    ModBlocks.MIRAGE_BLOCK.get())
                    //ModBlocks.MIRAGE_BLOCK)
    );

}
