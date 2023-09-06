package fenn7.grenadesandgadgets.client.screen.slot;

import fenn7.grenadesandgadgets.commonside.item.custom.grenades.AbstractGrenadeItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class HiddenExplosiveGrenadeSlot extends Slot {
    public HiddenExplosiveGrenadeSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.getItem() instanceof AbstractGrenadeItem;
    }

    @Override
    public int getMaxItemCount() {
        return 1;
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        return 1;
    }

    @Override
    public void onQuickTransfer(ItemStack newItem, ItemStack original) {
        super.onQuickTransfer(newItem, original);
    }
}
