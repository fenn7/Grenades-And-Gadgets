package fenn7.grenadesandgadgets.commonside.item.recipe.custom;

import java.util.function.Predicate;

import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.AbstractGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.misc.ClusterRoundItem;
import fenn7.grenadesandgadgets.commonside.item.recipe.GrenadesModSpecialRecipes;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ClusterRoundRecipe extends SpecialCraftingRecipe {
    private static final Predicate<ItemStack> IS_IRON = stack -> stack.getItem().equals(Items.IRON_INGOT);
    private static final Predicate<ItemStack> IS_GRENADE = stack -> stack.getItem() instanceof AbstractGrenadeItem;

    public ClusterRoundRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        int grenadeCount = 0;
        int ironCount = 0;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;
            if (IS_GRENADE.test(stack)) {
                if (++grenadeCount > 4) return false;
                continue;
            }
            if (IS_IRON.test(stack)) {
                if (++ironCount > 4) return false;
                continue;
            }
            return false;
        }
        return ironCount == 4 && grenadeCount > 0;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        ItemStack output = new ItemStack(GrenadesModItems.CLUSTER_ROUND, 2);
        NbtCompound outputNbt = output.getOrCreateNbt();
        NbtList grenadesList = new NbtList();
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack currentStack = inventory.getStack(i);
            if (currentStack.isEmpty())
                continue;
            if (IS_GRENADE.test(currentStack)) {
                grenadesList.add(currentStack.writeNbt(new NbtCompound()));
            }
        }
        outputNbt.put(ClusterRoundItem.GRENADES_KEY, grenadesList);
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 5;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GrenadesModSpecialRecipes.CLUSTER_ROUND;
    }
}
