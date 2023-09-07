package fenn7.grenadesandgadgets.client.screen;

import fenn7.grenadesandgadgets.client.screen.slot.HiddenExplosiveGrenadeSlot;
import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.block.entity.HiddenExplosiveBlockEntity;
import net.minecraft.client.MinecraftClient;
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
    private static final int PIXEL_BAR_LENGTH = 60;
    private final Inventory inventory;
    private final PropertyDelegate delegate;

    public HiddenExplosiveScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, playerInv, new SimpleInventory(1), new ArrayPropertyDelegate(2));
    }

    public HiddenExplosiveScreenHandler(int syncId, PlayerInventory playerInv, Inventory inventory, PropertyDelegate delegate) {
        super(GrenadesModScreens.HIDDEN_EXPLOSIVE_SCREEN_HANDLER, syncId);
        checkSize(inventory, 1);
        this.inventory = inventory;
        inventory.onOpen(playerInv.player);
        this.delegate = delegate;

        this.addSlot(new HiddenExplosiveGrenadeSlot(this.inventory, 0, 123, 34));

        this.addPlayerInventory(playerInv);
        this.addPlayerHotbar(playerInv);
        this.addProperties(delegate);
    }

    public void setDelegateValue(int index, int value) {
        if (index >= 0 && index < this.delegate.size()) {
            this.delegate.set(index, value);
        }
    }

    public boolean isArming() {
        GrenadesMod.LOGGER.warn("HANLDER THINKS ARM FLAG IS " + this.delegate.get(1));
        return this.delegate.get(1) == 1;
    }

    public int getScaledProgress() {
        int currentArmTicks = this.delegate.get(0);
        return isArming() ? currentArmTicks * PIXEL_BAR_LENGTH / HiddenExplosiveBlockEntity.MAX_ARMING_TICKS : 0;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public boolean hasGrenade() {
        return !this.inventory.getStack(0).isEmpty();
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true, invSlot)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false, invSlot)) {
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

    private boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast, int invSlot) {
        return (!this.hasGrenade() || invSlot == 0) && this.insertItem(stack, startIndex, endIndex, fromLast);
    }

    @Override
    protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
        return super.insertItem(stack, startIndex, endIndex, fromLast);
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
