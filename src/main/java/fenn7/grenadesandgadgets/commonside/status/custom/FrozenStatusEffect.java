package fenn7.grenadesandgadgets.commonside.status.custom;

import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.mob.MobEntity;

public class FrozenStatusEffect extends StatusEffect {
    /**
     * Effect handling for players can be found in ClientPlayerEntityMixin.
     * If not applied using GrenadesModUtil, visual effects may not work.
     **/
    public FrozenStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof MobEntity mob) {
            mob.setAiDisabled(true);
        }
        super.applyUpdateEffect(entity, amplifier);
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        if (entity.hasStatusEffect(GrenadesModStatus.FROZEN)) {
            return;
        }
        super.onApplied(entity, attributes, amplifier);
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);
        if (entity instanceof MobEntity mob) {
            mob.setAiDisabled(false);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
