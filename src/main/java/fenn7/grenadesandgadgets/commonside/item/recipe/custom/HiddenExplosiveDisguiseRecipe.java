package fenn7.grenadesandgadgets.commonside.item.recipe.custom;

import java.util.function.Predicate;

import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.item.custom.block.HiddenExplosiveBlockItem;
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
import net.minecraft.world.World;

public class HiddenExplosiveDisguiseRecipe extends SpecialCraftingRecipe {
    private static final Ingredient EXPLOSIVE = Ingredient.ofItems(GrenadesModItems.HIDDEN_EXPLOSIVE_BLOCK);
    private static final Predicate<Item> IS_BLOCK = item -> item instanceof BlockItem && !item.equals(GrenadesModItems.HIDDEN_EXPLOSIVE_BLOCK);

    public HiddenExplosiveDisguiseRecipe(Identifier id) {
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
            if (IS_BLOCK.test(stack.getItem())) {
                if (hasBlockItem) return false;
                hasBlockItem = true;
                continue;
            }
        }
        return hasExplosive && hasBlockItem;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        ItemStack output = new ItemStack(GrenadesModItems.HIDDEN_EXPLOSIVE_BLOCK, 1);
        NbtCompound nbt = output.getOrCreateNbt();
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (IS_BLOCK.test(stack.getItem())) {
                nbt.putString(HiddenExplosiveBlockItem.DISGUISE_KEY, stack.getItem().getTranslationKey());
                var itemNBT = output.getOrCreateSubNbt("BlockEntityTag");
                itemNBT.put(HiddenExplosiveBlockItem.DISGUISE_KEY, stack.writeNbt(new NbtCompound()));
                break;
            }
        }
        return output;
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
