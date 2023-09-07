package fenn7.grenadesandgadgets.commonside.block.entity;

import fenn7.grenadesandgadgets.client.screen.HiddenExplosiveScreenHandler;
import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.block.GrenadesModBlockEntities;
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
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
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

public class HiddenExplosiveBlockEntity extends BlockEntity implements IAnimatable, ExtendedScreenHandlerFactory, ImplementedInventory {
    public static final int MAX_ARMING_TICKS = 40;
    private static final String TRANSLATABLE = "container.grenadesandgadgets.hidden_explosive";
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private final AnimationFactory factory = GrenadesModUtil.getAnimationFactoryFor(this);
    private int sensorRange = 1;
    private Direction launchDirection;
    private Item disguiseBlockItem;

    private final PropertyDelegate delegate;
    private int currentArmingTicks = 0;
    private int armingFlag = 0;

    public HiddenExplosiveBlockEntity(BlockPos pos, BlockState state) {
        super(GrenadesModBlockEntities.HIDDEN_EXPLOSIVE_BLOCK_ENTITY, pos, state);
        this.delegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> HiddenExplosiveBlockEntity.this.currentArmingTicks;
                    case 1 -> HiddenExplosiveBlockEntity.this.armingFlag;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> HiddenExplosiveBlockEntity.this.currentArmingTicks = value;
                    case 1 -> HiddenExplosiveBlockEntity.this.armingFlag = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    public static void tick(World world, BlockPos pos, BlockState state, HiddenExplosiveBlockEntity entity) {
        if (entity.armingFlag == 1 && !entity.getStack(0).isEmpty()) {
            if (entity.currentArmingTicks < MAX_ARMING_TICKS) {
                ++entity.currentArmingTicks;
                if (entity.currentArmingTicks >= MAX_ARMING_TICKS) {
                    GrenadesMod.LOGGER.warn("AARMED");
                }
            }
        } else {
            entity.resetArming();
        }
    }

    public PropertyDelegate getDelegate() {
        return this.delegate;
    }

    public void resetArming() {
        if (this.currentArmingTicks > 0) {
            --this.currentArmingTicks;
            if (this.currentArmingTicks <= 0) {
                this.armingFlag = 0;
            }
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
        this.currentArmingTicks = nbt.getInt("current.arming.ticks");
        this.armingFlag = nbt.getInt("arming.flag");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        if (this.disguiseBlockItem != null) {
            nbt.put(HiddenExplosiveBlockItem.DISGUISE_KEY, this.disguiseBlockItem.getDefaultStack().writeNbt(new NbtCompound()));
        }
        nbt.putInt("current.arming.ticks", this.currentArmingTicks);
        nbt.putInt("arming.flag", this.armingFlag);
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
        return GrenadesModUtil.translatableTextOf(TRANSLATABLE);
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
