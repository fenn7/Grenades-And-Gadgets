package fenn7.grenadesandgadgets.commonside.entity.grenades;

import static fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe.AQUATIC;
import static fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe.CATACLYSMIC;
import static fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe.ECHOING;
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
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
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
    private static final Set<String> EXPLOSION_IMMUNE_MODIFIERS = Set.of(CATACLYSMIC, STICKY);
    private static final Double MINIMUM_CATACLYSMIC_THRESHOLD = 0.2D;
    private static final float CATACLYSMIC_MULTIPLIER = 0.75F;
    private static final float ECHOING_MULTIPLIER = 1.5F;
    private static final float AQUATIC_MULTIPLIER = 1.25F;
    protected static final String STICK_TARGET = "sticky.target";
    protected static final byte STATUS_BYTE = (byte) 3;
    protected static TrackedData<Boolean> BOUNCE_FLAG = DataTracker.registerData(AbstractGrenadeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    protected static TrackedData<Float> BOUNCE_MULTIPLIER = DataTracker.registerData(AbstractGrenadeEntity.class, TrackedDataHandlerRegistry.FLOAT);
    protected static TrackedData<Integer> MAX_AGE = DataTracker.registerData(AbstractGrenadeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected final AnimationFactory factory = GrenadesModUtil.getAnimationFactoryFor(this);
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

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BOUNCE_FLAG, true);
        this.dataTracker.startTracking(BOUNCE_MULTIPLIER, 2F / 3F);
        this.dataTracker.startTracking(MAX_AGE, 100);
    }

    @Override
    public void tick() {
        if (this.age == 0) {
            world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.HOSTILE,
                0.5F, 1.5F, true);
        }
        if (this.age >= this.getMaxAgeTicks() && !(this instanceof AbstractLingeringGrenadeEntity lingering
            && lingering.state != AbstractLingeringGrenadeEntity.LingeringState.UNEXPLODED)) {
            this.explodeWithEffects(this.power);
        }
        Vec3d velocity = this.getVelocity();
        super.tick();
        switch (this.getModifierName()) {
            case STICKY -> this.updateStickyPosition();
            case MOLTEN -> this.tickMolten();
            case AQUATIC -> this.updateAquaticVelocity(velocity);
        }
    }

    private void tickMolten() {
        if (!this.isInvisible()) {
            this.setOnFireFor(1);
        }
        if (this.isSubmergedInWater()) {
            this.explodeWithEffects(this.power);
        }
    }

    private void updateAquaticVelocity(Vec3d velocity) {
        if (this.isSubmergedInWater()) {
            this.setVelocity(velocity);
        }
    }

    private void updateStickyPosition() {
        NbtCompound thisNbt = ((GrenadesModEntityData) this).getPersistentData();
        if (thisNbt.contains(STICK_TARGET)) {
            this.noClip = true;
            this.setImmobile();
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

    protected void setImmobile() {
        this.setVelocity(Vec3d.ZERO);
        this.setNoGravity(true);
        this.setInvulnerable(true);
        this.setShouldBounce(false);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (this.getShouldBounce()) {
            String hitSide = blockHitResult.getSide().asString();
            Vec3d velocity = this.getVelocity().multiply(this.getBounceMultiplier());
            switch (hitSide) {
                case "up", "down" -> this.setVelocity(velocity.getX(), -velocity.getY(), velocity.getZ());
                case "east", "west" -> this.setVelocity(-velocity.getX(), velocity.getY(), velocity.getZ());
                case "north", "south" -> this.setVelocity(velocity.getX(), velocity.getY(), -velocity.getZ());
            }
            switch (this.getModifierName()) {
                case CATACLYSMIC -> {
                    if (velocity.length() >= MINIMUM_CATACLYSMIC_THRESHOLD) {
                        var copy = this.spawnCopyAtLocation();
                        if (copy != null) {
                            copy.setPower(this.power * CATACLYSMIC_MULTIPLIER);
                            copy.setMaxAgeTicks(0);
                        }
                    }
                }
                case AQUATIC -> {
                    if (this.isSubmergedInWater()) {
                        this.setPower(this.power * AQUATIC_MULTIPLIER);
                        this.explodeWithEffects(this.power);
                    }
                }
            }
        } else {
            switch (this.getModifierName()) {
                case REACTIVE, ECHOING -> this.explodeWithEffects(this.power);
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
    public boolean isImmuneToExplosion() {
        return EXPLOSION_IMMUNE_MODIFIERS.contains(this.getModifierName());
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
            case AQUATIC -> {
                if (this.isSubmergedInWater()) {
                    this.setPower(this.power * AQUATIC_MULTIPLIER);
                    this.explodeWithEffects(this.power);
                }
            }
            case MOLTEN -> target.setOnFireFor(5);
            case GRAVITY -> {
                if (target instanceof LivingEntity alive) {
                    alive.addStatusEffect(new StatusEffectInstance(GrenadesModStatus.DECELERATE, 20, 4));
                }
            }
            case CATACLYSMIC -> {
                if (this.getVelocity().length() >= MINIMUM_CATACLYSMIC_THRESHOLD) {
                    var copy = this.spawnCopyAtLocation();
                    if (copy != null) {
                        copy.setPower(this.power * CATACLYSMIC_MULTIPLIER);
                        copy.setMaxAgeTicks(0);
                    }
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
        nbt.putFloat("explosion_power", this.power);
        nbt.putInt("max_age", this.getMaxAgeTicks());
        nbt.putBoolean("should_bounce", this.getShouldBounce());
        nbt.putFloat("bounce_multiplier", this.getBounceMultiplier());
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.age = nbt.getInt("current_age");
        this.power = nbt.getFloat("explosion_power");
        this.dataTracker.set(MAX_AGE, nbt.getInt("max_age"));
        this.dataTracker.set(BOUNCE_FLAG, nbt.getBoolean("should_bounce"));
        this.dataTracker.set(BOUNCE_MULTIPLIER, nbt.getFloat("bounce_multiplier"));
        super.readNbt(nbt);
    }

    @Override
    public void remove(RemovalReason reason) {
        if (this.getModifierName().equals(ECHOING)) {
            var clone = this.spawnCopyAtLocation();
            if (clone != null) {
                clone.setPower(this.power * ECHOING_MULTIPLIER);
                clone.setMaxAgeTicks(20);
            }
        }
        super.remove(reason);
    }

    protected void explodeWithEffects(float power) {
        this.world.sendEntityStatus(this, STATUS_BYTE);
        this.explode(power);
    }

    protected abstract void explode(float power);

    protected abstract void initialise();

    protected AbstractGrenadeEntity spawnCopyAtLocation() {
        var clone = GrenadesModUtil.copyGrenadeFrom(this, false);
        if (clone != null) {
            clone.setImmobile();
            clone.setInvisible(true);
            clone.setPosition(this.getPos());
            this.world.spawnEntity(clone);
        }
        return clone;
    }

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
        this.dataTracker.set(MAX_AGE, maxAgeTicks);
    }

    public void setShouldBounce(boolean shouldBounce) {
        this.dataTracker.set(BOUNCE_FLAG, shouldBounce);
    }

    public void setBounceMultiplier(float bounceMultiplier) {
        this.dataTracker.set(BOUNCE_MULTIPLIER, bounceMultiplier);
    }

    public void setPower(float power) {
        this.power = power;
    }

    public float getPower() {
        return this.power;
    }

    public float getBounceMultiplier() {
        return this.dataTracker.get(BOUNCE_MULTIPLIER);
    }

    public int getMaxAgeTicks() {
        return this.dataTracker.get(MAX_AGE);
    }

    public boolean getShouldBounce() {
        return this.dataTracker.get(BOUNCE_FLAG);
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
        return this.factory;
    }
}
