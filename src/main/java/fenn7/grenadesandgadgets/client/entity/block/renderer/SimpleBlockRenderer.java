package fenn7.grenadesandgadgets.client.entity.block.renderer;

import fenn7.grenadesandgadgets.client.entity.block.model.SimpleBlockModel;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class SimpleBlockRenderer<T extends BlockEntity & IAnimatable> extends GeoBlockRenderer<T> {
    public SimpleBlockRenderer(BlockEntityRendererFactory.Context ctx, String blockName) {
        super(new SimpleBlockModel<>(blockName));
    }

    @Override
    public RenderLayer getRenderType(T animatable, float partialTick, MatrixStack poseStack, @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight, Identifier texture) {
        var u = this.getTextureLocation(animatable);
        return RenderLayer.getEntityTranslucent(texture);
    }
}
