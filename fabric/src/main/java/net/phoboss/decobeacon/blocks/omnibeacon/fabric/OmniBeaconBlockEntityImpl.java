package net.phoboss.decobeacon.blocks.omnibeacon.fabric;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.phoboss.decobeacon.blocks.decobeacon.DecoBeaconBlockEntity;
import net.phoboss.decobeacon.blocks.omnibeacon.OmniBeaconBlockEntity;

public class OmniBeaconBlockEntityImpl extends OmniBeaconBlockEntity {


    public OmniBeaconBlockEntityImpl(BlockPos pos, BlockState state, Boolean isGhost) {
        super(pos, state, isGhost);
    }

    public OmniBeaconBlockEntityImpl(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public static OmniBeaconBlockEntity createPlatformSpecific(BlockPos pos, BlockState state,Boolean isGhost){
        return new OmniBeaconBlockEntity( pos, state, isGhost);
    }

    public static OmniBeaconBlockEntity createPlatformSpecific(BlockPos pos, BlockState state){
        return new OmniBeaconBlockEntity( pos, state);
    }

}
