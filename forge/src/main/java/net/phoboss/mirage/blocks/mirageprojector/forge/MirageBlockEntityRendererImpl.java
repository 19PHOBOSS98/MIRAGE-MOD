package net.phoboss.mirage.blocks.mirageprojector.forge;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelDataManager;
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

    public void renderFakeBlock(BlockState state, BlockPos referencepPos, BlockRenderView world, MatrixStack matrices, VertexConsumerProvider vertexConsumers, boolean cull, Random random){

        IModelData modelData = ModelDataManager.getModelData((World) world,referencepPos);
        if (modelData == null) {
            RenderLayer rl = RenderLayers.getEntityBlockLayer(state,false);
            blockRenderManager.renderBlock(state,referencepPos,world,matrices,
                    vertexConsumers.getBuffer(rl),cull,random);
            return;
        }

        //IModelData modelData = entity.getModelData();
        //IModelData modelData = ModelDataManager.getModelData(entity.getWorld(),entity.getPos());
        for (RenderLayer renderLayer : RenderLayer.getBlockLayers()) {
            ForgeHooksClient.setRenderType(renderLayer);
            blockRenderManager.renderBatched(state,referencepPos,world,matrices,vertexConsumers.getBuffer(renderLayer),cull,random,modelData);
        }
        ForgeHooksClient.setRenderType(null);
    }
}
