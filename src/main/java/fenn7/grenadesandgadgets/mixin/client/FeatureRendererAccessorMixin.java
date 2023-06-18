package fenn7.grenadesandgadgets.mixin.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FeatureRenderer.class)
public interface FeatureRendererAccessorMixin<T extends Entity, M extends EntityModel<T>> {
    @Invoker("renderModel")
    static <T extends LivingEntity> void grenadesandgadgets$invokeRenderModel(EntityModel<T> model, Identifier texture, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float red, float green, float blue) {
        throw new AssertionError("If you are seeing this message, irreversible damage has occurred to your device.");
    }
}
