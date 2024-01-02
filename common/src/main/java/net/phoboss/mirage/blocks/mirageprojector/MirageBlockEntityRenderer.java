package net.phoboss.mirage.blocks.mirageprojector;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MirageBlockEntityRenderer implements BlockEntityRenderer<MirageBlockEntity> {
    public MirageBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    public int ticks=0;

    public static BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
    @Override
    public void render(MirageBlockEntity blockEntity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World beWorld = blockEntity.getWorld();

        BlockPos refPos = blockEntity.getPos().add(0,-1,0);
        BlockState fakeBlockState = beWorld.getBlockState(refPos);
        Block fakeBlock = fakeBlockState.getBlock();
        BlockEntity fakeBlockEntity = beWorld.getBlockEntity(refPos);
        if(fakeBlockEntity == null) {
            return;
        }
        BlockPos p = new BlockPos(0,5,0);
        BlockPos currentBlockPos = blockEntity.getPos().add(p);

        matrices.push();
        matrices.translate(p.getX(),p.getY(),p.getZ());
        RenderLayer rl = RenderLayers.getBlockLayer(fakeBlockState);
        //MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(fakeBlockState,matrices,vertexConsumers,
                //beWorld.getLightLevel(currentBlockPos),OverlayTexture.DEFAULT_UV);
        MinecraftClient.getInstance().getBlockRenderManager().renderBlock(fakeBlockState,currentBlockPos,beWorld,matrices,
                vertexConsumers.getBuffer(rl),true,beWorld.getRandom());

        MinecraftClient.getInstance().getBlockEntityRenderDispatcher().render(fakeBlockEntity,tickDelta,matrices,vertexConsumers);

        matrices.pop();
    }

    public boolean rendersOutsideBoundingBox(MirageBlockEntity mirageBlockEntity) {
        return true;
    }
    public boolean isInRenderDistance(MirageBlockEntity mirageBlockEntity, Vec3d vec3d) {
        return Vec3d.ofCenter(mirageBlockEntity.getPos()).multiply(1.0, 0.0, 1.0).isInRange(vec3d.multiply(1.0, 0.0, 1.0), (double)this.getRenderDistance());
    }


    @Override
    public int getRenderDistance() {
        return 256;
    }
}
