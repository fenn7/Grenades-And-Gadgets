package fenn7.grenadesandgadgets.commonside.block.entity;

import java.util.UUID;

import fenn7.grenadesandgadgets.commonside.item.custom.block.DisguisedExplosiveBlockItem;
import fenn7.grenadesandgadgets.commonside.util.ImplementedInventory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractDisguisedBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    protected static final String LAST_USER = "last.user";
    protected Item disguiseBlockItem;
    protected @Nullable PlayerEntity lastUser;
    protected @Nullable UUID lastUserUUID;

    public AbstractDisguisedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public Item getDisguiseBlockItem() {
        return this.disguiseBlockItem;
    }

    protected PlayerEntity getLastUser() {
        if (this.lastUser != null && !this.lastUser.isRemoved()) {
            return this.lastUser;
        }
        if (this.lastUserUUID != null && this.world instanceof ServerWorld) {
            return this.world.getPlayerByUuid(this.lastUserUUID);
        }
        return null;
    }

    protected void setLastUser(PlayerEntity player) {
        this.lastUser = player;
        this.lastUserUUID = player.getUuid();
    }

    @Override
    public void onOpen(PlayerEntity player) {
        this.setLastUser(player);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        var item = nbt.getCompound(DisguisedExplosiveBlockItem.DISGUISE_KEY);
        if (!item.isEmpty() && this.disguiseBlockItem == null) {
            this.disguiseBlockItem = ItemStack.fromNbt(item).getItem();
        }
        if (nbt.containsUuid(LAST_USER)) {
            this.lastUserUUID = nbt.getUuid(LAST_USER);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.disguiseBlockItem != null) {
            nbt.put(DisguisedExplosiveBlockItem.DISGUISE_KEY, this.disguiseBlockItem.getDefaultStack().writeNbt(new NbtCompound()));
        }
        if (this.lastUserUUID != null) {
            nbt.putUuid(LAST_USER, this.lastUserUUID);
        }
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }
}
