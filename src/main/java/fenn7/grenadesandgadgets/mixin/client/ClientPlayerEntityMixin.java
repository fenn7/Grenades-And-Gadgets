package fenn7.grenadesandgadgets.mixin.client;

import com.mojang.authlib.GameProfile;
import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity {
    @Shadow public Input input;

    public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "tickMovement", at = @At(value = "HEAD"), cancellable = true)
    private void grenadesandgadgets$injectCancelMovementIfFrozen(CallbackInfo ci) {
        if (this.hasStatusEffect(GrenadesModStatus.FROZEN)) {
            ci.cancel();
        }
    }
}
