package net.phoboss.mirage.block;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.phoboss.mirage.blocks.mirageprojector.MirageBlockEntity;
import net.phoboss.mirage.blocks.mirageprojector.MirageBlockModel;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;


public class MirageBlockEntityRendererImpl extends GeoBlockRenderer<MirageBlockEntity> {
    public MirageBlockEntityRendererImpl(BlockEntityRendererFactory.Context context) {
        super(new MirageBlockModel());
    }

    @Override
    public RenderLayer getRenderType(MirageBlockEntity animatable, float partialTick, MatrixStack poseStack, @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight, Identifier texture) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }
}
