package net.phoboss.mirage.client.rendering.customworld.fabric;

import dev.architectury.platform.Platform;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.jellysquid.mods.sodium.client.render.texture.SpriteUtil;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.phoboss.decobeacon.blocks.decobeacon.DecoBeaconBlock;
import net.phoboss.mirage.client.rendering.customworld.MirageWorld;

import java.util.Random;

public class MirageWorldImpl extends MirageWorld {

    public MirageWorldImpl(World world) {
        super(world);
    }


    public static void markAnimatedSprite(ObjectArrayList<Sprite> animatedSprites){
        if(!Platform.isModLoaded("sodium")){
            return;
        }
        animatedSprites.forEach((sprite)->{
            SpriteUtil.markSpriteActive(sprite);
        });
    }
    public static boolean addToManualBlockRenderList(long blockPosKey, StateNEntity stateNEntity, Long2ObjectOpenHashMap<StateNEntity> manualRenderBlocks){
        if(Platform.isModLoaded("decobeacon")) {
            if (stateNEntity.blockState.getBlock() instanceof DecoBeaconBlock) {
                manualRenderBlocks.put(blockPosKey, stateNEntity);
                return true;
            }
        }

        return false;
    }
    public static boolean isOnTranslucentRenderLayer(BlockState blockState){
        return RenderLayers.getBlockLayer(blockState) == RenderLayer.getTranslucent();
    }
    public static void refreshVertexBuffersIfNeeded(BlockPos projectorPos, MirageWorld mirageWorld){
        boolean shadersEnabled = false;
        if(Platform.isModLoaded("iris")){
            shadersEnabled = IrisApi.getInstance().getConfig().areShadersEnabled();
        }
        if(shadersEnabled && mirageWorld.newlyRefreshedBuffers || mirageWorld.overideRefreshBuffer){
            mirageWorld.initVertexBuffers(projectorPos);
            mirageWorld.newlyRefreshedBuffers = false;
            mirageWorld.overideRefreshBuffer = false;
        }
        if(!shadersEnabled){
            mirageWorld.newlyRefreshedBuffers = true;
        }
    }
    public static boolean shouldRenderModelData(BlockEntity blockEntity){
        return false;
    }
    public static void renderMirageModelData(BlockState state, BlockPos referencePos, BlockRenderView world, boolean cull, Random random, BlockEntity blockEntity, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider){

    }

}
