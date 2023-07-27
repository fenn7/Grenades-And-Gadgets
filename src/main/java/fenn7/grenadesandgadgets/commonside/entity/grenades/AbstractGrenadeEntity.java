package fenn7.grenadesandgadgets.commonside.entity.grenades;

import static fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe.GRAVITY;
import static fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe.MOLTEN;
import static fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe.REACTIVE;
import static fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe.STICKY;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import fenn7.grenadesandgadgets.client.GrenadesModClientUtil;
import fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe;
import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModEntityData;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public abstract class AbstractGrenadeEntity extends ThrownItemEntity implements IAnimatable {
    private static final String STICK_TARGET = "sticky.target";
    protected static final byte STATUS_BYTE = (byte) 3;
    protected final AnimationFactory factory = GrenadesModUtil.getAnimationFactoryFor(this);
    protected int maxAgeTicks = 100;
    protected boolean shouldBounce = true;
    protected float bounceMultiplier = 2.0F / 3;
    protected float power;
    protected ParticleEffect explosionEffect;
    protected GrenadesModSoundProfile explosionSoundProfile;

    public AbstractGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
        this.initialise();
    }

    public AbstractGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world, LivingEntity owner) {
        super(entityType, owner, world);
        this.initialise();
    }

    public AbstractGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world, double x, double y, double z) {
        super(entityType, x, y, z, world);
        this.initialise();
    }

    public void tick() {
        if (this.age == 0) {
            world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.HOSTILE,
                0.7F, 1.5F, true);
        }
        if (this.age >= this.maxAgeTicks && !(this instanceof AbstractLingeringGrenadeEntity lingering
            && lingering.state != AbstractLingeringGrenadeEntity.LingeringState.UNEXPLODED)) {
            explodeWithEffects(this.power);
        }
        switch (this.getModifierName()) {
            case STICKY -> this.updateStickyPosition();
            case MOLTEN -> this.tickMolten();
        }
        super.tick();
    }

    private void tickMolten() {
        if (!this.isInvisible()) {
            this.setOnFireFor(1);
        }
        if (this.isSubmergedInWater()) {
            this.explodeWithEffects(this.power);
        }
    }

    private void updateStickyPosition() {
        NbtCompound thisNbt = ((GrenadesModEntityData) this).getPersistentData();
        if (thisNbt.contains(STICK_TARGET)) {
            this.setVelocity(Vec3d.ZERO);
            this.setNoGravity(true);
            this.noClip = true;
            this.setInvulnerable(true);
            NbtElement positionNbt = thisNbt.get(STICK_TARGET);
            if (positionNbt instanceof NbtList positionXYZ && positionXYZ.size() == 3) {
                this.setPos(positionXYZ.getDouble(0), positionXYZ.getDouble(1), positionXYZ.getDouble(2));
            } else if (positionNbt instanceof NbtInt entityID) {
                Entity entity = this.world.getEntityById(entityID.intValue());
                if (entity != null && entity.isAlive()) {
                    this.setPos(entity.getX(), entity.getBodyY(0.5D), entity.getZ());
                } else {
                    this.explodeWithEffects(this.power);
                }
            }
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (this.shouldBounce) {
            String hitSide = blockHitResult.getSide().asString();
            Vec3d velocity = this.getVelocity().multiply(this.bounceMultiplier);
            switch (hitSide) {
                case "up", "down" -> this.setVelocity(velocity.getX(), -velocity.getY(), velocity.getZ());
                case "east", "west" -> this.setVelocity(-velocity.getX(), velocity.getY(), velocity.getZ());
                case "north", "south" -> this.setVelocity(velocity.getX(), velocity.getY(), -velocity.getZ());
            }
        } else {
            switch (this.getModifierName()) {
                case REACTIVE -> this.explodeWithEffects(this.power);
                case STICKY -> {
                    NbtCompound nbt = ((GrenadesModEntityData) this).getPersistentData();
                    if (!nbt.contains(STICK_TARGET)) {
                        NbtList positionXYZ = new NbtList();
                        positionXYZ.addAll(List.of(NbtDouble.of(this.getX()), NbtDouble.of(this.getY()), NbtDouble.of(this.getZ())));
                        nbt.put(STICK_TARGET, positionXYZ);
                    }
                }
            }
        }
        super.onBlockHit(blockHitResult);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity target = entityHitResult.getEntity();
        target.damage(DamageSource.thrownProjectile(this, this.getOwner()), 2.0F);
        switch (this.getModifierName()) {
            case STICKY -> {
                NbtCompound nbt = ((GrenadesModEntityData) this).getPersistentData();
                if (!nbt.contains(STICK_TARGET)) {
                    nbt.putInt(STICK_TARGET, target.getId());
                }
            }
            case MOLTEN -> target.setOnFireFor(5);
            case GRAVITY -> {
                if (target instanceof LivingEntity alive) {
                    alive.addStatusEffect(new StatusEffectInstance(GrenadesModStatus.DECELERATE, 20, 4));
                }
            }
            default -> this.explodeWithEffects(this.power);
        }
        super.onEntityHit(entityHitResult);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("current_age", this.age);
        nbt.putInt("max_age", this.maxAgeTicks);
        nbt.putBoolean("should_bounce", this.shouldBounce);
        nbt.putFloat("explosion_power", this.power);
        nbt.putFloat("bounce_multiplier", this.bounceMultiplier);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.age = nbt.getInt("current_age");
        this.maxAgeTicks = nbt.getInt("max_age");
        this.shouldBounce = nbt.getBoolean("should_bounce");
        this.power = nbt.getFloat("explosion_power");
        this.bounceMultiplier = nbt.getFloat("bounce_multiplier");
        super.readNbt(nbt);
    }

    protected void explodeWithEffects(float power) {
        this.world.sendEntityStatus(this, STATUS_BYTE);
        this.explode(power);
    }

    protected abstract void initialise();

    protected abstract void explode(float power);

    protected Set<BlockPos> getAffectedBlocksAtRange(float power) {
        BlockPos thisPos = this.getBlockPos();
        return GrenadesModUtil.getBlocksInSphereAroundPos(thisPos, power).stream()
            .filter(pos -> !GrenadesModUtil.areAnyBlocksBetween(this.world, thisPos, pos))
            .collect(Collectors.toSet());
    }

    protected Set<LivingEntity> getLivingEntitiesFromBlocks(Set<BlockPos> blocks) {
        Set<LivingEntity> entities = new HashSet<>();
        blocks.forEach(pos -> entities.addAll(this.world.getNonSpectatingEntities(LivingEntity.class, new Box(pos))));
        return entities;
    }

    protected Set<Entity> getEntitiesFromBlocks(Set<BlockPos> blocks) {
        Set<Entity> entities = new HashSet<>();
        blocks.forEach(pos -> entities.addAll(this.world.getNonSpectatingEntities(Entity.class, new Box(pos))));
        return entities;
    }

    public String getModifierName() {
        return this.getItem().getOrCreateNbt().getString(GrenadeModifierRecipe.MODIFIER_KEY);
    }

    public void setMaxAgeTicks(int maxAgeTicks) {
        this.maxAgeTicks = maxAgeTicks;
    }

    public void setShouldBounce(boolean shouldBounce) {
        this.shouldBounce = shouldBounce;
    }

    public void setBounceMultiplier(float bounceMultiplier) {
        this.bounceMultiplier = bounceMultiplier;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public float getPower() {
        return this.power;
    }

    public float getBounceMultiplier() {
        return this.bounceMultiplier;
    }

    public int getMaxAgeTicks() {
        return this.maxAgeTicks;
    }

    public boolean getShouldBounce() {
        return this.shouldBounce;
    }

    public void setExplosionEffect(ParticleEffect effect) {
        this.explosionEffect = effect;
    }

    public void setExplosionSoundProfile(GrenadesModSoundProfile sound) {
        this.explosionSoundProfile = sound;
    }

    @Override
    public void handleStatus(byte status) {
        if (status == STATUS_BYTE) {
            if (this.explosionEffect != null) {
                GrenadesModClientUtil.createExplosionEffects(this.world, this.explosionEffect, this.getPos(), 3, this.power);
            }
            if (this.explosionSoundProfile != null) {
                GrenadesModClientUtil.playExplosionSound(this.world, this.explosionSoundProfile, this.getPos());
            }
        }
        super.handleStatus(status);
    }

    protected float proportionalDistanceTo(Entity entity) {
        return this.distanceTo(entity) / this.power;
    }

    protected double blockDistanceTo(BlockPos position) {
        return Math.sqrt(this.getBlockPos().getSquaredDistance(position));
    }

    public ItemStack getGrenadeItemStack() {
        return this.getItem();
    }

    // animations
    protected <E extends IAnimatable> PlayState flyingAnimation(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.grenade.flying", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "controller", 0, this::flyingAnimation));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
