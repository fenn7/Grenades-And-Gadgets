package fenn7.grenadesandgadgets.mixin.client;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import fenn7.grenadesandgadgets.commonside.status.custom.FrozenStatusEffect;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModEntityData;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Shadow protected M model;
    private static final String BASE_BLOCKS = "textures/block/";

    @Inject(method = "getRenderLayer", at = @At(value = "RETURN"), cancellable = true)
    private void grenadesandgadgets$injectGetRenderLayer(T entity, boolean showBody, boolean translucent, boolean showOutline, CallbackInfoReturnable cir) {
        if (entity.hasStatusEffect(GrenadesModStatus.FROZEN)) {
            var x = entity.getActiveStatusEffects().get(GrenadesModStatus.FROZEN).getDuration();
            if (x <= 0) {
                entity.removeStatusEffect(GrenadesModStatus.FROZEN);
            }
            cir.setReturnValue(this.model.getLayer(new Identifier(BASE_BLOCKS + "blue_ice.png")));
        }
    }

    /*@ModifyVariable(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "STORE"))
    private RenderLayer grenadesandgadgets$injectRender(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (livingEntity.hasStatusEffect(GrenadesModStatus.FROZEN)) {
            return ((Model)this.model).getLayer(identifier);
        }
    }*/
}
