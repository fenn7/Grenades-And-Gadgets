package fenn7.grenadesandgadgets.commonside.status;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.status.custom.ArmourBreakStatusEffect;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GrenadesModStatus {
    public static final StatusEffect ARMOUR_BREAK = Registry.register(Registry.STATUS_EFFECT,
            new Identifier(GrenadesMod.MOD_ID, "armour_break"),
            new ArmourBreakStatusEffect(StatusEffectCategory.HARMFUL, 8401224))
        .addAttributeModifier(EntityAttributes.GENERIC_ARMOR, "6dea9060-a13b-49f2-b329-521fc0253163",
            -0.15D, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .addAttributeModifier(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, "201f3e9a-d201-4593-9631-e86600c8fd90",
            -0.10D, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .addAttributeModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, "201f3e9a-d201-4593-9631-e86600c8fd90",
            -0.10D, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);

    public static void registerEffects() {
        GrenadesMod.LOGGER.debug("Initialising Grenades And Gadgets Effects...");
    }
}
