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
        boolean areSidesPowered = blockEntity.areSidesPowered();
        if(blockEntity.isPowered()) {
            List<MirageWorld> mirageWorldList = blockEntity.getMirageWorlds();
            if(mirageWorldList.isEmpty()) {
                return;
            }
            MirageProjectorBook mirageProjectorBook = blockEntity.getBookSettingsPOJO();
            int mirageWorldIndex = blockEntity.getMirageWorldIndex();
            int bookStep = mirageProjectorBook.getStep();

            if(mirageProjectorBook.isAutoPlay()) {
                if(!areSidesPowered) {
                    blockEntity.incrementMirageWorldIndex();
                }
            }else{
                if(areSidesPowered && !blockEntity.wereSidesPowered()){
                    blockEntity.incrementBookStep();
                }
                if(bookStep != mirageWorldIndex) {
                    blockEntity.setMirageWorldIndex(bookStep);
                }
            }

            int index = mirageWorldIndex;

            if(mirageProjectorBook.isReverse()) {
                index = mirageWorldList.size()-1 - index;
            }

            index = Math.abs(Math.max(0,Math.min(index,mirageWorldList.size()-1)));

            MirageWorld mirageWorld = mirageWorldList.get(index);
            if (mirageWorld != null) {
                BlockPos projectorPos = blockEntity.getPos();
                mirageWorld.render(projectorPos, tickDelta, matrices, vertexConsumers, light, overlay);
            }
        }
        blockEntity.savePreviousSidePowerState(areSidesPowered);
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
