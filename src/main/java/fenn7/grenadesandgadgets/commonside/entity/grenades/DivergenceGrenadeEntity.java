package fenn7.grenadesandgadgets.commonside.entity.grenades;

import java.util.Set;

import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Vibration;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;

public class DivergenceGrenadeEntity extends AbstractDisplacementGrenadeEntity {
    private static final float DIVERGENCE_RANGE = 4.0F;
    private static final float DISPLACEMENT_DAMAGE_PER_ENTITY = 6.0F;
    private static final int MAX_DELAY_TICKS = 15;
    private static final ParticleEffect DIVERGENCE_GRENADE_EFFECT = ParticleTypes.EXPLOSION;
    private static final GrenadesModSoundProfile DIVERGENCE_GRENADE_SOUND_PROFILE = new GrenadesModSoundProfile(SoundEvents.BLOCK_BARREL_OPEN, 1.75F,  0.33F);

    public DivergenceGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public DivergenceGrenadeEntity(World world, PlayerEntity user) {
        super(GrenadesModEntities.DIVERGENCE_GRENADE_ENTITY, world, user);
    }

    public DivergenceGrenadeEntity(World world, double x, double y, double z) {
        super(GrenadesModEntities.DIVERGENCE_GRENADE_ENTITY, world, x, y, z);
    }

    @Override
    protected void initialise() {
        this.maxLingeringTicks = MAX_DELAY_TICKS;
        this.setPower(DIVERGENCE_RANGE);
        this.setExplosionEffect(DIVERGENCE_GRENADE_EFFECT);
        this.setExplosionSoundProfile(DIVERGENCE_GRENADE_SOUND_PROFILE);
    }

    @Override
    protected void handleParticleEffects() {
        if (this.world.isClient) {
            double randomX = this.random.nextDouble(this.getPower());
            double randomY = this.random.nextDouble(-0.5, 0.5);
            double correspondingZ = Math.sqrt(Math.pow(this.getPower(), 2) - Math.pow(randomX, 2));
            boolean shouldNegate = this.random.nextBoolean();

            double spawnX = shouldNegate ? this.getX() - randomX : this.getX() + randomX;
            double spawnY = shouldNegate ? this.getY() - randomY : this.getY() + randomY;
            double spawnZ = shouldNegate ? this.getZ() - correspondingZ : this.getZ() + correspondingZ;

            ParticleEffect vibration = new VibrationParticleEffect(new Vibration(this.getBlockPos(), new BlockPositionSource(new BlockPos(Math.round(spawnX), Math.round(spawnY), Math.round(spawnZ))), this.maxLingeringTicks));
            this.world.addParticle(vibration, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }

    @Override
    protected void handleDisplacement(Entity entity, BlockPos pos, Set<Entity> entities) {
        entity.move(MovementType.SELF, entity.getPos().subtract(this.getPos()).normalize().multiply(this.getPower()));
        if (entity instanceof LivingEntity alive) {
            alive.damage(DamageSource.thrownProjectile(this, this.getOwner()), DISPLACEMENT_DAMAGE_PER_ENTITY);
            alive.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40));
        }
    }

    @Override
    protected Item getDefaultItem() {
        return GrenadesModItems.GRENADE_DIVERGENCE;
    }
}
