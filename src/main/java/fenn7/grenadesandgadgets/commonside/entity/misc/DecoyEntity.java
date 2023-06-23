package fenn7.grenadesandgadgets.commonside.entity.misc;

import fenn7.grenadesandgadgets.client.network.GrenadesModS2CPackets;
import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Arm;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class DecoyEntity extends LivingEntity implements IAnimatable {
    public static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(DecoyEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final int MAX_LIFETIME = 600;
    private static final int INFLATION_TICKS = 20;
    private static final float BASE_HEALTH = 20.0F;
    private static final ParticleEffect TAUNT_EFFECT = ParticleTypes.ELECTRIC_SPARK;
    private static final ParticleEffect ENRAGE_EFFECT = ParticleTypes.ANGRY_VILLAGER;
    private final AnimationFactory factory = new AnimationFactory(this);
    private final DefaultedList<ItemStack> heldItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private final DefaultedList<ItemStack> armorItems = DefaultedList.ofSize(4, ItemStack.EMPTY);
    private PlayerEntity owner;
    private float range;

    public DecoyEntity(EntityType<DecoyEntity> entityType, World world) {
        super(entityType, world);
    }

    public DecoyEntity(World world, PlayerEntity owner, float range) {
        super(GrenadesModEntities.DECOY_ENTITY, world);
        this.owner = owner;
        this.range = range;
        this.setCustomName(this.owner.getDisplayName());
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return createLivingAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, BASE_HEALTH)
            .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    public void tick() {
        switch (this.age) {
            case 1 -> this.syncOwnerToClient();
            case INFLATION_TICKS -> this.enrageEntities();
        }
        /*if (this.age == 1 && !this.world.isClient) {
            this.syncOwnerToClient();
        }
        if (this.age == INFLATION_TICKS) {
            GrenadesModUtil.getBlocksInSphereAroundPos(this.getBlockPos(), this.range).stream()
                .filter(pos -> !GrenadesModUtil.areAnyBlocksBetween(this.world, this.getBlockPos(), pos))
                .forEach(pos -> this.world.getNonSpectatingEntities(MobEntity.class, new Box(pos)).forEach(entity -> {
                    entity.setTarget(this);
                    ((ServerWorld) this.world).spawnParticles(TAUNT_EFFECT, this.getX(), this.getBodyY(1) + 0.5D, this.getZ(), 1,0, 0.05, 0, 1);
                    ((ServerWorld) this.world).spawnParticles(ENRAGE_EFFECT, entity.getX(), entity.getBodyY(1) + 0.5D, entity.getZ(), 1,0, 0.05, 0, 1);
                }))
            ;
        }*/
        if (this.age >= MAX_LIFETIME) {
            this.world.sendEntityStatus(this, (byte) 60);
            this.remove(RemovalReason.DISCARDED);
        }
        super.tick();
    }

    private void syncOwnerToClient() {
        if (!this.world.isClient) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(this.getId());
            buf.writeString("player_owner");
            NbtCompound subNbt = new NbtCompound();
            subNbt.putInt("id", owner.getId());
            buf.writeNbt(subNbt);
            ServerPlayNetworking.send((ServerPlayerEntity) this.world.getPlayers().get(0), GrenadesModS2CPackets.SYNC_NBT_S2C, buf);
        }
    }

    private void enrageEntities() {
        GrenadesModUtil.getBlocksInSphereAroundPos(this.getBlockPos(), this.range).stream()
            .filter(pos -> !GrenadesModUtil.areAnyBlocksBetween(this.world, this.getBlockPos(), pos))
            .forEach(pos -> this.world.getNonSpectatingEntities(MobEntity.class, new Box(pos)).forEach(entity -> {
                entity.setTarget(this);
                ((ServerWorld) this.world).spawnParticles(TAUNT_EFFECT, this.getX(), this.getBodyY(1) + 0.5D, this.getZ(), 1,0, 0.05, 0, 1);
                ((ServerWorld) this.world).spawnParticles(ENRAGE_EFFECT, entity.getX(), entity.getBodyY(1) + 0.5D, entity.getZ(), 1,0, 0.05, 0, 1);
            }));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putFloat("taunt_range", this.range);
        return super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.range = nbt.getFloat("taunt_range");
        super.readNbt(nbt);
    }

    @Override
    protected void pushAway(Entity entity) {}

    @Override
    public void pushAwayFrom(Entity entity) {}

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return this.armorItems;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
    }

    @Override
    public Arm getMainArm() {
        return Arm.LEFT;
    }

    public PlayerEntity getPlayerOwner() {
        return this.owner;
    }

    protected <E extends IAnimatable> PlayState inflateAnimation(AnimationEvent<E> event) {
        if (this.age <= INFLATION_TICKS) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.model.inflate", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }
        //event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.model.inflate", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "controller", 0, this::inflateAnimation));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
