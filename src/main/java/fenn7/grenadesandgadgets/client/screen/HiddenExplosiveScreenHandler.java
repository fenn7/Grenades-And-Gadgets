package fenn7.grenadesandgadgets.client.screen;

import fenn7.grenadesandgadgets.client.screen.slot.HiddenExplosiveGrenadeSlot;
import fenn7.grenadesandgadgets.commonside.block.entity.HiddenExplosiveBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class HiddenExplosiveScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate delegate;

    public HiddenExplosiveScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, playerInv, new SimpleInventory(1), new ArrayPropertyDelegate(4));
    }

    public HiddenExplosiveScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate delegate) {
        super(GrenadesModScreens.HIDDEN_EXPLOSIVE_SCREEN_HANDLER, syncId);
        checkSize(inventory, 1);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
        this.delegate = delegate;

        this.addSlot(new HiddenExplosiveGrenadeSlot(this.inventory, 0, 0, 0));

        this.addPlayerInventory(playerInventory);
        this.addPlayerHotbar(playerInventory);
        this.addProperties(delegate);
    }

    public boolean isCurrentlyArming() {
        return this.delegate.get(0) > 0 && this.delegate.get(3) == 1;
    }

    public int getScaledProgress() {
        int armingTicks = this.delegate.get(0);
        int progressBarSize = 60; // This is the width in pixels of your arrow

        return this.isCurrentlyArming() ? armingTicks * progressBarSize / HiddenExplosiveBlockEntity.MAX_ARMING_TICKS : 0;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 86 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 144));
        }
    }
}
