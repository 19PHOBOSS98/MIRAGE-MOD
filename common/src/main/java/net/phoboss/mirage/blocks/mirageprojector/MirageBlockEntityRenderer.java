package net.phoboss.mirage.blocks.mirageprojector;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockRenderView;
import net.phoboss.mirage.blocks.mirageprojector.customworld.MirageWorld;

import java.util.Random;

public class MirageBlockEntityRenderer implements BlockEntityRenderer<MirageBlockEntity> {
    public MirageBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @ExpectPlatform
    public static MirageBlockEntityRenderer createPlatformSpecific(BlockEntityRendererFactory.Context ctx) {
        throw new AssertionError();
    }

    public int ticks=0;

    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static BlockRenderManager blockRenderManager = mc.getBlockRenderManager();
    public static BlockEntityRenderDispatcher blockEntityRenderDispatcher = mc.getBlockEntityRenderDispatcher();

    public static EntityRenderDispatcher entityRenderDispatcher = mc.getEntityRenderDispatcher();

    public static WorldRenderer worldRenderer = mc.worldRenderer;
    public static GameRenderer gameRenderer = mc.gameRenderer;

    private static Vec3i offset = new Vec3i(-2,-3,3);
    @Override
    public void render(MirageBlockEntity blockEntity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        MirageWorld mirageWorld = blockEntity.getMirageWorld();
        BlockPos projectorPos = blockEntity.getPos();
        mirageWorld.render(projectorPos, tickDelta, matrices, vertexConsumers, light, overlay);


        //I tried looking at the WorldRenderer class but the render method has too much overhead, I decided to render entities and blocks here instead
/*

        MirageWorld mirageWorld = blockEntity.getMirageWorld();
        BlockPos projectorPos = blockEntity.getPos();

        Map<Long, MirageWorld.StateNEntity> fakeStructure = mirageWorld.getMirageStateNEntities();
        fakeStructure.forEach((fakeBlockPosKey,fakeStateNEntity)->{
            BlockPos fakeBlockPos = BlockPos.fromLong(fakeBlockPosKey);
            matrices.push();
            BlockPos relativePos = fakeBlockPos.subtract(projectorPos);
            matrices.translate(relativePos.getX(),relativePos.getY(),relativePos.getZ());

            BlockState fakeBlockState = fakeStateNEntity.blockState;
            BlockEntity fakeBlockEntity = fakeStateNEntity.blockEntity;
            Entity fakeEntity = fakeStateNEntity.entity;
            if (fakeBlockState != null) {
                renderFakeBlock(fakeBlockState, fakeBlockPos, mirageWorld, matrices, vertexConsumers, true, mirageWorld.getRandom(), fakeBlockEntity);
            }
            if (fakeBlockEntity != null) {
                renderFakeBlockEntity(fakeBlockEntity, tickDelta, matrices, vertexConsumers);
            }

            matrices.pop();
            matrices.push();
            if (fakeEntity != null) {
                renderFakeEntity(fakeEntity, gameRenderer.getCamera(), tickDelta, mirageWorld, matrices, vertexConsumers);
            }
            */
/*FluidState fakeFluidState = fakeBlockState.getFluidState();
            if (!fakeFluidState.isEmpty()) {
                renderFakeFluid(fakeBlockPos, mirageWorld, vertexConsumers, fakeBlockState, fakeFluidState);
            }*//*


            matrices.pop();
        });
*/

    }

    @ExpectPlatform
    public void renderFakeBlock(BlockState state, BlockPos referencePos, BlockRenderView world, MatrixStack matrices, VertexConsumerProvider vertexConsumers, boolean cull, Random random, BlockEntity blockEntity){
        throw new AssertionError();
    }

    public void renderFakeBlockEntity(BlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers){
        MinecraftClient.getInstance().getBlockEntityRenderDispatcher().render(blockEntity,tickDelta,matrices,vertexConsumers);
    }


    public void renderFakeEntity(Entity entity, Camera camera, float tickDelta, BlockRenderView world, MatrixStack matrices, VertexConsumerProvider vertexConsumers){
        Vec3d cameraPos = camera.getPos();
        if (entity.age == 0) {
            entity.lastRenderX = entity.getX();
            entity.lastRenderY = entity.getY();
            entity.lastRenderZ = entity.getZ();
        }
        double d0 = MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
        double d1 = MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY());
        double d2 = MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());
        float f = MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw());
        //this.entityRenderDispatcher.render(entity, d0 - cameraPos.x, d1 - cameraPos.y, d2 - cameraPos.z, f, tickDelta, matrices, vertexConsumers, this.entityRenderDispatcher.getLight(entity, tickDelta));
        //this.entityRenderDispatcher.render(entity, d0, d1, d2, f, tickDelta, matrices, vertexConsumers, this.entityRenderDispatcher.getLight(entity, tickDelta));
        this.entityRenderDispatcher.render(entity, 0, 0, 0, f, tickDelta, matrices, vertexConsumers, this.entityRenderDispatcher.getLight(entity, tickDelta));
    }

    /*@ExpectPlatform
    public void renderFakeFluid(BlockPos pos, BlockRenderView world, VertexConsumerProvider vertexConsumers, BlockState blockState, FluidState fluidState){
        RenderLayer renderLayer = RenderLayers.getFluidLayer(fluidState);
        blockRenderManager.renderFluid(pos, world, vertexConsumers.getBuffer(renderLayer),blockState,fluidState);
    }*/

    @Override
    public int getRenderDistance() {
        return 512;
    }

    @Override
    public boolean rendersOutsideBoundingBox(MirageBlockEntity blockEntity) {
        return true;
    }
}
