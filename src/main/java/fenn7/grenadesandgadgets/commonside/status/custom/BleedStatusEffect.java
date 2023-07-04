package fenn7.grenadesandgadgets.commonside.status.custom;

import fenn7.grenadesandgadgets.commonside.util.GrenadesModEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class BleedStatusEffect extends StatusEffect {
    private static final float BLEED_DAMAGE_PER_AMP = 2F;
    private static final float MAX_BLEED_DAMAGE_TICK = 10F;
    private static final String BLEED_START_AGE = "bleed_start_age";

    public BleedStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        int startAge = ((GrenadesModEntityData) entity).getPersistentData().getInt(BLEED_START_AGE);
        if ((entity.age - startAge) % 20 == 0) {
            entity.damage(DamageSource.GENERIC, Math.min(MAX_BLEED_DAMAGE_TICK, BLEED_DAMAGE_PER_AMP * (1 + amplifier)));
        }
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);
        ((GrenadesModEntityData) entity).getPersistentData().putInt(BLEED_START_AGE, entity.age);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
