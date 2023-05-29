package fenn7.grenadesandgadgets.commonside.item.recipe.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.FragmentationGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.SmokeBallGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.recipe.GrenadesModSpecialRecipes;
import fenn7.grenadesandgadgets.commonside.tags.GrenadesModTags;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FragmentationGrenadeRecipe extends SpecialCraftingRecipe {
    private static final Ingredient FRAGMENTS = Ingredient.fromTag(GrenadesModTags.Items.FRAGMENT_MATERIALS);
    private static final Ingredient GUNPOWDER = Ingredient.ofItems(Items.GUNPOWDER);

    public FragmentationGrenadeRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        int fragmentCount = 0;
        int gunPowderCount = 0;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;
            if (FRAGMENTS.test(stack)) {
                if (fragmentCount > 2) return false;
                ++fragmentCount;
                continue;
            }
            if (GUNPOWDER.test(stack)) {
                if (gunPowderCount > 2) return false;
                ++gunPowderCount;
                continue;
            }
            return false;
        }
        return gunPowderCount == 2 && fragmentCount > 0;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        ItemStack output = new ItemStack(GrenadesModItems.GRENADE_FRAGMENTATION, 4);
        NbtCompound outputNbt = output.getOrCreateNbt();
        NbtList fragmentNbtList = new NbtList();
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack currentStack = inventory.getStack(i);
            if (currentStack.isEmpty())
                continue;
            if (FRAGMENTS.test(currentStack)) {
                fragmentNbtList.add(currentStack.writeNbt(new NbtCompound()));
            }
        }
        outputNbt.put(FragmentationGrenadeItem.FRAGMENTS, fragmentNbtList);
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GrenadesModSpecialRecipes.SMOKE_BALL;
    }
}
