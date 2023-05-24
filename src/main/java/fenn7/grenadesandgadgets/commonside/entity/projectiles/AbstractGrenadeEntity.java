package fenn7.grenadesandgadgets.commonside.entity.projectiles;

import java.util.concurrent.ThreadLocalRandom;

import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public abstract class AbstractGrenadeEntity extends ThrownItemEntity implements IAnimatable {
    private final static byte STATUS_BYTE = (byte) 3;
    protected final AnimationFactory factory = new AnimationFactory(this);
    protected int maxAgeTicks = 100;
    protected boolean shouldBounce = true;
    protected float power;
    protected ParticleEffect explosionEffect;
    protected SoundEvent explosionSound;
    protected float explosionSoundVolume;
    protected float explosionSoundPitch;

    public AbstractGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public AbstractGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world, LivingEntity owner) {
        super(entityType, owner, world);
    }

    public AbstractGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world, double x, double y, double z) {
        super(entityType, x, y, z, world);
    }

    public void tick() {
        if (this.age == 1) {
            world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.HOSTILE,
                0.3F, 1.5F, true);
        }
        if (this.age >= this.maxAgeTicks) {
            explode(this.power);
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
            this.world.sendEntityStatus(this, (byte) STATUS_BYTE);
            explode(this.power);
        }
        super.onBlockHit(blockHitResult);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        this.world.sendEntityStatus(this, (byte) 3);
        entityHitResult.getEntity().damage(DamageSource.thrownProjectile(this, this.getOwner()), 1.0F);
        explode(this.power);
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

    public void setExplosionSound(SoundEvent sound, float volume, float pitch) {
        this.explosionSound = sound;
        this.explosionSoundVolume = volume;
        this.explosionSoundPitch = pitch;
    }

    @Override
    public void handleStatus(byte status) {
        if (status == STATUS_BYTE) {
            if (this.explosionEffect != null) {
                GrenadesModUtil.createExplosionEffects(this.world, this.explosionEffect, this.getPos(), 3, this.power);
            }
            if (this.explosionSound != null) {
                GrenadesModUtil.playExplosionSound(this.world, this.explosionSound, this.getPos(), this.explosionSoundVolume, this.explosionSoundPitch);
            }
        } else {
            super.handleStatus(status);
        }
    }

    // animations
    protected <E extends IAnimatable> PlayState flyingAnimation(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.grenade.flying", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "controller", 0, this::flyingAnimation));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
