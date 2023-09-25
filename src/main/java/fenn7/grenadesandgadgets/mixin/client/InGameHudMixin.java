package fenn7.grenadesandgadgets.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.AbstractGrenadeItem;
import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {
    @Shadow protected abstract void renderOverlay(Identifier texture, float opacity);
    @Shadow @Final private MinecraftClient client;

    @Shadow private int scaledHeight;
    @Shadow private int scaledWidth;

    private static final String MISC_TEXTURES = "textures/misc/";
    private static final String RADIANT_LIGHT = "radiant_light_overlay.png";
    private static final String GRENADE_COUNTDOWN = "grenade_countdown.png";
    private static final String FROZEN = "textures/block/frosted_ice_";
    private static final int FROZEN_TICK_INTERVAL = 10;
    private static final int RADIANT_FALLOFF_TICKS = 15;
    private static final int GRENADE_COUNTDOWN_WIDTH = 92;
    private static final int GRENADE_COUNTDOWN_HEIGHT = 12;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getLastFrameDuration()F"))
    private void grenadesandgadgets$injectScreenOverlayRenders(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (this.client.player.hasStatusEffect(GrenadesModStatus.FROZEN)) {
            int frozenTicks = this.client.player.getStatusEffect(GrenadesModStatus.FROZEN).getDuration();
            int proportionalFrozenTicks = frozenTicks / FROZEN_TICK_INTERVAL;
            this.renderOverlay(new Identifier(FROZEN + (proportionalFrozenTicks >= 4 ? 0 : Math.min((4 - proportionalFrozenTicks), 3)) + ".png"),
                0.5F);
        }
        if (this.client.player.hasStatusEffect(GrenadesModStatus.RADIANT_LIGHT)) {
            float radiantTicks = this.client.player.getStatusEffect(GrenadesModStatus.RADIANT_LIGHT).getDuration();
            this.renderOverlay(new Identifier(GrenadesMod.MOD_ID, MISC_TEXTURES + RADIANT_LIGHT),
                 radiantTicks > RADIANT_FALLOFF_TICKS ? 1F : radiantTicks / RADIANT_FALLOFF_TICKS);
        }
        if (this.client.player.isUsingItem() && this.client.player.getActiveItem().getItem() instanceof AbstractGrenadeItem item) {
            this.renderGrenadeCountdown(matrices, this.client.player.getActiveItem(), item);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void renderGrenadeCountdown(MatrixStack matrices, ItemStack stack, AbstractGrenadeItem item) {
        int hudHeight = this.scaledHeight - 32 + 3 - (this.client.interactionManager.hasExperienceBar() ? 35 : 20);
        int hudX = (this.scaledWidth - GRENADE_COUNTDOWN_WIDTH) / 2;
        float progress = (float) this.client.player.getItemUseTime() / item.getMaxUseTime(stack);
        RenderSystem.setShaderTexture(0, new Identifier(GrenadesMod.MOD_ID, MISC_TEXTURES + GRENADE_COUNTDOWN));
        this.drawTexture(matrices, hudX, hudHeight, 0, 0, GRENADE_COUNTDOWN_WIDTH, GRENADE_COUNTDOWN_HEIGHT);
        if (progress > 0) {
            int scaledProgress = (int) Math.ceil(progress * GRENADE_COUNTDOWN_WIDTH / 2);
            this.drawTexture(matrices, hudX, hudHeight, 0, GRENADE_COUNTDOWN_HEIGHT, scaledProgress, GRENADE_COUNTDOWN_HEIGHT);
            this.drawTexture(matrices, hudX + GRENADE_COUNTDOWN_WIDTH - scaledProgress, hudHeight, GRENADE_COUNTDOWN_WIDTH - scaledProgress, GRENADE_COUNTDOWN_HEIGHT, scaledProgress, GRENADE_COUNTDOWN_HEIGHT);
        }
    }
}
