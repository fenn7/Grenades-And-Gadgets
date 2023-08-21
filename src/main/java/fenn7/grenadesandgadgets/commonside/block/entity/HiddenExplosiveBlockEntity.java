package fenn7.grenadesandgadgets.commonside.block.entity;

import fenn7.grenadesandgadgets.commonside.block.GrenadesModBlockEntities;
import fenn7.grenadesandgadgets.commonside.item.custom.block.HiddenExplosiveBlockItem;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class HiddenExplosiveBlockEntity extends BlockEntity implements IAnimatable {
    private final AnimationFactory factory = GrenadesModUtil.getAnimationFactoryFor(this);
    private Item disguiseBlockItem;

    public HiddenExplosiveBlockEntity(BlockPos pos, BlockState state) {
        super(GrenadesModBlockEntities.HIDDEN_EXPLOSIVE_BLOCK_ENTITY, pos, state);
    }

    public Item getDisguiseBlockItem() {
        return this.disguiseBlockItem;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        var item = nbt.getCompound(HiddenExplosiveBlockItem.DISGUISE_KEY);
        if (!item.isEmpty() && this.disguiseBlockItem == null) {
            this.disguiseBlockItem = ItemStack.fromNbt(item).getItem();
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.disguiseBlockItem != null) {
            nbt.put(HiddenExplosiveBlockItem.DISGUISE_KEY, this.disguiseBlockItem.getDefaultStack().writeNbt(new NbtCompound()));
        }
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

    @Override
    public void registerControllers(AnimationData animationData) {
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
