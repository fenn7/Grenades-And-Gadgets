package fenn7.grenadesandgadgets.client.screen.slot;

import fenn7.grenadesandgadgets.commonside.item.custom.grenades.AbstractGrenadeItem;
import fenn7.grenadesandgadgets.commonside.tags.GrenadesModTags;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.slot.Slot;

public class DisguisedExplosiveGrenadeSlot extends Slot {
    private static final Ingredient PAYLOAD = Ingredient.fromTag(GrenadesModTags.Items.PAYLOAD_EXPLOSIVES);

    public DisguisedExplosiveGrenadeSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.getItem() instanceof AbstractGrenadeItem || PAYLOAD.test(stack);
    }

    @Override
    public int getMaxItemCount() {
        return 1;
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        return 1;
    }
}
