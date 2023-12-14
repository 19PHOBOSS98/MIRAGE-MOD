package net.phoboss.decobeacon.items;

import dev.architectury.registry.CreativeTabRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.phoboss.decobeacon.blocks.ModBlocks;


public class ModItemGroups {

    public static ItemGroup DECO_BEACON  = CreativeTabRegistry.create(
            new Identifier("decobeacon", "deco_beacon"),
            () -> new ItemStack(ModBlocks.DECO_BEACON_GHOST.get())
    );

}
