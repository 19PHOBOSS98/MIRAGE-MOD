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

        boolean isTopPowered = blockEntity.isTopPowered();
        blockEntity.setReverse(blockEntity.areSidesPowered());
        if(blockEntity.isPowered()) {
            List<MirageWorld> mirageWorldList = blockEntity.getMirageWorlds();
            if(mirageWorldList.isEmpty()) {
                return;
            }
            MirageProjectorBook mirageProjectorBook = blockEntity.getBookSettingsPOJO();

            if(mirageProjectorBook.isAutoPlay()) {
                if(!isTopPowered) {
                    blockEntity.nextMirageWorldIndex(mirageWorldList.size());
                }
            }else{
                if(isTopPowered && !blockEntity.wasTopPowered()){
                    blockEntity.nextBookStep(mirageWorldList.size());
                }
                //blockEntity.setMirageWorldIndex(Math.abs(Math.max(0,Math.min(mirageProjectorBook.getStep(),mirageWorldList.size()-1))));
                blockEntity.setMirageWorldIndex(Math.abs(mirageProjectorBook.getStep()) % mirageWorldList.size());//better-ish clamping function for manual book step setting
            }

            int mirageWorldIndex = blockEntity.getMirageWorldIndex();

            MirageWorld mirageWorld = mirageWorldList.get(mirageWorldIndex);

            if (mirageWorld != null) {
                BlockPos projectorPos = blockEntity.getPos();
                mirageWorld.render(projectorPos, tickDelta, matrices, vertexConsumers, light, overlay);
            }
        }
        blockEntity.savePreviousTopPowerState(isTopPowered);
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
