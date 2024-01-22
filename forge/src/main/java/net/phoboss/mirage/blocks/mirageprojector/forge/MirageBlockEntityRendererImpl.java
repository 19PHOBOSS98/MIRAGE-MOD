package net.phoboss.mirage.blocks.mirageprojector.forge;

import dev.architectury.platform.Platform;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.IModelData;
import net.phoboss.decobeacon.blocks.decobeacon.DecoBeaconBlock;
import net.phoboss.mirage.blocks.mirageprojector.MirageBlockEntityRenderer;
import net.phoboss.mirage.blocks.mirageprojector.customworld.MirageWorld;

import java.util.List;
import java.util.Random;

public class MirageBlockEntityRendererImpl extends MirageBlockEntityRenderer {

    public MirageBlockEntityRendererImpl(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }



    public static void markAnimatedSprite(BlockState blockState,Random random){
        if(!Platform.isModLoaded("embeddium")){
            return;
        }
        if(blockState == null){
            return;
        }
        BakedModel model = blockRenderManager.getModel(blockState);
        for( Direction direction: Direction.values()){
            List<BakedQuad> list = model.getQuads(blockState, direction, random);
            list.forEach((quad)->{
                Sprite sprite = quad.getSprite();
                if(sprite != null){
                    //SpriteUtil.markSpriteActive(sprite);
                }
            });
        }
    }//WIP Embeddium compat

    public static boolean isOnTranslucentRenderLayer(BlockState blockState){
        return RenderLayers.canRenderInLayer(blockState,TRANSLUCENT_RENDER_LAYER);
    }
    public static boolean addToManualRenderList(long blockPosKey, MirageWorld.StateNEntity stateNEntity, Long2ObjectOpenHashMap manualRenderTranslucentBlocks){
        if(stateNEntity.blockState.getBlock() instanceof DecoBeaconBlock){
            manualRenderTranslucentBlocks.put(blockPosKey, stateNEntity);
            return true;
        }
        return false;
    }
    public static void refreshVertexBuffersIfNeeded(BlockPos projectorPos, MirageWorld mirageWorld){
        if(!Platform.isModLoaded("oculus")){
            return;
        }
        Boolean shadersEnabled = IrisApi.getInstance().getConfig().areShadersEnabled();
        if(shadersEnabled && mirageWorld.newlyRefreshedBuffers || mirageWorld.overideRefreshBuffer){
            MirageBlockEntityRenderer.initVertexBuffers(projectorPos,mirageWorld);
            mirageWorld.newlyRefreshedBuffers = false;
            mirageWorld.overideRefreshBuffer = false;
        }
        if(!shadersEnabled){
            mirageWorld.newlyRefreshedBuffers = true;
        }
    }
    public static void renderMirageModelData(BlockState state, BlockPos referencePos, BlockRenderView world, boolean cull, Random random, BlockEntity blockEntity, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider){
        IModelData modelData = blockEntity.getModelData();

        for (RenderLayer renderLayer : RenderLayer.getBlockLayers()) {
            if (RenderLayers.canRenderInLayer(state, renderLayer)) {
                ForgeHooksClient.setRenderType(renderLayer);
                blockRenderManager.renderBatched(state,referencePos,world,matrices,vertexConsumerProvider.getBuffer(renderLayer),cull,random,modelData);
            }
        }

        ForgeHooksClient.setRenderType(null);
    }

}
