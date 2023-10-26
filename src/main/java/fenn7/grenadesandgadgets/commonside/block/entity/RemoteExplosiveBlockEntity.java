package fenn7.grenadesandgadgets.commonside.block.entity;

import fenn7.grenadesandgadgets.client.screen.RemoteExplosiveScreenHandler;
import fenn7.grenadesandgadgets.commonside.block.GrenadesModBlockEntities;
import fenn7.grenadesandgadgets.commonside.block.custom.RemoteExplosiveBlock;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.AbstractGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.GrenadeItem;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import fenn7.grenadesandgadgets.commonside.util.ImplementedInventory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
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

public class RemoteExplosiveBlockEntity extends AbstractDisguisedExplosiveBlockEntity implements IAnimatable, ExtendedScreenHandlerFactory, ImplementedInventory {
    private final AnimationFactory factory = GrenadesModUtil.getAnimationFactoryFor(this);
    public static final int MAX_DELAY_TICKS = 1200;
    private static final String NBT_TAG = "configuration.data";
    private static final String TITLE = "container.grenadesandgadgets.remote_explosive";
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private int delayTicks = 0;

    public RemoteExplosiveBlockEntity(BlockPos pos, BlockState state) {
        super(GrenadesModBlockEntities.REMOTE_EXPLOSIVE_BLOCK_ENTITY, pos, state);
        this.delegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return RemoteExplosiveBlockEntity.this.delayTicks;
            }

            @Override
            public void set(int index, int value) {
                RemoteExplosiveBlockEntity.this.delayTicks = value;
            }

            @Override
            public int size() {
                return 1;
            }
        };
    }

    public static void tick(World world, BlockPos pos, BlockState state, RemoteExplosiveBlockEntity entity) {
        if (!world.isClient && state.get(RemoteExplosiveBlock.ARMED)) {
            if (!entity.inventory.get(0).isEmpty()) {
                if (entity.delayTicks > 0) {
                    --entity.delayTicks;
                } else if (entity.delayTicks == 0) {
                    entity.detonate(world, pos);
                }
            } else {
                world.setBlockState(pos, state.with(RemoteExplosiveBlock.ARMED, false));
            }
        }
    }

    public void detonate(World world, BlockPos pos) {
        ItemStack stack = this.getStack(0);
        this.removeStack(0);
        if (stack.getItem() instanceof AbstractGrenadeItem grenadeItem && this.getLastUser() != null) {
            var grenadeEntity = grenadeItem.createGrenadeAt(world, this.getLastUser(), stack);
            grenadeEntity.setItem(stack);
            GrenadeItem.addNbtModifier(stack, grenadeEntity);
            grenadeEntity.setMaxAgeTicks(0);
            grenadeEntity.setNoGravity(true);
            grenadeEntity.setPosition(Vec3d.ofCenter(pos));
            world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.HOSTILE, 20.0F, 0.5F);
            world.breakBlock(pos, false);
            world.spawnEntity(grenadeEntity);
            //world.setBlockState(pos, Blocks.AIR.getDefaultState());
        } else {
            Entity payload;
            switch (PAYLOAD_TO_ENTITY.get(stack.getItem())) {
                case "TNT" -> {
                    payload = new TntEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, this.getLastUser());
                    ((TntEntity) payload).setFuse(0);
                }
                default -> payload = null;
            };
            if (payload != null) {
                payload.setNoGravity(true);
                payload.setPosition(Vec3d.ofCenter(pos));
                world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.HOSTILE, 20.0F, 0.5F);
                world.breakBlock(pos, false);
                world.spawnEntity(payload);
                //world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, this.inventory);
        super.readNbt(nbt);
        this.delegate.set(0, nbt.getInt(NBT_TAG));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putInt(NBT_TAG, this.delayTicks);
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "controller", 0, ae -> {
            ae.getController().setAnimation(new AnimationBuilder().addAnimation("animation.remote_bomb.idle", ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }));
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
        return new RemoteExplosiveScreenHandler(syncId, inv, this, this.delegate);
    }
}