package net.phoboss.mirage.blocks.mirageprojector;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.phoboss.mirage.client.rendering.customworld.MirageWorld;

import java.util.List;


public class MirageBlockEntityRenderer implements BlockEntityRenderer<MirageBlockEntity> {
    public MirageBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(MirageBlockEntity blockEntity, float tickDelta, MatrixStack matrices,VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if(blockEntity.isPowered()) {
            List<MirageWorld> mirageWorldList = blockEntity.getMirageWorlds();
            if(mirageWorldList.isEmpty()) {
                return;
            }

            MirageProjectorBook mirageProjectorBook = blockEntity.getBookSettingsPOJO();
            if(mirageProjectorBook.isAutoPlay()) {
                blockEntity.incrementMirageWorldIndex();
            }else{
                blockEntity.setMirageWorldIndex(mirageProjectorBook.getIndex());
            }

            int index = blockEntity.getMirageWorldIndex();

            if(mirageProjectorBook.isReverse()){
                index = mirageWorldList.size()-1 - index;
            }

            index = Math.abs(Math.max(0,Math.min(index,mirageWorldList.size()-1)));

            MirageWorld mirageWorld = mirageWorldList.get(index);
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
    public boolean rendersOutsideBoundingBox(MirageBlockEntity blockEntity) {
        return true;
    }
}
