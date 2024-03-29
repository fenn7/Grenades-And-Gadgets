package fenn7.grenadesandgadgets.commonside.status;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.status.custom.ArmourBreakStatusEffect;
import fenn7.grenadesandgadgets.commonside.status.custom.BleedStatusEffect;
import fenn7.grenadesandgadgets.commonside.status.custom.CausticStatusEffect;
import fenn7.grenadesandgadgets.commonside.status.custom.DecelerateStatusEffect;
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

    public static final StatusEffect CAUSTIC = Registry.register(Registry.STATUS_EFFECT,
            new Identifier(GrenadesMod.MOD_ID, "caustic"),
            new CausticStatusEffect(StatusEffectCategory.HARMFUL, 0x00FF00));

    public static final StatusEffect BLEED = Registry.register(Registry.STATUS_EFFECT,
            new Identifier(GrenadesMod.MOD_ID, "bleed"),
            new BleedStatusEffect(StatusEffectCategory.HARMFUL, 0x990000));

    public static final StatusEffect DECELERATE = Registry.register(Registry.STATUS_EFFECT,
            new Identifier(GrenadesMod.MOD_ID, "decelerate"),
            new DecelerateStatusEffect(StatusEffectCategory.HARMFUL, 0x133769)
                .addAttributeModifier(EntityAttributes.GENERIC_FLYING_SPEED, "499a876c-c36a-4a6f-ac25-409009e37d85",
                    -0.1D, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
                .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "499a876c-c36a-4a6f-ac25-409009e37d85",
                    -0.1D, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));

    public static void registerEffects() {
        GrenadesMod.LOGGER.warn("Initialising Grenades And Gadgets Effects...");
    }
}
