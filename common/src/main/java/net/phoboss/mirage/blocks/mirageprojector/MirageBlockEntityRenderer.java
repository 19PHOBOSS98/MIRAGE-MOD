package net.phoboss.mirage.blocks.mirageprojector;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.phoboss.mirage.blocks.mirageprojector.customworld.MirageWorld;

public class MirageBlockEntityRenderer implements BlockEntityRenderer<MirageBlockEntity> {
    public MirageBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }


    public int ticks=0;

    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static BlockRenderManager blockRenderManager = mc.getBlockRenderManager();
    public static BlockEntityRenderDispatcher blockEntityRenderDispatcher = mc.getBlockEntityRenderDispatcher();

    public static EntityRenderDispatcher entityRenderDispatcher = mc.getEntityRenderDispatcher();

    public static WorldRenderer worldRenderer = mc.worldRenderer;
    public static GameRenderer gameRenderer = mc.gameRenderer;

    private static Boolean render = true;


    @Override
    public void render(MirageBlockEntity blockEntity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
            MirageWorld mirageWorld = blockEntity.getMirageWorld();
            BlockPos projectorPos = blockEntity.getPos();
            mirageWorld.render(projectorPos, tickDelta, matrices, vertexConsumers, light, overlay);
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
