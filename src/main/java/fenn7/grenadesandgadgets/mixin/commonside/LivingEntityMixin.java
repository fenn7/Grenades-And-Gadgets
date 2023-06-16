package fenn7.grenadesandgadgets.mixin.commonside;

import fenn7.grenadesandgadgets.client.network.GrenadesModS2CPackets;
import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private static final float FROZEN_BASE_MODIFIER = 2F;

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);
    @Shadow public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);
    @Shadow public abstract boolean removeStatusEffect(StatusEffect type);

    @Shadow public abstract @Nullable LivingEntity getAttacker();

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyVariable(method = "applyDamage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float grenadesandgadgets$modifyDamageOnThaw(float amount) {
        return this.hasStatusEffect(GrenadesModStatus.FROZEN)
            ? (amount * FROZEN_BASE_MODIFIER) + (this.getStatusEffect(GrenadesModStatus.FROZEN).getAmplifier() / 10F) : amount;
    }

    @Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setAbsorptionAmount(F)V"))
    private void grenadesandgadgets$injectThawIfAttacked(CallbackInfo ci) {
        if (this.hasStatusEffect(GrenadesModStatus.FROZEN)) {
            if (!this.world.isClient) {
                try {
                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                    buf.writeInt(this.getId());
                    ServerPlayerEntity pete = this.getAttacker() instanceof ServerPlayerEntity player ? player
                        : (ServerPlayerEntity) this.world.getPlayers().get(0);
                    ServerPlayNetworking.send(pete, GrenadesModS2CPackets.FROZEN_NBT_SYNC, buf);
                } catch (UnsupportedOperationException ignored) {}
            }
            this.removeStatusEffect(GrenadesModStatus.FROZEN);
        }
    }
}
