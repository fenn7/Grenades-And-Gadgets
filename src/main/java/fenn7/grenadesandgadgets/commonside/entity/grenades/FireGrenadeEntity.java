package fenn7.grenadesandgadgets.commonside.entity.grenades;

import java.util.List;
import java.util.stream.Stream;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
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
import net.minecraft.util.hit.BlockHitResult;
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
            this.explodeWithEffects(this.power * 0.66F);
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
        super.explode(power);
        BlockPos impactPos = this.getBlockPos();
        Box impactBox = new Box(impactPos).expand(power, power / 2F, power);

        Stream<BlockPos> posStream = BlockPos.stream(impactBox);
        posStream.filter(pos -> Math.sqrt(pos.getSquaredDistance(impactPos)) <= power)
                .filter(pos -> AbstractFireBlock.canPlaceAt(world, pos, this.getMovementDirection())
                    && this.shouldSetOnFire(this.world, pos))
                .forEach(pos -> {
                    BlockState fireState = AbstractFireBlock.getState(world, pos.offset(this.getMovementDirection()));
                    this.world.setBlockState(pos, fireState, 11);
                });

        if (this.state != LingeringState.LINGERING) {
            List<LivingEntity> list = GrenadesModUtil.getLivingEntitiesAtRangeFromEntity(this.world, this, 1.75F);
            list.stream().forEach(Entity::setOnFireFromLava);
        }
    }

    private boolean shouldSetOnFire(World world, BlockPos firePos) {
        for (double x = Math.min(firePos.getX(), this.getX()); x <= Math.max(firePos.getX(), this.getX()); x++) {
            for (double y = Math.min(firePos.getY(), this.getY()); y <= Math.max(firePos.getY(), this.getY()); y++) {
                for (double z = Math.min(firePos.getZ(), this.getZ()); z <= Math.max(firePos.getZ(), this.getZ()); z++) {
                    BlockState between = world.getBlockState(new BlockPos(x, y, z));
                    if (between != null && between.getMaterial().isSolid() && !between.getMaterial().isBurnable()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
