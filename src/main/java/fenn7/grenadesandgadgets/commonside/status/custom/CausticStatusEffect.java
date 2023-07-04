package fenn7.grenadesandgadgets.commonside.status.custom;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class CausticStatusEffect extends StatusEffect {
    /** Must sync this to client to access heal reduction effect. **/
    public CausticStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }
}
