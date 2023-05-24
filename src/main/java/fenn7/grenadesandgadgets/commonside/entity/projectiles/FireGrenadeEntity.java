package fenn7.grenadesandgadgets.commonside.entity.projectiles;

import java.util.List;
import java.util.stream.Stream;

import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class FireGrenadeEntity extends AbstractLingeringGrenadeEntity {
    private static final float FIRE_RANGE = 2.8F;
    private static final int MAX_LINGERING_TICKS = 8;
    private static final ParticleEffect FIRE_GRENADE_EFFECT = ParticleTypes.LAVA;
    private static final GrenadesModSoundProfile FIRE_GRENADE_SOUND_PROFILE = new GrenadesModSoundProfile(SoundEvents.ENTITY_BLAZE_SHOOT, 1.5F, 0.675F);

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
    protected void initialise() {
        this.maxLingeringTicks = MAX_LINGERING_TICKS;
        this.setPower(FIRE_RANGE);
        this.setExplosionEffect(FIRE_GRENADE_EFFECT);
        this.setExplosionSoundProfile(FIRE_GRENADE_SOUND_PROFILE);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (!this.world.isClient()) {
            this.setInactive();
            this.explodeWithEffects(this.power * 0.66F);
            this.setState(LingeringState.LINGERING);
        }
        super.onCollision(hitResult);
    }

    @Override
    protected void handleDiscard() {
        this.explode(this.power);
        super.handleDiscard();
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

        if (this.state != LingeringState.LINGERING) {
            List<LivingEntity> list = world.getNonSpectatingEntities(LivingEntity.class, impactBox);
            list.stream().filter(e -> Math.sqrt(e.squaredDistanceTo(
                            impactPos.getX(), impactPos.getY(), impactPos.getZ())) <= 1.5F)
                    .forEach(Entity::setOnFireFromLava);
        }
    }
}
