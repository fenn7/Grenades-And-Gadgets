package fenn7.grenadesandgadgets.commonside.status;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.status.custom.ArmourBreakStatusEffect;
import fenn7.grenadesandgadgets.commonside.status.custom.FrozenStatusEffect;
import fenn7.grenadesandgadgets.commonside.status.custom.RadiantLightStatusEffect;
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
            -0.175D, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .addAttributeModifier(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, "201f3e9a-d201-4593-9631-e86600c8fd90",
            -0.10D, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .addAttributeModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, "201f3e9a-d201-4593-9631-e86600c8fd90",
            -0.10D, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);

    public static final StatusEffect RADIANT_LIGHT = Registry.register(Registry.STATUS_EFFECT,
            new Identifier(GrenadesMod.MOD_ID, "radiant_light"),
            new RadiantLightStatusEffect(StatusEffectCategory.NEUTRAL, 0xF8FFAD))
        .addAttributeModifier(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS, "1a720e3b-c4d7-448f-ba36-3e8b7c60276d",
            -420D, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);

    public static final StatusEffect FROZEN = Registry.register(Registry.STATUS_EFFECT,
            new Identifier(GrenadesMod.MOD_ID, "frozen"),
            new FrozenStatusEffect(StatusEffectCategory.HARMFUL, 0xF8F8F8));

    public static void registerEffects() {
        GrenadesMod.LOGGER.debug("Initialising Grenades And Gadgets Effects...");
    }
}
