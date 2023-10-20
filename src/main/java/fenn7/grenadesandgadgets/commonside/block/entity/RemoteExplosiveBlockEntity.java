package fenn7.grenadesandgadgets.commonside.block.entity;

import fenn7.grenadesandgadgets.client.screen.RemoteExplosiveScreenHandler;
import fenn7.grenadesandgadgets.commonside.block.GrenadesModBlockEntities;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import fenn7.grenadesandgadgets.commonside.util.ImplementedInventory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class RemoteExplosiveBlockEntity extends AbstractDisguisedBlockEntity implements IAnimatable, ExtendedScreenHandlerFactory, ImplementedInventory {
    private final AnimationFactory factory = GrenadesModUtil.getAnimationFactoryFor(this);
    public static final int MAX_DELAY_TICKS = 1200;
    private static final String NBT_TAG = "configuration.data";
    private static final String TITLE = "container.grenadesandgadgets.remote_explosive";
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    private final PropertyDelegate delegate;
    private int delayTicks = 0;
    private int armingFlag = 0;

    public RemoteExplosiveBlockEntity(BlockPos pos, BlockState state) {
        super(GrenadesModBlockEntities.REMOTE_EXPLOSIVE_BLOCK_ENTITY, pos, state);
        this.delegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> RemoteExplosiveBlockEntity.this.delayTicks;
                    case 1 -> RemoteExplosiveBlockEntity.this.armingFlag;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> RemoteExplosiveBlockEntity.this.delayTicks = value;
                    case 1 -> RemoteExplosiveBlockEntity.this.armingFlag = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
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
        nbt.putIntArray(NBT_TAG, new int[]{this.delayTicks, this.armingFlag});
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
