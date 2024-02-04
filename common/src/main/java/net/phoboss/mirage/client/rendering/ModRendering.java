package net.phoboss.mirage.client.rendering;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import net.minecraft.client.render.RenderLayer;
import net.phoboss.mirage.blocks.ModBlockEntities;
import net.phoboss.mirage.blocks.ModBlocks;
import net.phoboss.mirage.blocks.mirageprojector.MirageBlockEntityRenderer;


public class ModRendering {

    public static void registerRenderType() {
        RenderTypeRegistry.register(RenderLayer.getTranslucent(),
                ModBlocks.MIRAGE_BLOCK.get());
                //ModBlocks.MIRAGE_BLOCK);
    }
    @ExpectPlatform
    public static void registerBlockEntityRenderers() {
        BlockEntityRendererRegistry.register(ModBlockEntities.MIRAGE_BLOCK.get(), MirageBlockEntityRenderer::new);
    }

    public static void registerAll() {
        registerRenderType();
        registerBlockEntityRenderers();
    }
}
