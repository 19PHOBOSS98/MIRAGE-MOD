package net.phoboss.mirage.blocks.mirageprojector.customworld.fabric;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.phoboss.mirage.blocks.mirageprojector.customworld.MirageWorld;

import java.util.Random;

public class MirageWorldImpl extends MirageWorld {

    public MirageWorldImpl(MinecraftClient MC) {
        super(MC);
    }

    public static void renderMirageBlock(BlockState state, BlockPos referencePos, BlockRenderView world, MatrixStack matrices, VertexConsumerProvider vertexConsumers, boolean cull, Random random, BlockEntity blockEntity){
        RenderLayer rl = RenderLayers.getEntityBlockLayer(state,true);
        blockRenderManager.renderBlock(state,referencePos,world,matrices,
                vertexConsumers.getBuffer(rl),cull,random);

    }

    public static boolean addToManualRenderList(long blockPosKey, BlockWEntity blockWEntity, Long2ObjectOpenHashMap manualRenderTranslucentBlocks){
        /*if(blockWEntity.blockState.getBlock() instanceof DecoBeaconBlock){
            manualRenderTranslucentBlocks.put(blockPosKey, blockWEntity);
        }*/
        return false;
    }

}
