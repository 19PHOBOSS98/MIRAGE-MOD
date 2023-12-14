package net.phoboss.decobeacon.blocks.decobeacon.fabric;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.phoboss.decobeacon.blocks.decobeacon.DecoBeaconBlockEntity;

public class DecoBeaconBlockEntityImpl extends DecoBeaconBlockEntity {


    public DecoBeaconBlockEntityImpl(BlockPos pos, BlockState state, Boolean isGhost) {
        super(pos, state, isGhost);
    }

    public DecoBeaconBlockEntityImpl(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public static DecoBeaconBlockEntity createPlatformSpecific(BlockPos pos, BlockState state,Boolean isGhost){
        return new DecoBeaconBlockEntity( pos, state, isGhost);
    }

    public static DecoBeaconBlockEntity createPlatformSpecific(BlockPos pos, BlockState state){
        return new DecoBeaconBlockEntity( pos, state);
    }

}
