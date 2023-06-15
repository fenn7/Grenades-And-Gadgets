package fenn7.grenadesandgadgets.mixin.client;

import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "doAttack", at = @At(value = "HEAD"), cancellable = true)
    private void grenadesandgadgets$injectDisableAttackIfFrozen(CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().player.hasStatusEffect(GrenadesModStatus.FROZEN)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
