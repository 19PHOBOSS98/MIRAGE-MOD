package net.phoboss.mirage.blocks.mirageprojector;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.injectables.annotations.ExpectPlatform;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockRenderView;
import net.phoboss.mirage.blocks.mirageprojector.customworld.MirageWorld;

import java.util.Random;

public class MirageBlockEntityRenderer implements BlockEntityRenderer<MirageBlockEntity> {
    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static BlockRenderManager blockRenderManager = mc.getBlockRenderManager();
    public static BlockEntityRenderDispatcher blockEntityRenderDispatcher = mc.getBlockEntityRenderDispatcher();
    public static EntityRenderDispatcher entityRenderDispatcher = mc.getEntityRenderDispatcher();

    public static RenderLayer TRANSLUCENT_RENDER_LAYER = RenderLayer.getTranslucent();
    public MirageBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }
    @Override
    public int getRenderDistance() {
        return 512;
    }

    @Override
    public boolean rendersOutsideBoundingBox(MirageBlockEntity blockEntity) {
        return true;
    }
    @Override
    public void render(MirageBlockEntity blockEntity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
            MirageWorld mirageWorld = blockEntity.getMirageWorld();
            if(mirageWorld != null) {
                BlockPos projectorPos = blockEntity.getPos();
                renderMirageWorld(mirageWorld, projectorPos, tickDelta, matrices, vertexConsumers, light, overlay);
            }
    }

    @ExpectPlatform
    public static void markAnimatedSprite(BlockState blockState,Random random){
        throw new AssertionError();
    }//WIP Embeddium compat
    @ExpectPlatform
    public static boolean isOnTranslucentRenderLayer(BlockState blockState){
        return RenderLayers.getEntityBlockLayer(blockState,true) == RenderLayer.getTranslucent();
    }
    @ExpectPlatform
    public static boolean addToManualRenderList(long blockPosKey, MirageWorld.StateNEntity stateNEntity, Long2ObjectOpenHashMap manualRenderTranslucentBlocks){
        return false;
    }
    @ExpectPlatform
    public static void refreshVertexBuffersIfNeeded(BlockPos projectorPos,MirageWorld mirageWorld){
        throw new AssertionError();
    }
    @ExpectPlatform
    public static void renderMirageModelData(BlockState state, BlockPos referencePos, BlockRenderView world, boolean cull, Random random, BlockEntity blockEntity, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider){
        throw new AssertionError();
    }

    public static void renderMirageBlockEntity(BlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers){
        blockEntityRenderDispatcher.render(blockEntity,tickDelta,matrices,vertexConsumers);
    }
    public static void renderMirageEntity(Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers){
        entityRenderDispatcher.render(entity, 0, 0, 0, entity.getYaw(), tickDelta, matrices, vertexConsumers, entityRenderDispatcher.getLight(entity, tickDelta));
    }

    public static void renderMirageBlock(BlockState state, BlockPos referencePos, BlockRenderView world, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, boolean cull, Random random){
        RenderLayer rl = RenderLayers.getEntityBlockLayer(state,true);
        blockRenderManager.renderBlock(state,referencePos,world,matrices,
                vertexConsumerProvider.getBuffer(rl),cull,random);
    }
    public static void initBlockRenderLists(MirageWorld mirageWorld) {
        mirageWorld.mirageStateNEntities.forEach((blockPosKey,stateNEntity)->{
            BlockState blockState = stateNEntity.blockState;
            BlockEntity blockEntity = stateNEntity.blockEntity;

            if(blockEntity != null) {
                if (blockEntityRenderDispatcher.get(blockEntity)!=null) {
                    mirageWorld.BERBlocksList.put(blockPosKey,new MirageWorld.BlockWEntity(blockState,blockEntity));
                }
                if (isOnTranslucentRenderLayer(blockState)) {
                    if(addToManualRenderList(blockPosKey,new MirageWorld.StateNEntity(blockState,blockEntity), mirageWorld.manualBlocksList)){//isDecoBeaconBlock
                        return;
                    }
                }
                mirageWorld.VertexBufferBlocksList.put(blockPosKey,stateNEntity);
                return;
            }

            if(blockState != null) {
                if (isOnTranslucentRenderLayer(blockState)) {
                    mirageWorld.manualBlocksList.put(blockPosKey, new MirageWorld.StateNEntity(blockState));
                    return;
                }
            }

            mirageWorld.VertexBufferBlocksList.put(blockPosKey,stateNEntity);
        });
    }
    public static void initVertexBuffers(BlockPos projectorPos, MirageWorld mirageWorld) {
        mirageWorld.mirageBufferStorage.reset();
        MatrixStack matrices = new MatrixStack();
        VertexConsumerProvider.Immediate vertexConsumers = mirageWorld.mirageBufferStorage.tempImmediate;

        mirageWorld.VertexBufferBlocksList.forEach((fakeBlockPosKey,fakeStateNEntity)->{
            BlockPos fakeBlockPos = BlockPos.fromLong(fakeBlockPosKey);
            BlockState fakeBlockState = fakeStateNEntity.blockState;
            BlockEntity fakeBlockEntity = fakeStateNEntity.blockEntity;
            Entity fakeEntity = fakeStateNEntity.entity;


            matrices.push();

            if (fakeEntity != null) {
                Vec3d entityPos = fakeEntity.getPos().subtract(new Vec3d(projectorPos.getX(),projectorPos.getY(),projectorPos.getZ()));
                matrices.translate(entityPos.getX(),entityPos.getY(),entityPos.getZ());
                renderMirageEntity(fakeEntity, 0, matrices, vertexConsumers);
            }
            matrices.pop();

            matrices.push();
            BlockPos relativePos = fakeBlockPos.subtract(projectorPos);
            matrices.translate(relativePos.getX(),relativePos.getY(),relativePos.getZ());

            //markAnimatedSprite(fakeBlockState,mirageWorld.getRandom());

            if (fakeBlockEntity != null) {
                renderMirageModelData(fakeBlockState, fakeBlockPos, mirageWorld, true, mirageWorld.getRandom(), fakeBlockEntity, matrices, vertexConsumers);
                matrices.pop();
                return;
            }

            if (fakeBlockState != null) {
                renderMirageBlock(fakeBlockState, fakeBlockPos, mirageWorld, matrices, vertexConsumers, true, mirageWorld.getRandom());
            }
            matrices.pop();
        });

        mirageWorld.mirageBufferStorage.copyBufferBuilders(mirageWorld.mirageBufferStorage.tempImmediate);
        mirageWorld.mirageBufferStorage.uploadBufferBuildersToVertexBuffers();
    }
    public void renderMirageWorld(MirageWorld mirageWorld, BlockPos projectorPos, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay){
        if(mirageWorld != null) {
            refreshVertexBuffersIfNeeded(projectorPos,mirageWorld);
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.push();
            matrixStack.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
            mirageWorld.mirageBufferStorage.mirageVertexBuffers.forEach((renderLayer,vertexBuffer)->{
                renderLayer.startDrawing();
                vertexBuffer.setShader(matrixStack.peek().getPositionMatrix(), RenderSystem.getProjectionMatrix(),RenderSystem.getShader());
                renderLayer.endDrawing();
            });
            matrixStack.pop();

            mirageWorld.manualBlocksList.forEach((key, block)->{//need to render multi-model-layered translucent blocks (i.e. slime, honey, DecoBeacons etc) manually :(
                matrices.push();
                BlockPos fakeBlockPos = BlockPos.fromLong(key);
                BlockPos relativePos = fakeBlockPos.subtract(projectorPos);
                matrices.translate(relativePos.getX(),relativePos.getY(),relativePos.getZ());
                renderMirageBlock(block.blockState, fakeBlockPos, mirageWorld, matrices, vertexConsumers, true, mirageWorld.getRandom());
                matrices.pop();
            });

            mirageWorld.BERBlocksList.forEach((key,block)->{//animated blocks (enchanting table...)
                matrices.push();
                BlockPos fakeBlockPos = BlockPos.fromLong(key);
                BlockPos relativePos = fakeBlockPos.subtract(projectorPos);
                matrices.translate(relativePos.getX(),relativePos.getY(),relativePos.getZ());
                renderMirageBlockEntity(block.blockEntity, tickDelta, matrices, vertexConsumers);
                matrices.pop();
            });
        }
    }
}
