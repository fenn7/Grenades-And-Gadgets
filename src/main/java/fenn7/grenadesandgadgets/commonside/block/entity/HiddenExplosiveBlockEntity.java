package fenn7.grenadesandgadgets.commonside.block.entity;

import java.util.ArrayList;
import java.util.List;

import fenn7.grenadesandgadgets.client.screen.HiddenExplosiveScreenHandler;
import fenn7.grenadesandgadgets.commonside.block.GrenadesModBlockEntities;
import fenn7.grenadesandgadgets.commonside.item.custom.block.HiddenExplosiveBlockItem;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import fenn7.grenadesandgadgets.commonside.util.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class HiddenExplosiveBlockEntity extends BlockEntity implements IAnimatable, NamedScreenHandlerFactory, ImplementedInventory {
    public static final int MAX_ARMING_TICKS = 40;
    private static final String DELEGATE_VALUES = "delegate.values";
    private static final String TRANSLATABLE = "container.grenadesandgadgets.hidden_explosive";
    private final DefaultedList<ItemStack> bombInv = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private final AnimationFactory factory = GrenadesModUtil.getAnimationFactoryFor(this);
    private Item disguiseBlockItem;

    protected final PropertyDelegate delegate;
    private int armingTicks = 0;
    private int sensorRange;
    private Direction launchDirection;
    private boolean shouldKeepArming = false;

    public HiddenExplosiveBlockEntity(BlockPos pos, BlockState state) {
        super(GrenadesModBlockEntities.HIDDEN_EXPLOSIVE_BLOCK_ENTITY, pos, state);
        this.delegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> HiddenExplosiveBlockEntity.this.armingTicks;
                    case 1 -> HiddenExplosiveBlockEntity.this.sensorRange;
                    case 2 -> HiddenExplosiveBlockEntity.this.launchDirection.getId();
                    case 3 -> HiddenExplosiveBlockEntity.this.shouldKeepArming ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> HiddenExplosiveBlockEntity.this.armingTicks = value;
                    case 1 -> HiddenExplosiveBlockEntity.this.sensorRange = value;
                    case 2 -> HiddenExplosiveBlockEntity.this.launchDirection = Direction.byId(value);
                    case 3 -> HiddenExplosiveBlockEntity.this.shouldKeepArming = value == 1;
                }
            }

            @Override
            public int size() {
                return 4;
            }
        };
    }

    public static void tick(World world, BlockPos pos, BlockState state, HiddenExplosiveBlockEntity entity) {
        entity.shouldKeepArming = !entity.getStack(0).isEmpty();
        if (entity.shouldKeepArming) {
            if (entity.armingTicks < MAX_ARMING_TICKS) {
                ++entity.armingTicks;
            }
        } else {
            entity.resetArmingTicks();
        }
    }

    public void resetArmingTicks() {
        this.armingTicks = 0;
    }

    public Item getDisguiseBlockItem() {
        return this.disguiseBlockItem;
    }

    private List<Integer> delegateToList() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i <= this.delegate.size(); ++i) {
            list.add(delegate.get(i));
        }
        return list;
    }

    private void setDelegateFromList(int[] intList) {
        for (int i = 0; i <= intList.length; ++i) {
            this.delegate.set(i, intList[i]);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, this.bombInv);
        nbt.putIntArray(DELEGATE_VALUES, this.delegateToList());
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
        Inventories.writeNbt(nbt, this.bombInv);
        if (nbt.getIntArray(DELEGATE_VALUES).length > 0) {
            this.setDelegateFromList(nbt.getIntArray(DELEGATE_VALUES));
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
        animationData.addAnimationController(new AnimationController<>(this, "controller", 0,
            animationEvent -> {
                animationEvent.getController().setAnimation(new AnimationBuilder().addAnimation("animation.model.idle", ILoopType.EDefaultLoopTypes.LOOP));
                return PlayState.CONTINUE;
            })
        );
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.bombInv;
    }

    @Override
    public Text getDisplayName() {
        return GrenadesModUtil.translatableTextOf(TRANSLATABLE);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new HiddenExplosiveScreenHandler(syncId, inv, this, this.delegate);
    }
}
