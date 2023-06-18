package fenn7.grenadesandgadgets.mixin.client;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow protected abstract void renderOverlay(Identifier texture, float opacity);
    @Shadow @Final private MinecraftClient client;

    private static final String MISC_TEXTURES = "textures/misc/";
    private static final String RADIANT_LIGHT = "radiant_light_overlay.png";
    private static final String FROZEN = "textures/block/frosted_ice_";
    private static final int FROZEN_TICK_INTERVAL = 10;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getLastFrameDuration()F"))
    private void grenadesandgadgets$injectScreenOverlayRenders(CallbackInfo ci) {
        if (this.client.player.hasStatusEffect(GrenadesModStatus.RADIANT_LIGHT)) {
            float radiantTicks = this.client.player.getStatusEffect(GrenadesModStatus.RADIANT_LIGHT).getDuration();
            this.renderOverlay(new Identifier(GrenadesMod.MOD_ID, MISC_TEXTURES + RADIANT_LIGHT),
                 radiantTicks > 20 ? 1F : radiantTicks / 20F);
        }
        if (this.client.player.hasStatusEffect(GrenadesModStatus.FROZEN)) {
            int frozenTicks = this.client.player.getStatusEffect(GrenadesModStatus.FROZEN).getDuration();
            int proportionalFrozenTicks = frozenTicks / FROZEN_TICK_INTERVAL;
            this.renderOverlay(new Identifier(FROZEN + (proportionalFrozenTicks >= 4 ? 0 : Math.min((4 - proportionalFrozenTicks), 3)) + ".png"),
                0.5F);
        }
    }
}
