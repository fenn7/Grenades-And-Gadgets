package fenn7.grenadesandgadgets.commonside.item.recipe.custom;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.AbstractGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.TemporalFissureGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.recipe.GrenadesModSpecialRecipes;
import fenn7.grenadesandgadgets.commonside.tags.GrenadesModTags;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class GrenadeModifierRecipe extends SpecialCraftingRecipe {
    public static final String MODIFIER_KEY = "grenade.modifier";
    public static final Map<Item, String> MODIFIER_MAP = Map.of(
        Items.HONEYCOMB,"Sticky",
        Items.SLIME_BALL, "Elastic",
        Items.REDSTONE, "Reactive",
        Items.GUNPOWDER, "Potent",
        Items.KELP, "Aquatic",
        Items.MAGMA_CREAM, "Molten",
        Items.FEATHER, "Levity",
        Items.OBSIDIAN, "Gravity",
        Items.CREEPER_HEAD, "Echoing",
        Items.DRAGON_BREATH, "Cataclysmic"
    );
    private static final Ingredient MODIFIERS = Ingredient.fromTag(GrenadesModTags.Items.GRENADE_MODIFIERS);
    private static final Predicate<ItemStack> IS_GRENADE = stack -> stack.getItem() instanceof AbstractGrenadeItem;

    public GrenadeModifierRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        boolean hasGrenade = false;
        boolean hasMod = false;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;
            if (MODIFIERS.test(stack)) {
                if (hasMod) return false;
                hasMod = true;
            } else if (IS_GRENADE.test(stack)) {
                if (hasGrenade || stack.getOrCreateNbt().contains(MODIFIER_KEY)) return false;
                hasGrenade = true;
            }
            if (hasGrenade && hasMod) break;
        }
        return hasGrenade && hasMod;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        int grenadeSlot = -1;
        int modifierSlot = -1;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (IS_GRENADE.test(stack)) {
                grenadeSlot = i;
            } else if (MODIFIERS.test(stack)) {
                modifierSlot = i;
            }
            if (grenadeSlot >= 0 && modifierSlot >= 0)
                break;
        }
        ItemStack output = grenadeSlot >= 0 ? inventory.getStack(grenadeSlot).copy().split(1) : ItemStack.EMPTY;
        if (modifierSlot >= 0) {
            output.getOrCreateNbt().putString(MODIFIER_KEY, MODIFIER_MAP.getOrDefault(inventory.getStack(modifierSlot).getItem(), ""));
        }
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GrenadesModSpecialRecipes.GRENADE_MODIFER;
    }
}
