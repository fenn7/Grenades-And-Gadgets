package fenn7.grenadesandgadgets.commonside.item.recipe.custom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import net.minecraft.util.collection.DefaultedList;
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
            Item item = inventory.getStack(i).getItem();
            if (IS_BLOCK.test(item)) {
                nbt.putString(HiddenExplosiveBlockItem.DISGUISE_KEY, item.getTranslationKey());
                var itemNBT = output.getOrCreateSubNbt("BlockEntityTag");
                itemNBT.put(HiddenExplosiveBlockItem.DISGUISE_KEY, new ItemStack(item).writeNbt(new NbtCompound()));
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
            if (stack.getItem() instanceof HiddenExplosiveBlockItem && nbt.contains("BlockEntityTag")) {
                ItemStack oldDisguise = ItemStack.fromNbt(nbt.getCompound("BlockEntityTag").getCompound(HiddenExplosiveBlockItem.DISGUISE_KEY));
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
