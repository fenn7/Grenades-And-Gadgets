package fenn7.grenadesandgadgets.client.item.renderer;

import java.util.Collections;

import com.mojang.blaze3d.systems.RenderSystem;
import fenn7.grenadesandgadgets.client.item.model.TemporalFissureGrenadeItemModel;
import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.TemporalFissureGrenadeItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import software.bernie.geckolib3.util.EModelRenderCycle;

public class TemporalFissureGrenadeItemRenderer extends GeoItemRenderer<TemporalFissureGrenadeItem> {
    private static final String TEXTURE_LOCATION = "textures/item/grenade3d/grenade_temporal_";

    public TemporalFissureGrenadeItemRenderer() {
        super(new TemporalFissureGrenadeItemModel());
    }

    @Override
    public void render(TemporalFissureGrenadeItem animatable, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight, ItemStack stack) {
        this.currentItemStack = stack;
        GeoModel model = this.modelProvider.getModel(this.modelProvider.getModelLocation(animatable));
        AnimationEvent<TemporalFissureGrenadeItem> animationEvent = new AnimationEvent<>(
            animatable, 0.0F, 0.0F, MinecraftClient.getInstance().getTickDelta(), false, Collections.singletonList(stack)
        );
        this.dispatchedMat = poseStack.peek().getPositionMatrix().copy();
        this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
        this.modelProvider.setLivingAnimations(animatable, this.getInstanceId(animatable), animationEvent);
        poseStack.push();
        poseStack.translate(0.5, 0.51F, 0.5);
        RenderSystem.setShaderTexture(0, this.getTextureLocation(stack));
        Color renderColor = this.getRenderColor(animatable, 0.0F, poseStack, bufferSource, null, packedLight);
        RenderLayer renderType = this.getRenderType(animatable, 0.0F, poseStack, bufferSource, null, packedLight, this.getTextureLocation(stack));
        this.render(model, animatable, 0.0F, renderType, poseStack, bufferSource, null, packedLight, OverlayTexture.DEFAULT_UV,
            (float)renderColor.getRed() / 255.0F, (float)renderColor.getGreen() / 255.0F, (float)renderColor.getBlue() / 255.0F, (float)renderColor.getAlpha() / 255.0F);
        poseStack.pop();
    }

    private Identifier getTextureLocation(ItemStack stack) {
        return switch (stack.getOrCreateNbt().getInt(TemporalFissureGrenadeItem.NBT_DIMENSION_KEY)) {
            case -1 -> new Identifier(GrenadesMod.MOD_ID, TEXTURE_LOCATION + "nether.png");
            case 1 -> new Identifier(GrenadesMod.MOD_ID, TEXTURE_LOCATION + "end.png");
            default -> new Identifier(GrenadesMod.MOD_ID, TEXTURE_LOCATION + "overworld.png");
        };
    }
}
