package fenn7.grenadesandgadgets.mixin.client;

import java.util.List;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {
    @Shadow protected M model;
    @Shadow @Final protected List<FeatureRenderer<T, M>> features;
    private static final String GRENADESMOD_MISC = "textures/misc/";

    protected LivingEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    /*@Inject(method = "getRenderLayer", at = @At(value = "RETURN"), cancellable = true)
    private void grenadesandgadgets$injectOverwriteRenderLayer(T entity, boolean showBody, boolean translucent, boolean showOutline, CallbackInfoReturnable cir) {
        if (entity.hasStatusEffect(GrenadesModStatus.FROZEN)) {
            int frozenTicks = entity.getActiveStatusEffects().get(GrenadesModStatus.FROZEN).getDuration();
            if (frozenTicks <= 0) {
                entity.removeStatusEffect(GrenadesModStatus.FROZEN);
            }
            cir.setReturnValue(this.model.getLayer(new Identifier(GrenadesMod.MOD_ID, GRENADESMOD_MISC + "frozen_mob_encasing.png")));
        }
    }*/

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;hasOutline(Lnet/minecraft/entity/Entity;)Z", shift = At.Shift.AFTER), cancellable = true)
    private void grenadesandgadgets$injectAdditionalRenderLayer(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (livingEntity.hasStatusEffect(GrenadesModStatus.FROZEN)) {
            int frozenTicks = livingEntity.getActiveStatusEffects().get(GrenadesModStatus.FROZEN).getDuration();
            if (frozenTicks <= 0) {
                livingEntity.removeStatusEffect(GrenadesModStatus.FROZEN);
            }
            Identifier frozenTexture = new Identifier(GrenadesMod.MOD_ID, GRENADESMOD_MISC + "frozen_mob_encasing.png");
            RenderLayer renderLayer = RenderLayer.getEntityTranslucent(frozenTexture); //this.model.getLayer(frozenTexture);
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
            this.model.render(matrixStack, vertexConsumer, i, 0, 1.0f, 1.0f, 1.0f,0.5f);
            if (!livingEntity.isSpectator()) {
                //matrixStack.scale(1.1F, 1.1F, 1.1F);
                for (FeatureRenderer<T, M> featureRenderer : this.features) {
                    FeatureRendererAccessorMixin.grenadesandgadgets$invokeRenderModel(featureRenderer.getContextModel(), frozenTexture, matrixStack, vertexConsumerProvider, i, livingEntity, 1.0f, 1.0f, 1.0f);
                }
            }
            matrixStack.pop();
            super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
            ci.cancel();
        }
    }
}
