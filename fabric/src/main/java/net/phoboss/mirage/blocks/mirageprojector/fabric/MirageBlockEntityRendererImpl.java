package net.phoboss.mirage.blocks.mirageprojector.fabric;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.phoboss.mirage.blocks.mirageprojector.MirageBlockEntity;
import net.phoboss.mirage.blocks.mirageprojector.MirageBlockEntityRenderer;

import java.util.Random;

public class MirageBlockEntityRendererImpl extends MirageBlockEntityRenderer {
    public MirageBlockEntityRendererImpl(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    public static MirageBlockEntityRenderer createPlatformSpecific(BlockEntityRendererFactory.Context ctx) {
        return new MirageBlockEntityRendererImpl(ctx);
    }

    public void renderFakeBlock(BlockState state, BlockPos referencePos, BlockRenderView world, MatrixStack matrices, VertexConsumerProvider vertexConsumers, boolean cull, Random random){
        RenderLayer rl = RenderLayers.getEntityBlockLayer(state,true);
        blockRenderManager.renderBlock(state,referencePos,world,matrices,
                vertexConsumers.getBuffer(rl),cull,random);
    }
}
