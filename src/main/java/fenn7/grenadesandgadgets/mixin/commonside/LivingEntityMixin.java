package fenn7.grenadesandgadgets.mixin.commonside;

import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
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
            this.removeStatusEffect(GrenadesModStatus.FROZEN);
        }
    }
}
