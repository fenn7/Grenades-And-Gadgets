package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fenn7.grenadesandgadgets.commonside.entity.grenades.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.MagicGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import it.unimi.dsi.fastutil.Hash;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MagicGrenadeItem extends AbstractGrenadeItem {
    public static final String EFFECTS = "Effects";
    public static final String EFFECT_TYPE = "effect_type";
    public static final String EFFECT_COUNT = "effect_count";

    public MagicGrenadeItem(Settings settings) {
        super(settings);
        this.defaultSpeed = 0.65F;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        NbtCompound stackNbt = stack.getOrCreateNbt();
        if (stackNbt.contains(EFFECTS) && stackNbt.get(EFFECTS) instanceof NbtList) {
            tooltip.add(GrenadesModUtil.textOf("§l" + "Effects:"));
            NbtList effectNbtList = stackNbt.getList(EFFECTS, 10);
            effectNbtList.forEach(effectNbt -> {
                if (effectNbt instanceof NbtCompound effectNbtCompound && effectNbtCompound.contains(EFFECT_TYPE) && effectNbtCompound.contains(EFFECT_COUNT)) {
                    var effectType = StatusEffect.byRawId(effectNbtCompound.getInt(EFFECT_TYPE));
                    int effectCount = effectNbtCompound.getInt(EFFECT_COUNT);
                    tooltip.add(GrenadesModUtil.translatableTextOf(effectType.getTranslationKey()).append(" x" + effectCount));
                }
            });
        }
    }

    @Override
    protected AbstractGrenadeEntity createGrenadeAt(World world, PlayerEntity player, ItemStack stack) {
        return new MagicGrenadeEntity(world, player);
    }
}
