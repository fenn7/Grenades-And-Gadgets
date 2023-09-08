package fenn7.grenadesandgadgets.commonside.block.entity;

import java.util.UUID;

import fenn7.grenadesandgadgets.client.screen.HiddenExplosiveScreenHandler;
import fenn7.grenadesandgadgets.commonside.block.GrenadesModBlockEntities;
import fenn7.grenadesandgadgets.commonside.block.custom.HiddenExplosiveBlock;
import fenn7.grenadesandgadgets.commonside.item.custom.block.HiddenExplosiveBlockItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.AbstractGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.GrenadeItem;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
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
    private static final float INCREASED_POWER_PER_RANGE = 0.25F;
    private static final String NBT_TAG = "configuration.data";
    private static final String LAST_USER = "last.user";
    private static final String TITLE = "container.grenadesandgadgets.hidden_explosive";
    private static final String ARMED = "container.grenadesandgadgets.armed";
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private final AnimationFactory factory = GrenadesModUtil.getAnimationFactoryFor(this);
    private Item disguiseBlockItem;
    private @Nullable PlayerEntity lastUser;
    private @Nullable UUID lastUserUUID;

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
            entity.tickArming(world, pos, state);
            if (state.get(HiddenExplosiveBlock.ARMED)) {

            }
        }
    }

    public void detonate(World world, BlockPos pos) {
        ItemStack stack = this.getStack(0);
        if (stack.getItem() instanceof AbstractGrenadeItem grenadeItem && this.getLastUser() != null) {
            var grenadeEntity = grenadeItem.createGrenadeAt(world, this.getLastUser(), stack);
            grenadeEntity.setItem(stack);
            GrenadeItem.addNbtModifier(stack, grenadeEntity);
            this.removeStack(0);
            grenadeEntity.setMaxAgeTicks(10);
            grenadeEntity.setNoGravity(true);
            BlockPos potentialPos = pos.offset(Direction.byId(this.directionID));
            grenadeEntity.setPosition(Vec3d.ofCenter(this.directionID > 0 ? (!world.getBlockState(potentialPos).isSolidBlock(world, pos) ? potentialPos : pos) : pos));
            grenadeEntity.setPower(grenadeEntity.getPower() * (2 - (this.detectRange * INCREASED_POWER_PER_RANGE)));
            world.spawnEntity(grenadeEntity);
            world.breakBlock(pos, false);
        }
    }

    private void tickArming(World world, BlockPos pos, BlockState state) {
        if (this.armingFlag == 1 && !this.getStack(0).isEmpty()) {
            if (this.currentArmingTicks < MAX_ARMING_TICKS) {
                ++this.currentArmingTicks;
                if (this.currentArmingTicks >= MAX_ARMING_TICKS) {
                    world.setBlockState(pos, state.with(HiddenExplosiveBlock.ARMED, true));
                    if (this.getLastUser() != null) {
                        this.getLastUser().sendMessage(GrenadesModUtil.translatableTextOf(ARMED), false);
                    }
                }
            }
        } else {
            world.setBlockState(pos, state.with(HiddenExplosiveBlock.ARMED, false));
            if (this.currentArmingTicks > 0) {
                --this.currentArmingTicks;
            }
        }
    }

    public PropertyDelegate getDelegate() {
        return this.delegate;
    }

    public Item getDisguiseBlockItem() {
        return this.disguiseBlockItem;
    }

    public int getDetectRange() {
        return this.detectRange;
    }

    private PlayerEntity getLastUser() {
        if (this.lastUser != null && !this.lastUser.isRemoved()) {
            return this.lastUser;
        }
        if (this.lastUserUUID != null && this.world instanceof ServerWorld) {
            return this.world.getPlayerByUuid(this.lastUserUUID);
        }
        return null;
    }

    private void setLastUser(PlayerEntity player) {
        this.lastUser = player;
        this.lastUserUUID = player.getUuid();
    }

    @Override
    public void onOpen(PlayerEntity player) {
        this.setLastUser(player);
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
        if (nbt.containsUuid(LAST_USER)) {
            this.lastUserUUID = nbt.getUuid(LAST_USER);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        if (this.disguiseBlockItem != null) {
            nbt.put(HiddenExplosiveBlockItem.DISGUISE_KEY, this.disguiseBlockItem.getDefaultStack().writeNbt(new NbtCompound()));
        }
        nbt.putIntArray(NBT_TAG, new int[]{this.currentArmingTicks, this.armingFlag, this.detectRange, this.directionID});
        if (this.lastUserUUID != null) {
            nbt.putUuid(LAST_USER, this.lastUserUUID);
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
