package fenn7.grenadesandgadgets.mixin.commonside;

import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private static final float FROZEN_BASE_MODIFIER = 1.5F;
    private static final float MISS_BASE_CHANCE = 0.30F;
    private static final float MISS_BONUS_CHANCE = 0.04F;
    private static final float CAUSTIC_HEAL_REDUCTION = 0.15F;
    private static final float MAX_CAUSTIC_HEAL_REDUCTION = 1.5F;

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);
    @Shadow public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);

    @Shadow public abstract boolean removeStatusEffect(StatusEffect type);

    @Shadow public abstract float getHealth();

    @Shadow public abstract void kill();

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyVariable(method = "applyDamage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float grenadesandgadgets$modifyDamageOnThaw(float amount) {
        return this.hasStatusEffect(GrenadesModStatus.FROZEN)
            ? (amount * FROZEN_BASE_MODIFIER) + (this.getStatusEffect(GrenadesModStatus.FROZEN).getAmplifier() / 10F) : amount;
    }

    @Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setAbsorptionAmount(F)V", ordinal = 0))
    private void grenadesandgadgets$injectThawIfAttacked(CallbackInfo ci) {
        if (this.hasStatusEffect(GrenadesModStatus.FROZEN)) {
            GrenadesModUtil.removeEffectServerAndClient((LivingEntity) (Object) this, GrenadesModStatus.FROZEN);
            this.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.ICE.getDefaultState()),
                this.getX(), this.getBodyY(0.5), this.getZ(), 0, 0, 0);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void grenadesandgadgets$injectMissAttackChance(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() instanceof LivingEntity alive) {
            boolean hasRadiantLight = alive.hasStatusEffect(GrenadesModStatus.RADIANT_LIGHT);
            boolean hasBlindness = alive.hasStatusEffect(StatusEffects.BLINDNESS);
            if (hasRadiantLight || hasBlindness) {
                int radiantLightLevel = hasRadiantLight ? alive.getStatusEffect(GrenadesModStatus.RADIANT_LIGHT).getAmplifier() : 0;
                int blindLevel = hasBlindness ? alive.getStatusEffect(StatusEffects.BLINDNESS).getAmplifier() : 0;
                if (this.random.nextFloat() <= MISS_BASE_CHANCE + (Math.max(radiantLightLevel, blindLevel) * MISS_BONUS_CHANCE)) {
                    cir.setReturnValue(false);
                    cir.cancel();
                }
            }
        }
    }

    @ModifyVariable(method = "heal", at = @At("HEAD"), argsOnly = true)
    private float grenadesandgadgets$modifyHealOnCaustic(float amount) {
        return this.hasStatusEffect(GrenadesModStatus.CAUSTIC)
            ? amount - (Math.min(MAX_CAUSTIC_HEAL_REDUCTION, this.getStatusEffect(GrenadesModStatus.CAUSTIC).getAmplifier() * CAUSTIC_HEAL_REDUCTION) * amount)
            : amount;
    }

    @Inject(method = "heal", at = @At("TAIL"))
    private void grenadesandgadgets$killOnHealthUnderflow(float amount, CallbackInfo ci) {
        if (this.getHealth() <= 0.0F) {
            this.remove(RemovalReason.KILLED);
        }
    }
}
