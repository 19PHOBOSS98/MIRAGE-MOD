package net.phoboss.mirage.blocks.mirageprojector.customworld;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

public class MirageBufferStorage {
    public Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> mirageEntityLayerBuffers;
    public Object2ObjectLinkedOpenHashMap<RenderLayer, VertexBuffer> mirageVertexBuffers = new Object2ObjectLinkedOpenHashMap();

    private final static List<RenderLayer> renderLayerList = getRenderLayerList();
    public VertexConsumerProvider.Immediate tempImmediate;
    public MirageBufferStorage() {
        reset();
    }
    public static List<RenderLayer> getRenderLayerList(){
        List<RenderLayer> layers = new ArrayList<>();
        layers.add(TexturedRenderLayers.getEntitySolid());
        layers.add(TexturedRenderLayers.getEntityCutout());
        layers.add(TexturedRenderLayers.getBannerPatterns());
        layers.add(TexturedRenderLayers.getEntityTranslucentCull());

        layers.add(TexturedRenderLayers.getShieldPatterns());
        layers.add(TexturedRenderLayers.getBeds());
        layers.add(TexturedRenderLayers.getShulkerBoxes());
        layers.add(TexturedRenderLayers.getSign());
        layers.add(TexturedRenderLayers.getChest());
        layers.add(RenderLayer.getTranslucentNoCrumbling());
        layers.add(RenderLayer.getArmorGlint());
        layers.add(RenderLayer.getArmorEntityGlint());
        layers.add(RenderLayer.getGlint());
        layers.add(RenderLayer.getDirectGlint());
        layers.add(RenderLayer.getGlintTranslucent());
        layers.add(RenderLayer.getEntityGlint());
        layers.add(RenderLayer.getDirectEntityGlint());
        layers.add(RenderLayer.getWaterMask());
        for (RenderLayer renderLayer : RenderLayer.getBlockLayers()) {
            layers.add(renderLayer);
        }
        return layers;
    }
    public SortedMap<RenderLayer, BufferBuilder> setBufferBuilders(){
        return (SortedMap) Util.make(new Object2ObjectLinkedOpenHashMap(), (map) -> {
            this.renderLayerList.forEach((renderLayer -> {
                assignBufferBuilder(map, renderLayer);
            }));
        });
    }
    private static void assignBufferBuilder(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> builderStorage, RenderLayer layer) {
        builderStorage.put(layer, new BufferBuilder(layer.getExpectedBufferSize()));
    }



    public void copyBufferBuilders(VertexConsumerProvider.Immediate immediate){
        for(RenderLayer renderLayer: this.renderLayerList){
            this.mirageEntityLayerBuffers.put(renderLayer,(BufferBuilder) immediate.getBuffer(renderLayer));
        }
    }

    public void sortTranslucentBlockBufferLayer(Vec3d playerCamera, BlockPos projectorPos){
        RenderLayer translucentLayer = RenderLayer.getTranslucent();
        BufferBuilder bufferBuilder = this.mirageEntityLayerBuffers.get(translucentLayer);
        if(!bufferBuilder.isBuilding()){
            bufferBuilder.begin(translucentLayer.getDrawMode(),translucentLayer.getVertexFormat());
        }
        ChunkSectionPos projectorChunkSection =  ChunkSectionPos.from(projectorPos);
        BlockPos projectorChunkOrigin = projectorChunkSection.getMinPos();
        bufferBuilder.sortFrom((float)playerCamera.x - (float) projectorChunkOrigin.getX(),
                               (float)playerCamera.y - (float) projectorChunkOrigin.getY(),
                               (float)playerCamera.z - (float) projectorChunkOrigin.getZ());

    }
    public void uploadBufferBuilderToVertexBuffer(RenderLayer renderLayer) {
        if(!this.mirageVertexBuffers.containsKey(renderLayer)){
            this.mirageVertexBuffers.put(renderLayer, new VertexBuffer());
        }
        this.mirageVertexBuffers.get(renderLayer).upload(this.mirageEntityLayerBuffers.get(renderLayer));
    }
    public void uploadBufferBuildersToVertexBuffers() {
        this.mirageEntityLayerBuffers.forEach((renderLayer, bufferBuilder)->{
            if(bufferBuilder.isBuilding()){
                bufferBuilder.end();
                if(!this.mirageVertexBuffers.containsKey(renderLayer)){
                    this.mirageVertexBuffers.put(renderLayer, new VertexBuffer());
                }

                this.mirageVertexBuffers.get(renderLayer).upload(bufferBuilder);

            }
        });
    }
    public void reset() {

        this.mirageEntityLayerBuffers = new Object2ObjectLinkedOpenHashMap();

        this.mirageVertexBuffers = new Object2ObjectLinkedOpenHashMap();

        this.tempImmediate = VertexConsumerProvider.immediate(setBufferBuilders(), new BufferBuilder(256));
    }

}
