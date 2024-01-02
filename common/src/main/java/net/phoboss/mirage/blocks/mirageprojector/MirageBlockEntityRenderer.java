package net.phoboss.mirage.blocks.mirageprojector;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Random;

public class MirageBlockEntityRenderer implements BlockEntityRenderer<MirageBlockEntity> {
    public MirageBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @ExpectPlatform
    public static MirageBlockEntityRenderer createPlatformSpecific(BlockEntityRendererFactory.Context ctx) {
        throw new AssertionError();
    }

    public int ticks=0;

    public static BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
    public static BlockEntityRenderDispatcher blockEntityRenderDispatcher = MinecraftClient.getInstance().getBlockEntityRenderDispatcher();

    private static Vec3i offset = new Vec3i(-2,-3,3);
    @Override
    public void render(MirageBlockEntity blockEntity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World beWorld = blockEntity.getWorld();
        BlockPos refPos = blockEntity.getPos().add(0,-1,0);

        Map<BlockPos,Block> schem = blockEntity.getScheme();
        matrices.push();
        Vec3i offset2 = new Vec3i(-2,-5,3);
        matrices.translate(offset2.getX(),offset2.getY(),offset2.getZ());
        schem.forEach(
                (bpos, block)
                -> {
                        matrices.push();
                        matrices.translate(bpos.getX(),bpos.getY(),bpos.getZ());
                        BlockState fakeBlockState = beWorld.getBlockState(refPos);


                        BlockEntity fakeBlockEntity = beWorld.getBlockEntity(refPos);

                        if (fakeBlockEntity != null) {
                            renderFakeBlockEntity(fakeBlockEntity,tickDelta,matrices,vertexConsumers);
                        }

                        renderFakeBlock(fakeBlockState,refPos,beWorld,matrices,
                                vertexConsumers,true,beWorld.getRandom());
                        matrices.pop();
                }
        );
        matrices.pop();

        /*
        World beWorld = blockEntity.getWorld();
        BlockPos refPos = blockEntity.getPos().add(0,-1,0);
        BlockState fakeBlockState = beWorld.getBlockState(refPos);

        BlockPos p = new BlockPos(0,2,5);
        BlockPos renPos = blockEntity.getPos().add(p);
        matrices.push();
        matrices.translate(p.getX(), p.getY(), p.getZ());

        BlockEntity fakeBlockEntity = beWorld.getBlockEntity(refPos);

        if (fakeBlockEntity != null) {
            renderFakeBlockEntity(fakeBlockEntity,tickDelta,matrices,vertexConsumers);
        }

        renderFakeBlock(fakeBlockState,refPos,renPos,beWorld,matrices,
                vertexConsumers,true,beWorld.getRandom());
        matrices.pop();
        */
    }

    @ExpectPlatform
    public void renderFakeBlock(BlockState state, BlockPos referencePos, BlockRenderView world, MatrixStack matrices, VertexConsumerProvider vertexConsumers, boolean cull, Random random){
        throw new AssertionError();
    }

    public void renderFakeBlockEntity(BlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers){
        MinecraftClient.getInstance().getBlockEntityRenderDispatcher().render(blockEntity,tickDelta,matrices,vertexConsumers);
    }

    @Override
    public int getRenderDistance() {
        return 512;
    }

    @Override
    public boolean rendersOutsideBoundingBox(MirageBlockEntity blockEntity) {
        return true;
    }
}
