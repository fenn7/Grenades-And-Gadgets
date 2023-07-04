package fenn7.grenadesandgadgets.commonside.damage;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.mixin.commonside.DamageSourceAccessorMixin;
import net.minecraft.entity.damage.DamageSource;

public class GrenadesModDamageSources {
    public static final DamageSource BLEED = registerDamageSource("bleed_out");

    private static DamageSource registerDamageSource(String name) {
        return DamageSourceAccessorMixin.grenadesandgadgets$invokeConstructor(name);
    }
}
