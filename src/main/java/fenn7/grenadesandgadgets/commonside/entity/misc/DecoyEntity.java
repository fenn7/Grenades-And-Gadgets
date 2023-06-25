package fenn7.grenadesandgadgets.commonside.entity.misc;

import fenn7.grenadesandgadgets.client.network.GrenadesModS2CPackets;
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
import net.minecraft.entity.damage.DamageSource;
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
    public static final String PLAYER_OWNER = "player_owner";
    public static final String NBT_ID = "id";
    private static final int MAX_LIFETIME = 600;
    private static final int ADDITIONAL_LIFETIME_PER_RANGE = 40;
    private static final int INFLATION_TICKS = 20;
    private static final float BASE_HEALTH = 20.0F;
    private static final ParticleEffect TAUNT_EFFECT = ParticleTypes.ELECTRIC_SPARK;
    private static final ParticleEffect ENRAGE_EFFECT = ParticleTypes.ANGRY_VILLAGER;
    private final AnimationFactory factory = new AnimationFactory(this);
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
        this.setCustomNameVisible(true);
        this.setInvulnerable(true);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return createLivingAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, BASE_HEALTH)
            .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    public void tick() {
        if (this.age >= MAX_LIFETIME + (Math.floor(this.range) * ADDITIONAL_LIFETIME_PER_RANGE)) {
            this.world.sendEntityStatus(this, (byte) 60);
            this.remove(RemovalReason.DISCARDED);
        }
        switch (this.age) {
            case 1 -> this.syncOwnerToClient();
            case INFLATION_TICKS -> this.enrageEntities();
        }
        super.tick();
    }

    private void syncOwnerToClient() {
        if (!this.world.isClient && this.owner != null) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(this.getId());
            buf.writeString(PLAYER_OWNER);
            NbtCompound subNbt = new NbtCompound();
            subNbt.putInt(NBT_ID, this.owner.getId());
            buf.writeNbt(subNbt);
            ServerPlayNetworking.send((ServerPlayerEntity) this.world.getPlayers().get(0), GrenadesModS2CPackets.SYNC_NBT_S2C, buf);
        }
    }

    private void enrageEntities() {
        this.setInvulnerable(false);
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
        if (this.owner != null) {
            nbt.putInt(PLAYER_OWNER, this.owner.getId());
        }
        nbt.putFloat("taunt_range", this.range);
        return super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains(PLAYER_OWNER)) {
            this.owner = (PlayerEntity) this.world.getEntityById(nbt.getInt(PLAYER_OWNER));
        }
        this.range = nbt.getFloat("taunt_range");
        super.readNbt(nbt);
    }

    @Override
    protected void updatePostDeath() {
        this.world.sendEntityStatus(this, (byte)60);
        this.remove(Entity.RemovalReason.KILLED);
    }

    public void onDeath(DamageSource source) {
        super.onDeath(source);
    }

    @Override
    protected void pushAway(Entity entity) {}

    @Override
    public void pushAwayFrom(Entity entity) {}

    @Override
    protected float applyArmorToDamage(DamageSource source, float amount) {
        return amount;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return DefaultedList.of();
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
        if (this.age == 1) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.model.inflate", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }
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
