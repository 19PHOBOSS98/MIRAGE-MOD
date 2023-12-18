package net.phoboss.decobeacon.blocks.omnibeacon.forge;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.phoboss.decobeacon.blocks.omnibeacon.OmniBeaconBlockEntity;

public class OmniBeaconBlockEntityImpl extends OmniBeaconBlockEntity {


    public OmniBeaconBlockEntityImpl(BlockPos pos, BlockState state, Boolean isGhost) {
        super(pos, state, isGhost);
    }

    public OmniBeaconBlockEntityImpl(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public static OmniBeaconBlockEntity createPlatformSpecific(BlockPos pos, BlockState state,Boolean isGhost){
        return new OmniBeaconBlockEntityImpl( pos, state, isGhost);
    }

    public static OmniBeaconBlockEntity createPlatformSpecific(BlockPos pos, BlockState state){
        return new OmniBeaconBlockEntityImpl( pos, state);
    }

    @Override
    public Box getRenderBoundingBox() {
        BlockPos pos = this.getPos();
        return new Box(pos,pos.add(1,this.getDecoBeamSegmentsTotalHeight(),1));
    }

}
