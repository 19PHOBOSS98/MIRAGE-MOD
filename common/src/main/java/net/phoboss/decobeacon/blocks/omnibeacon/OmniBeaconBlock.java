package net.phoboss.decobeacon.blocks.omnibeacon;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.phoboss.decobeacon.blocks.ModBlockEntities;
import net.phoboss.decobeacon.blocks.decobeacon.DecoBeaconBlock;
import net.phoboss.decobeacon.blocks.decobeacon.DecoBeaconBlockEntity;
import org.jetbrains.annotations.Nullable;


public class OmniBeaconBlock extends DecoBeaconBlock {
    public OmniBeaconBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return OmniBeaconBlockEntity.createPlatformSpecific(pos,state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.OMNI_BEACON.get(), OmniBeaconBlockEntity::tick);
    }


    @Override
    public ActionResult onUse(BlockState state,
                              World world,
                              BlockPos pos,
                              PlayerEntity player,
                              Hand hand,
                              BlockHitResult hit) {
        Item mainHandItem = player.getMainHandStack().getItem();
        if(hand == Hand.MAIN_HAND){
            if(!world.isClient()){
                OmniBeaconBlockEntity blockEntity = (OmniBeaconBlockEntity) world.getBlockEntity(pos);
                if(mainHandItem instanceof DyeItem itemDye){
                    world.setBlockState(pos,state.with(COLOR,itemDye.getColor().getId()),Block.NOTIFY_ALL);
                    return ActionResult.SUCCESS;

                }else if(mainHandItem == Items.AIR){
                    int delta = player.isSneaking() ? -1 : 1;
                    int currentColor = Math.floorMod((state.get(COLOR) + delta),16);
                    world.setBlockState(pos,state.with(COLOR,currentColor),Block.NOTIFY_ALL);
                    return ActionResult.SUCCESS;

                }else if(mainHandItem == Items.REDSTONE_TORCH){
                    blockEntity.setActiveLow(!blockEntity.isActiveLow());
                    return ActionResult.SUCCESS;

                }else if(mainHandItem == Items.SOUL_TORCH){
                    blockEntity.setTransparent(!blockEntity.isTransparent());
                    return ActionResult.SUCCESS;

                }else if(mainHandItem == Items.TORCH){
                    blockEntity.setBeamDirection(hit.getSide().getUnitVector());
                    return ActionResult.SUCCESS;

                }
            }

        }
        return ActionResult.PASS;
    }


}
