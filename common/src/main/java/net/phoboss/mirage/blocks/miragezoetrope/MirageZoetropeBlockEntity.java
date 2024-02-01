package net.phoboss.mirage.blocks.miragezoetrope;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.phoboss.mirage.blocks.ModBlockEntities;
import net.phoboss.mirage.blocks.mirageprojector.MirageBlockEntity;
import net.phoboss.mirage.client.rendering.customworld.MirageWorld;


public class MirageZoetropeBlockEntity extends MirageBlockEntity {


    public MirageZoetropeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MIRAGE_ZOETROPE_BLOCK.get(), pos, state);
    }
    public MirageZoetropeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, MirageZoetropeBlockEntity blockEntity) {
        if(blockEntity.isPowered()) {
            MirageWorld mirageWorld = blockEntity.getMirageWorld();
            if (mirageWorld != null) {
                blockEntity.getMirageWorld().tick();
            }
        }
    }
}
