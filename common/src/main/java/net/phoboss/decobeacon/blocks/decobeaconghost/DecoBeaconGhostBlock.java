package net.phoboss.decobeacon.blocks.decobeaconghost;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.phoboss.decobeacon.blocks.ModBlockEntities;
import net.phoboss.decobeacon.blocks.decobeacon.DecoBeaconBlock;
import net.phoboss.decobeacon.blocks.decobeacon.DecoBeaconBlockEntity;
import org.jetbrains.annotations.Nullable;

public class DecoBeaconGhostBlock extends DecoBeaconBlock {
    public DecoBeaconGhostBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return DecoBeaconBlockEntity.createPlatformSpecific(pos,state,true);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.DECO_BEACON.get(), DecoBeaconBlockEntity::tick);
    }
}
