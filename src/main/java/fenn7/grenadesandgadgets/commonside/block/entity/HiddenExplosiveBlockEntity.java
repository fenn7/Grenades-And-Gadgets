package fenn7.grenadesandgadgets.commonside.block.entity;

import java.util.Optional;

import fenn7.grenadesandgadgets.client.screen.HiddenExplosiveScreenHandler;
import fenn7.grenadesandgadgets.commonside.block.GrenadesModBlockEntities;
import fenn7.grenadesandgadgets.commonside.block.custom.HiddenExplosiveBlock;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.AbstractGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.GrenadeItem;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import fenn7.grenadesandgadgets.commonside.util.ImplementedInventory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.GameEventTags;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.Vibration;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class HiddenExplosiveBlockEntity extends AbstractDisguisedBlockEntity implements IAnimatable, ExtendedScreenHandlerFactory, ImplementedInventory {
    public static final int MAX_ARMING_TICKS = 40;
    private static final float INCREASED_POWER_PER_RANGE = 0.15F;
    private static final float INCREASED_POWER_BASE = 1.5F;
    private static final String NBT_TAG = "configuration.data";
    private static final String TITLE = "container.grenadesandgadgets.hidden_explosive";
    private static final String ARMED = "container.grenadesandgadgets.armed";
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private final AnimationFactory factory = GrenadesModUtil.getAnimationFactoryFor(this);
    private final HiddenExplosiveListener listener;
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
        this.listener = new HiddenExplosiveListener(new BlockPositionSource(pos));
    }

    public static void tick(World world, BlockPos pos, BlockState state, HiddenExplosiveBlockEntity entity) {
        if (!world.isClient) {
            entity.tickArming(world, pos, state);
            if (state.get(HiddenExplosiveBlock.ARMED)) {
                entity.listener.tick(world);
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
            grenadeEntity.setMaxAgeTicks(5);
            grenadeEntity.setNoGravity(true);
            BlockPos potentialPos = pos.offset(Direction.byId(this.directionID));
            grenadeEntity.setPosition(Vec3d.ofCenter(this.directionID > 0 ? (!world.getBlockState(potentialPos).isSolidBlock(world, pos) ? potentialPos : pos) : pos));
            grenadeEntity.setPower(grenadeEntity.getPower() * (INCREASED_POWER_BASE + (0.9F - (MathHelper.clamp(this.detectRange, 1, 4) * INCREASED_POWER_PER_RANGE))));
            world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_BASS, SoundCategory.HOSTILE, 20.0F, 0.5F);
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
                    world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_PLING, SoundCategory.BLOCKS, 2.0F, 1.5F);
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

    public HiddenExplosiveListener getListener() {
        return this.listener;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, this.inventory);
        super.readNbt(nbt);
        var configData = nbt.getIntArray(NBT_TAG);
        for (int i = 0; i < configData.length; ++i) {
            this.delegate.set(i, configData[i]);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putIntArray(NBT_TAG, new int[]{this.currentArmingTicks, this.armingFlag, this.detectRange, this.directionID});
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

    @SuppressWarnings("ALL")
    private class HiddenExplosiveListener implements GameEventListener {
        protected final PositionSource positionSource;
        protected Optional<GameEvent> event = Optional.empty();
        protected int range;
        protected int distance;
        protected int delay;

        public HiddenExplosiveListener(PositionSource positionSource) {
            this.positionSource = positionSource;
        }

        public void tick(World world) {
            this.range = HiddenExplosiveBlockEntity.this.detectRange;
            if (this.event.isPresent()) {
                --this.delay;
                if (this.delay <= 0) {
                    this.delay = 0;
                    this.event = Optional.empty();
                    HiddenExplosiveBlockEntity.this.detonate(world, HiddenExplosiveBlockEntity.this.pos);
                }
            }
        }

        @Override
        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        @Override
        public int getRange() {
            return this.range;
        }

        @Override
        public boolean listen(World world, GameEvent event, @Nullable Entity entity, BlockPos pos) {
            if (!HiddenExplosiveBlockEntity.this.getCachedState().get(HiddenExplosiveBlock.ARMED) || !this.shouldActivate(event, entity)) {
                return false;
            }
            Optional<BlockPos> optional = this.positionSource.getPos(world);
            if (optional.isEmpty()) {
                return false;
            }
            BlockPos blockPos = optional.get();
            if (this.rejects(pos, event) || this.isBlocked(world, pos, blockPos)) {
                return false;
            }
            this.listen(world, event, pos, blockPos);
            return true;
        }

        private boolean shouldActivate(GameEvent event, @Nullable Entity entity) {
            if (this.event.isPresent() || !event.isIn(GameEventTags.VIBRATIONS)) {
                return false;
            }
            if (entity != null && (entity.occludeVibrationSignals() || event.isIn(GameEventTags.IGNORE_VIBRATIONS_SNEAKING) && entity.bypassesSteppingEffects())) {
                return false;
            }
            return entity == null || !entity.isSpectator();
        }

        private void listen(World world, GameEvent event, BlockPos pos, BlockPos sourcePos) {
            this.event = Optional.of(event);
            if (world instanceof ServerWorld sw) {
                this.delay = this.distance = MathHelper.floor(Math.sqrt(pos.getSquaredDistance(sourcePos)));
                sw.spawnParticles(new VibrationParticleEffect(new Vibration(pos, this.positionSource, this.delay)), pos.getX(), pos.getY(), pos.getZ(), 1,0, 0, 0, 0);
            }
        }

        private boolean isBlocked(World world, BlockPos pos, BlockPos sourcePos) {
            return world.raycast(new BlockStateRaycastContext(Vec3d.ofCenter(pos), Vec3d.ofCenter(sourcePos), s -> s.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS))).getType() == HitResult.Type.BLOCK;
        }

        public boolean rejects(BlockPos pos, GameEvent event) {
            boolean placeEvent = event == GameEvent.BLOCK_PLACE && pos.equals(HiddenExplosiveBlockEntity.this.getPos());
            boolean destroyEvent = event == GameEvent.BLOCK_DESTROY && pos.equals(HiddenExplosiveBlockEntity.this.getPos())
                && !HiddenExplosiveBlockEntity.this.getCachedState().get(HiddenExplosiveBlock.ARMED);
            return placeEvent || destroyEvent;
        }
    }
}