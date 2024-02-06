package net.phoboss.mirage.items.mirageprojector.forge;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.phoboss.mirage.items.mirageprojector.MirageBlockItem;
import net.phoboss.mirage.items.mirageprojector.MirageBlockItemModel;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class MirageBlockItemRenderer extends GeoItemRenderer<MirageBlockItem> {
    public MirageBlockItemRenderer() {
        super(new MirageBlockItemModel());
    }

    @Override
    public RenderLayer getRenderType(MirageBlockItem animatable, float partialTick, MatrixStack poseStack, @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight, Identifier texture) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }
}
