package net.phoboss.mirage.client.rendering.customworld.fabric;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.world.World;
import net.phoboss.mirage.client.rendering.customworld.MirageWorld;

public class MirageWorldImpl extends MirageWorld {

    public MirageWorldImpl(World world) {
        super(world);
    }

    public static boolean addToManualRenderList(long blockPosKey, StateNEntity stateNEntity, Long2ObjectOpenHashMap<StateNEntity> manualRenderBlocks){
        /*if(stateNEntity.blockState.getBlock() instanceof DecoBeaconBlock){
            manualRenderTranslucentBlocks.put(blockPosKey, stateNEntity);
            return true;
        }*/
        return false;
    }


}
