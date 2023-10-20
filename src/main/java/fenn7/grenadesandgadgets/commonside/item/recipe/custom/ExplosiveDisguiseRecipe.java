package fenn7.grenadesandgadgets.commonside.item.recipe.custom;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.item.custom.block.DisguisedExplosiveBlockItem;
import fenn7.grenadesandgadgets.commonside.item.recipe.GrenadesModSpecialRecipes;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class ExplosiveDisguiseRecipe extends SpecialCraftingRecipe {
    private static final Ingredient EXPLOSIVE = Ingredient.ofItems(GrenadesModItems.HIDDEN_EXPLOSIVE_BLOCK, GrenadesModItems.REMOTE_EXPLOSIVE_BLOCK);
    private static final Predicate<ItemStack> IS_BLOCK = stack -> stack.getItem() instanceof BlockItem && !EXPLOSIVE.test(stack);

    public ExplosiveDisguiseRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        boolean hasExplosive = false;
        boolean hasBlockItem = false;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;
            if (EXPLOSIVE.test(stack)) {
                if (hasExplosive) return false;
                hasExplosive = true;
                continue;
            }
            if (IS_BLOCK.test(stack)) {
                if (hasBlockItem) return false;
                hasBlockItem = true;
            }
        }
        return hasExplosive && hasBlockItem;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        ItemStack output = ItemStack.EMPTY;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (EXPLOSIVE.test(stack)) {
                output = stack.copy();
                break;
            }
        }
        NbtCompound nbt = output.getOrCreateNbt();
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (IS_BLOCK.test(stack)) {
                nbt.putString(DisguisedExplosiveBlockItem.DISGUISE_KEY, stack.getItem().getTranslationKey());
                var itemNBT = output.getOrCreateSubNbt("BlockEntityTag");
                itemNBT.put(DisguisedExplosiveBlockItem.DISGUISE_KEY, new ItemStack(stack.getItem()).writeNbt(new NbtCompound()));
                break;
            }
        }
        return output;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(CraftingInventory inventory) {
        DefaultedList<ItemStack> remainderList = super.getRemainder(inventory);
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            NbtCompound nbt = stack.getOrCreateNbt();
            if (stack.getItem() instanceof DisguisedExplosiveBlockItem && nbt.contains("BlockEntityTag")) {
                ItemStack oldDisguise = ItemStack.fromNbt(nbt.getCompound("BlockEntityTag").getCompound(DisguisedExplosiveBlockItem.DISGUISE_KEY));
                remainderList.set(i, oldDisguise);
            }
        }
        return remainderList;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GrenadesModSpecialRecipes.HIDDEN_EXPLOSIVE_DISGUISE;
    }
}
