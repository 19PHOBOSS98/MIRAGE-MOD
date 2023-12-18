package net.phoboss.decobeacon.blocks.omnibeaconghost;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.phoboss.decobeacon.blocks.ModBlockEntities;
import net.phoboss.decobeacon.blocks.decobeacon.DecoBeaconBlock;
import net.phoboss.decobeacon.blocks.omnibeacon.OmniBeaconBlock;
import net.phoboss.decobeacon.blocks.omnibeacon.OmniBeaconBlockEntity;
import org.jetbrains.annotations.Nullable;


public class OmniBeaconGhostBlock extends OmniBeaconBlock {
    public OmniBeaconGhostBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return OmniBeaconBlockEntity.createPlatformSpecific(pos,state,true);
    }
    /*
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.OMNI_BEACON.get(), OmniBeaconBlockEntity::tick);
    }
    */


}
