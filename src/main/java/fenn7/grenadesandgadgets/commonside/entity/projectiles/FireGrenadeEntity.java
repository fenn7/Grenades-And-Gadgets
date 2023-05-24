package fenn7.grenadesandgadgets.commonside.entity.projectiles;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireGrenadeEntity extends AbstractGrenadeEntity {
    private static final float FIRE_RANGE = 2.8F;
    private static final ParticleEffect FIRE_GRENADE_EFFECT = ParticleTypes.LAVA;
    private static final SoundEvent EXPLOSION_SOUND = SoundEvents.ENTITY_BLAZE_SHOOT;
    private static final float EXPLOSION_SOUND_VOLUME = 1.5F;
    public static final float EXPLOSION_SOUND_PITCH = 0.675F;

    private boolean shouldDiscard = false;
    private boolean shouldLinger = false;
    private int postExplosionTicks = 0;

    public FireGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public FireGrenadeEntity(World world, PlayerEntity user) {
        super(GrenadesModEntities.FIRE_GRENADE_ENTITY, world, user);
    }

    public FireGrenadeEntity(World world, double x, double y, double z) {
        super(GrenadesModEntities.FIRE_GRENADE_ENTITY, world, x, y, z);
    }

    @Override
    protected void initDataTracker() {
        this.setPower(FIRE_RANGE);
        this.setExplosionEffect(FIRE_GRENADE_EFFECT);
        this.setExplosionSound(EXPLOSION_SOUND, EXPLOSION_SOUND_VOLUME, EXPLOSION_SOUND_PITCH);
        super.initDataTracker();
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (!this.world.isClient()) {
            world.sendEntityStatus(this, (byte) 3);
            this.setVelocity(Vec3d.ZERO);
            this.setNoGravity(true);
            explode(this.power * 0.66F);
            this.shouldLinger = true;
        }
        super.onCollision(hitResult);
    }

    @Override
    public void tick() {
        if (this.shouldLinger) {
            this.postExplosionTicks++;
        }
        if (this.postExplosionTicks >= 10) {
            this.shouldLinger = false;
            this.shouldDiscard = true;
            explode(this.power);
        }
        super.tick();
    }

    @Override
    protected Item getDefaultItem() {
        return GrenadesModItems.GRENADE_FIRE;
    }

    @Override
    protected void explode(float power) {
        BlockPos impactPos = this.getBlockPos();
        Box impactBox = new Box(impactPos).expand(power, power / 2, power);

        Stream<BlockPos> posStream = BlockPos.stream(impactBox);
        posStream.filter(pos -> Math.sqrt(pos.getSquaredDistance(impactPos)) <= power)
                .filter(pos -> AbstractFireBlock.canPlaceAt(world, pos, this.getMovementDirection()))
                .forEach(pos -> {
                    BlockState fireState = AbstractFireBlock.getState(world, pos.offset(this.getMovementDirection()));
                    world.setBlockState(pos, fireState, 11);
                });

        if (this.shouldLinger) {
            List<LivingEntity> list = world.getNonSpectatingEntities(LivingEntity.class, impactBox);
            list.stream().filter(e -> Math.sqrt(e.squaredDistanceTo(
                            impactPos.getX(), impactPos.getY(), impactPos.getZ())) <= 1.5F)
                    .forEach(Entity::setOnFireFromLava);
        }

        if (this.shouldDiscard) {
            this.discard();
        }
    }
}
