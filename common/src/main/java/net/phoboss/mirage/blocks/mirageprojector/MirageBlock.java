package net.phoboss.mirage.blocks.mirageprojector;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.phoboss.mirage.blocks.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class MirageBlock extends BlockWithEntity implements BlockEntityProvider {
    public MirageBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Item mainHandItem = player.getMainHandStack().getItem();
        if(hand == Hand.MAIN_HAND){

                if(mainHandItem instanceof BlockItem bi){
                    MirageBlockEntity entity = (MirageBlockEntity) world.getBlockEntity(pos);
                        entity.setCurrentBlock(bi.getBlock());
                    return ActionResult.SUCCESS;

                }

        }
        return ActionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MirageBlockEntity(pos,state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.MIRAGE_BLOCK.get(), MirageBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

}
