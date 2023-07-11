package fenn7.grenadesandgadgets.commonside.item.recipe.custom;

import java.util.Arrays;
import java.util.List;

import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
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

public class TemporalFissureGrenadeRecipe extends SpecialCraftingRecipe {
    private static final Ingredient DIMENSIONAL_ESSENCES = Ingredient.fromTag(GrenadesModTags.Items.DIMENSIONAL_ESSENCES);
    private static final Ingredient LAPIS = Ingredient.ofItems(Items.LAPIS_LAZULI);
    private static final Ingredient DIAMOND = Ingredient.ofItems(Items.DIAMOND);
    private static final Ingredient GUNPOWDER = Ingredient.ofItems(Items.GUNPOWDER);
    private static final Ingredient STICK = Ingredient.ofItems(Items.STICK);
    private static final List<Ingredient> INGREDIENTS = List.of(LAPIS, DIAMOND, LAPIS, GUNPOWDER, DIMENSIONAL_ESSENCES, GUNPOWDER, DIAMOND, STICK, DIAMOND);
    private static final int DIMENSION_SLOT = 4;
    private static final int DIMENSION_MISS = -1337;

    public TemporalFissureGrenadeRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty())
                return false;
            if (!INGREDIENTS.get(i).test(stack))
                return false;
        }
        return true;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        ItemStack output = new ItemStack(GrenadesModItems.GRENADE_TEMPORAL_FISSURE, 2);
        NbtCompound outputNbt = output.getOrCreateNbt();
        int dimensionKey = this.checkStackAgainstDimension(inventory.getStack(DIMENSION_SLOT));
        if (dimensionKey != DIMENSION_MISS) {
            outputNbt.putInt(TemporalFissureGrenadeItem.NBT_DIMENSION_KEY, dimensionKey);
        }
        return output;
    }

    private int checkStackAgainstDimension(ItemStack stack) {
        List<ItemStack> dimensionStacks = Arrays.asList(DIMENSIONAL_ESSENCES.getMatchingStacks());
        for (int i = 0; i < dimensionStacks.size(); ++i) {
            if (dimensionStacks.get(i).isItemEqualIgnoreDamage(stack)) {
                return (i - 1);
            }
        }
        return DIMENSION_MISS;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 9;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GrenadesModSpecialRecipes.TEMPORAL_FISSURE_GRENADE;
    }
}
