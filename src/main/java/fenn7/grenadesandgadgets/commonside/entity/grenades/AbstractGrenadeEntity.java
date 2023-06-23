package fenn7.grenadesandgadgets.commonside.entity.grenades;

import java.util.List;

import fenn7.grenadesandgadgets.client.GrenadesModClientUtil;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
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
    protected final static byte STATUS_BYTE = (byte) 3;
    protected final AnimationFactory factory = new AnimationFactory(this);
    protected int maxAgeTicks = 100;
    protected boolean shouldBounce = true;
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
            // TODO: Make nades not rely on entity status?
            explodeWithEffects(this.power);
        }
        super.tick();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (this.shouldBounce) {
            String hitSide = blockHitResult.getSide().asString();
            Vec3d velocity = this.getVelocity().multiply(0.65F);
            switch (hitSide) {
                case "up", "down" -> this.setVelocity(velocity.getX(), -velocity.getY(), velocity.getZ());
                case "east", "west" -> this.setVelocity(-velocity.getX(), velocity.getY(), velocity.getZ());
                case "north", "south" -> this.setVelocity(velocity.getX(), velocity.getY(), -velocity.getZ());
            }
        } else {
            this.world.sendEntityStatus(this, STATUS_BYTE);
            explode(this.power);
        }
        super.onBlockHit(blockHitResult);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        entityHitResult.getEntity().damage(DamageSource.thrownProjectile(this, this.getOwner()), 2.0F);
        this.explodeWithEffects(this.power);
        super.onEntityHit(entityHitResult);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("current_age", this.age);
        nbt.putFloat("explosion_power", this.power);
        nbt.putBoolean("should_bounce", this.shouldBounce);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.age = nbt.getInt("current_age");
        this.power = nbt.getFloat("explosion_power");
        this.shouldBounce = nbt.getBoolean("should_bounce");
        super.readNbt(nbt);
    }

    protected void explodeWithEffects(float power) {
        this.world.sendEntityStatus(this, STATUS_BYTE);
        this.explode(power);
    }

    protected abstract void explode(float power);

    protected abstract void initialise();

    protected List<BlockPos> getAffectedBlocksAtRange(float power) {
        BlockPos thisPos = this.getBlockPos();
        return GrenadesModUtil.getBlocksInSphereAroundPos(thisPos, power).stream()
            .filter(pos -> !GrenadesModUtil.areAnyBlocksBetween(this.world, thisPos, pos))
            .toList();
    }

    public void setMaxAgeTicks(int maxAgeTicks) {
        this.maxAgeTicks = maxAgeTicks;
    }

    public void setShouldBounce(boolean shouldBounce) {
        this.shouldBounce = shouldBounce;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public float getPower() {
        return this.power;
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
