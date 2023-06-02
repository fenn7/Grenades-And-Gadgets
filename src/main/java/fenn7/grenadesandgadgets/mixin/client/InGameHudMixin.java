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
    private static final String LOCATION = "textures/misc/";
    @Shadow protected abstract void renderOverlay(Identifier texture, float opacity);
    @Shadow @Final private MinecraftClient client;

    private static final String RADIANT_LIGHT = "radiant_light_overlay.png";

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getLastFrameDuration()F"))
    private void grenadesandgadgets$injectOverlayRender(CallbackInfo ci) {
        if (this.client.player.hasStatusEffect(GrenadesModStatus.RADIANT_LIGHT)) {
            float remainingTicks = this.client.player.getStatusEffect(GrenadesModStatus.RADIANT_LIGHT).getDuration();
            this.renderOverlay(new Identifier(GrenadesMod.MOD_ID, LOCATION + RADIANT_LIGHT),
                 remainingTicks > 20 ? 1F : remainingTicks / 20F);
        }
    }
}
