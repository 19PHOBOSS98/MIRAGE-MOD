package net.phoboss.mirage.client.rendering.customworld;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;

/*
this class is fed into renderers to collect the vertices to be drawn.
 */

public class MirageImmediate implements VertexConsumerProvider {
    private Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> layerBuffers;
    public MirageImmediate() {
        layerBuffers = new Object2ObjectLinkedOpenHashMap<>();
    }
    public MirageImmediate(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> defaultBuffers) {
        layerBuffers = defaultBuffers;
    }

    @Override
    public VertexConsumer getBuffer(RenderLayer renderLayer) {
        if(!layerBuffers.containsKey(renderLayer)){
            layerBuffers.put(renderLayer, new BufferBuilder(renderLayer.getExpectedBufferSize()));
        }
        BufferBuilder bufferBuilder = layerBuffers.get(renderLayer);
        if(!bufferBuilder.isBuilding()){
            bufferBuilder.begin(renderLayer.getDrawMode(), renderLayer.getVertexFormat());
        }
        return bufferBuilder;
    }

    public Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> getLayerBuffers(){
        return layerBuffers;
    }
}
