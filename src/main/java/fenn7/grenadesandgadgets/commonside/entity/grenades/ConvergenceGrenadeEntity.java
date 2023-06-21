package fenn7.grenadesandgadgets.commonside.entity.grenades;

import java.util.List;

import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Vibration;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.EntityPositionSource;

public class ConvergenceGrenadeEntity extends AbstractLingeringGrenadeEntity {
    // TODO: extraciate this to a superclass shared with Divergence and Upheaval grenades
    private static final float CONVERGENCE_RANGE = 4.0F;
    private static final float CRAM_DAMAGE_PER_ENTITY = 4.0F;
    private static final int MAX_DELAY_TICKS = 10;
    private static final ParticleEffect CONVERGENCE_GRENADE_EFFECT = ParticleTypes.EXPLOSION;
    private static final GrenadesModSoundProfile CONVERGENCE_GRENADE_SOUND_PROFILE = new GrenadesModSoundProfile(SoundEvents.BLOCK_BARREL_CLOSE, 1.5F,  0.5F);

    public ConvergenceGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public ConvergenceGrenadeEntity(World world, PlayerEntity user) {
        super(GrenadesModEntities.CONVERGENCE_GRENADE_ENTITY, world, user);
    }

    public ConvergenceGrenadeEntity(World world, double x, double y, double z) {
        super(GrenadesModEntities.CONVERGENCE_GRENADE_ENTITY, world, x, y, z);
    }

    @Override
    protected void initialise() {
        this.maxLingeringTicks = MAX_DELAY_TICKS;
        this.setPower(CONVERGENCE_RANGE);
        this.setExplosionEffect(CONVERGENCE_GRENADE_EFFECT);
        this.setExplosionSoundProfile(CONVERGENCE_GRENADE_SOUND_PROFILE);
    }

    @Override
    public void tick() {
        if (this.state == LingeringState.LINGERING && this.world.isClient) {
            double randomX = this.random.nextDouble(this.power);
            double randomY = this.random.nextDouble(-0.5, 0.5);
            double correspondingZ = Math.sqrt(Math.pow(this.power, 2) - Math.pow(randomX, 2));
            boolean shouldNegate = this.random.nextBoolean();

            double spawnX = shouldNegate ? this.getX() - randomX : this.getX() + randomX;
            double spawnY = shouldNegate ? this.getY() - randomY : this.getY() + randomY;
            double spawnZ = shouldNegate ? this.getZ() - correspondingZ : this.getZ() + correspondingZ;

            ParticleEffect vibration = new VibrationParticleEffect(new Vibration(new BlockPos(Math.round(spawnX), Math.round(spawnY), Math.round(spawnZ)), new EntityPositionSource(this.getId()), this.maxLingeringTicks));
            this.world.addParticle(vibration, spawnX, spawnY, spawnZ, 0, 0, 0);
        }
        super.tick();
    }

    @Override
    protected void handleDiscard() {
        this.explode(this.power);
        super.handleDiscard();
    }

    @Override
    protected void explode(float power) {
        super.explode(power);
        if (this.state == LingeringState.DISCARDED) {
            this.getAffectedBlocksAtRange(power).forEach(pos -> {
                List<LivingEntity> affectedEntities = this.world.getNonSpectatingEntities(LivingEntity.class, new Box(pos));
                affectedEntities.forEach(entity -> {
                    entity.move(MovementType.SELF, this.getPos().subtract(entity.getPos()));
                    entity.damage(DamageSource.CRAMMING, affectedEntities.size() * CRAM_DAMAGE_PER_ENTITY);
                });
            });
        }
    }

    @Override
    protected Item getDefaultItem() {
        return GrenadesModItems.GRENADE_CONVERGENCE;
    }
}
