package net.phoboss.mirage.client.rendering.forge;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.phoboss.mirage.blocks.ModBlockEntities;
import net.phoboss.mirage.blocks.mirageprojector.MirageBlockEntityRenderer;


public class ModRenderingImpl {

    public static void registerBlockEntityRenderers() {
        //BlockEntityRendererRegistry.register(ModBlockEntities.MIRAGE_BLOCK.get(), MirageBlockEntityRendererImpl::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.MIRAGE_BLOCK.get(), MirageBlockEntityRenderer::new);



    }

}
