package fenn7.grenadesandgadgets.commonside.item.recipe.custom;

import java.util.HashMap;
import java.util.HashSet;

import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.FragmentationGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.MagicGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.SmokeBallGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.recipe.GrenadesModSpecialRecipes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class MagicGrenadeRecipe extends SpecialCraftingRecipe {
    private static final Ingredient BLAZE = Ingredient.ofItems(Items.BLAZE_POWDER);
    private static final Ingredient GUNPOWDER = Ingredient.ofItems(Items.GUNPOWDER);

    public MagicGrenadeRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        boolean hasBlaze = false;
        boolean hasGunpowder = false;
        int potionCount = 0;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;
            if (BLAZE.test(stack)) {
                if (hasBlaze) return false;
                hasBlaze = true;
                continue;
            }
            if (GUNPOWDER.test(stack)) {
                if (hasGunpowder) return false;
                hasGunpowder = true;
                continue;
            }
            if (stack.getItem() instanceof PotionItem) {
                if (++potionCount > 4) return false;
                continue;
            }
            return false;
        }
        return hasBlaze && hasGunpowder && potionCount > 0;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        ItemStack output = new ItemStack(GrenadesModItems.GRENADE_MAGIC, 3);
        NbtCompound outputNbt = output.getOrCreateNbt();
        var effectMap = new HashMap<StatusEffect, Integer>();
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack currentStack = inventory.getStack(i);
            if (currentStack.isEmpty())
                continue;
            if (currentStack.getItem() instanceof PotionItem) {
                var effectList = PotionUtil.getPotionEffects(currentStack);
                // to distinguish between different effect amplifiers, change 1 to effect.getAmplifier().
                effectList.forEach(effect ->
                    effectMap.put(effect.getEffectType(), 1 + effectMap.getOrDefault(effect.getEffectType(), 0)));
            }
        }
        NbtList allEffectsNbtList = new NbtList();
        effectMap.forEach((effect, count) -> {
            var effectNbt = new NbtCompound();
            effectNbt.putInt(MagicGrenadeItem.EFFECT_TYPE, StatusEffect.getRawId(effect));
            effectNbt.putInt(MagicGrenadeItem.EFFECT_COUNT, count);
            allEffectsNbtList.add(effectNbt);
        });
        outputNbt.put(MagicGrenadeItem.EFFECTS, allEffectsNbtList);
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GrenadesModSpecialRecipes.MAGIC_GRENADE;
    }
}
