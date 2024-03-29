package fenn7.grenadesandgadgets.commonside.item.recipe.custom;

import java.util.LinkedHashSet;

import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.SmokeBallGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.recipe.GrenadesModSpecialRecipes;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SmokeBallGrenadeRecipe extends SpecialCraftingRecipe {
    private static final Ingredient IRON = Ingredient.ofItems(Items.IRON_INGOT);
    private static final Ingredient GUNPOWDER = Ingredient.ofItems(Items.GUNPOWDER);

    public SmokeBallGrenadeRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        boolean hasIron = false;
        boolean hasGunpowder = false;
        boolean hasDye = false;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;
            if (IRON.test(stack)) {
                if (hasIron) return false;
                hasIron = true;
                continue;
            }
            if (GUNPOWDER.test(stack)) {
                if (hasGunpowder) return false;
                hasGunpowder = true;
                continue;
            }
            if (stack.getItem() instanceof DyeItem) {
                hasDye = true;
                continue;
            }
            return false;
        }
        return hasIron && hasGunpowder && hasDye;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        LinkedHashSet<Integer> dyes = new LinkedHashSet<>();
        ItemStack output = new ItemStack(GrenadesModItems.GRENADE_SMOKE_BALL, 3);
        NbtCompound nbt = output.getOrCreateSubNbt(SmokeBallGrenadeItem.SMOKE_BALL_COLOUR);
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack currentStack = inventory.getStack(i);
            if (currentStack.isEmpty() || !(currentStack.getItem() instanceof DyeItem dye))
                continue;
            dyes.add(dye.getColor().getFireworkColor());
        }
        nbt.putIntArray(SmokeBallGrenadeItem.COLOUR_SUB_TAG, dyes.stream().toList());
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GrenadesModSpecialRecipes.SMOKE_BALL;
    }
}
