package net.phoboss.mirage.blocks.mirageprojector.forge;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.IModelData;
import net.phoboss.mirage.blocks.mirageprojector.MirageBlockEntityRenderer;

import java.util.Random;


public class MirageBlockEntityRendererImpl extends MirageBlockEntityRenderer {
    public MirageBlockEntityRendererImpl(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    public static MirageBlockEntityRenderer createPlatformSpecific(BlockEntityRendererFactory.Context ctx) {
        return new MirageBlockEntityRendererImpl(ctx);
    }

    public void renderFakeBlock(BlockState state, BlockPos referencePos, BlockRenderView world, MatrixStack matrices, VertexConsumerProvider vertexConsumers, boolean cull, Random random, BlockEntity blockEntity){

        if (blockEntity == null) {
            RenderLayer rl = RenderLayers.getEntityBlockLayer(state,false);
            blockRenderManager.renderBlock(state,referencePos,world,matrices,
                    vertexConsumers.getBuffer(rl),cull,random);

            return;
        }

        IModelData modelData = blockEntity.getModelData();

        for (RenderLayer renderLayer : RenderLayer.getBlockLayers()) {
            if (RenderLayers.canRenderInLayer(state, renderLayer)) {
                ForgeHooksClient.setRenderType(renderLayer);
                blockRenderManager.renderBatched(state,referencePos,world,matrices,vertexConsumers.getBuffer(renderLayer),cull,random,modelData);
            }
        }
        ForgeHooksClient.setRenderType(null);
    }

    /*public void renderFakeFluid(BlockPos pos, BlockRenderView world, VertexConsumerProvider vertexConsumers, BlockState blockState, FluidState fluidState){

        for (RenderLayer renderLayer : RenderLayer.getBlockLayers()) {
            if (RenderLayers.canRenderInLayer(fluidState, renderLayer)) {
                ForgeHooksClient.setRenderType(renderLayer);
                blockRenderManager.renderFluid(pos, world, vertexConsumers.getBuffer(renderLayer),blockState,fluidState);
            }
        }
        ForgeHooksClient.setRenderType(null);
    }*/
}
