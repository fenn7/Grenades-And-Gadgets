package fenn7.grenadesandgadgets.commonside.status.custom;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;

public class FrozenStatusEffect extends StatusEffect {
    private float pitch;
    private float bodyYaw;
    private float headYaw;

    public FrozenStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof MobEntity mob) {
            mob.setAiDisabled(true);
        } else if (entity instanceof PlayerEntity player && player.age % 20 == 0) {
            GrenadesMod.LOGGER.warn(pitch + ", " + bodyYaw + ", " + headYaw);
            GrenadesMod.LOGGER.warn(player.getPitch() + ", " + player.bodyYaw + ", " + player.headYaw);
        }
        super.applyUpdateEffect(entity, amplifier);
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);
        this.pitch = entity.getPitch(0);
        this.headYaw = entity.headYaw;
        this.bodyYaw = entity.bodyYaw;
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
