package net.phoboss.mirage.client.rendering.fabric;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.phoboss.mirage.blocks.ModBlockEntities;
import net.phoboss.mirage.blocks.mirageprojector.MirageBlockEntityRenderer;
import net.phoboss.mirage.items.ModItems;
import net.phoboss.mirage.items.mirageprojector.MirageBlockItemRenderer;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;


public class ModRenderingImpl {

    public static void registerBlockEntityRenderers() {
        //BlockEntityRendererRegistry.register(ModBlockEntities.MIRAGE_BLOCK.get(), MirageBlockEntityRendererImpl::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.MIRAGE_BLOCK.get(), MirageBlockEntityRenderer::new);

        GeoItemRenderer.registerItemRenderer(ModItems.MIRAGE_BLOCK_ITEM, new MirageBlockItemRenderer());

    }

}
