package net.phoboss.mirage.blocks.miragezoetrope;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.phoboss.mirage.client.rendering.customworld.MirageWorld;


public class MirageZoetropeBlockEntityRenderer implements BlockEntityRenderer<MirageZoetropeBlockEntity> {
    public MirageZoetropeBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(MirageZoetropeBlockEntity blockEntity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if(blockEntity.isPowered()) {
            MirageWorld mirageWorld = blockEntity.getMirageWorld();
            if (mirageWorld != null) {
                BlockPos projectorPos = blockEntity.getPos();
                mirageWorld.render(projectorPos, tickDelta, matrices, vertexConsumers, light, overlay);
            }
        }
    }

    @Override
    public int getRenderDistance() {
        return 512;
    }

    @Override
    public boolean rendersOutsideBoundingBox(MirageZoetropeBlockEntity blockEntity) {
        return true;
    }
}
