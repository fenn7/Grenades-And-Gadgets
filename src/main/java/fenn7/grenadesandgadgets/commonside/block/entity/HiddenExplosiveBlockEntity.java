package fenn7.grenadesandgadgets.commonside.block.entity;

import fenn7.grenadesandgadgets.client.screen.HiddenExplosiveScreenHandler;
import fenn7.grenadesandgadgets.commonside.block.GrenadesModBlockEntities;
import fenn7.grenadesandgadgets.commonside.block.custom.HiddenExplosiveBlock;
import fenn7.grenadesandgadgets.commonside.item.custom.block.HiddenExplosiveBlockItem;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import fenn7.grenadesandgadgets.commonside.util.ImplementedInventory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class HiddenExplosiveBlockEntity extends BlockEntity implements IAnimatable, ExtendedScreenHandlerFactory, ImplementedInventory {
    public static final int MAX_ARMING_TICKS = 40;
    private static final String NBT_TAG = "configuration.data";
    private static final String TITLE = "container.grenadesandgadgets.hidden_explosive";
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private final AnimationFactory factory = GrenadesModUtil.getAnimationFactoryFor(this);
    private Item disguiseBlockItem;

    private final PropertyDelegate delegate;
    private int currentArmingTicks = 0;
    private int armingFlag = 0;
    private int detectRange = 1;
    private int directionID = -1;

    public HiddenExplosiveBlockEntity(BlockPos pos, BlockState state) {
        super(GrenadesModBlockEntities.HIDDEN_EXPLOSIVE_BLOCK_ENTITY, pos, state);
        this.delegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> HiddenExplosiveBlockEntity.this.currentArmingTicks;
                    case 1 -> HiddenExplosiveBlockEntity.this.armingFlag;
                    case 2 -> HiddenExplosiveBlockEntity.this.detectRange;
                    case 3 -> HiddenExplosiveBlockEntity.this.directionID;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> HiddenExplosiveBlockEntity.this.currentArmingTicks = value;
                    case 1 -> HiddenExplosiveBlockEntity.this.armingFlag = value;
                    case 2 -> HiddenExplosiveBlockEntity.this.detectRange = value;
                    case 3 -> HiddenExplosiveBlockEntity.this.directionID = value;
                }
            }

            @Override
            public int size() {
                return 4;
            }
        };
    }

    public static void tick(World world, BlockPos pos, BlockState state, HiddenExplosiveBlockEntity entity) {
        if (!world.isClient) {
            if (entity.armingFlag == 1 && !entity.getStack(0).isEmpty()) {
                if (entity.currentArmingTicks < MAX_ARMING_TICKS) {
                    ++entity.currentArmingTicks;
                    if (entity.currentArmingTicks >= MAX_ARMING_TICKS) {
                        world.setBlockState(pos, state.with(HiddenExplosiveBlock.ARMED, true));
                    }
                }
            } else {
                entity.resetArming(world, pos, state);
            }
        }
    }

    public PropertyDelegate getDelegate() {
        return this.delegate;
    }

    public void resetArming(World world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state.with(HiddenExplosiveBlock.ARMED, false));
        if (this.currentArmingTicks > 0) {
            --this.currentArmingTicks;
        };
    }

    public Item getDisguiseBlockItem() {
        return this.disguiseBlockItem;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, this.inventory);
        super.readNbt(nbt);
        var item = nbt.getCompound(HiddenExplosiveBlockItem.DISGUISE_KEY);
        if (!item.isEmpty() && this.disguiseBlockItem == null) {
            this.disguiseBlockItem = ItemStack.fromNbt(item).getItem();
        }
        var configData = nbt.getIntArray(NBT_TAG);
        for (int i = 0; i < configData.length; ++i) {
            this.delegate.set(i, configData[i]);
        }
        //this.currentArmingTicks = nbt.getInt("current.arming.ticks");
        //this.armingFlag = nbt.getInt("arming.flag");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        if (this.disguiseBlockItem != null) {
            nbt.put(HiddenExplosiveBlockItem.DISGUISE_KEY, this.disguiseBlockItem.getDefaultStack().writeNbt(new NbtCompound()));
        }
        //nbt.putInt("current.arming.ticks", this.currentArmingTicks);
        //nbt.putInt("arming.flag", this.armingFlag);
        nbt.putIntArray(NBT_TAG, new int[]{this.currentArmingTicks, this.armingFlag, this.detectRange, this.directionID});
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
        return this.inventory;
    }

    @Override
    public Text getDisplayName() {
        return GrenadesModUtil.translatableTextOf(TITLE);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new HiddenExplosiveScreenHandler(syncId, inv, this, this.delegate);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }
}
