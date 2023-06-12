package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fenn7.grenadesandgadgets.commonside.entity.grenades.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.MagicGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.client.item.TooltipContext;
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
    private final HashMap<StatusEffectInstance, Integer> effectOccurenceMap = new HashMap<>();

    public MagicGrenadeItem(Settings settings) {
        super(settings);
        this.defaultSpeed = 0.65F;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        NbtCompound stackNbt = stack.getOrCreateNbt();
        if (stackNbt.contains(EFFECTS) && stackNbt.get(EFFECTS) instanceof NbtList) {
            tooltip.add(GrenadesModUtil.textOf("§l" + "Effects:"));
            NbtList nbtList = stackNbt.getList(EFFECTS, 10);
            for (int i = 0; i < nbtList.size(); ++i) {
                var effectList = PotionUtil.getPotionEffects(ItemStack.fromNbt(nbtList.getCompound(i)));
                effectList.forEach(effect ->
                    this.effectOccurenceMap.put(effect, this.effectOccurenceMap.getOrDefault(effect, 0)));
            }
            this.effectOccurenceMap.forEach((effect, integer) ->
                tooltip.add(new TranslatableText(effect.getTranslationKey()).append(" x" + (1 + integer))));
        }
    }

    @Override
    protected AbstractGrenadeEntity createGrenadeAt(World world, PlayerEntity player, ItemStack stack) {
        return new MagicGrenadeEntity(world, player);
    }

    public HashMap<StatusEffectInstance, Integer> getEffectOccurenceMap() {
        return this.effectOccurenceMap;
    }
}
