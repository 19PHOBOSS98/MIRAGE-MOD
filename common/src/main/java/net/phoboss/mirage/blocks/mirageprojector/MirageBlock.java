package net.phoboss.mirage.blocks.mirageprojector;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

    public static void loadMirage(MirageBlockEntity blockEntity,String fileName){
        if(blockEntity == null) {
            return;
        }
        blockEntity.setSchematicMirage(fileName);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack mainHandItemStack = player.getMainHandStack();
        Item mainHandItem = player.getMainHandStack().getItem();
        if(hand == Hand.MAIN_HAND){

            MirageBlockEntity blockEntity = (MirageBlockEntity) world.getBlockEntity(pos);
            String fileName = "miragetestingwentities";
            /*
            String fileName = "test";
            String fileName = "redstonetest";
            String fileName = "mirage1";
            String fileName = "paintings";
            String fileName = "portal1";
            String fileName = "warp";
            */
            loadMirage(blockEntity,fileName);


            return ActionResult.SUCCESS;
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
