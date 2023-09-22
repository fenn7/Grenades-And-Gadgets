package fenn7.grenadesandgadgets.commonside.entity.grenades;

import java.util.Set;

import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class StormGrenadeEntity extends AbstractDisplacementGrenadeEntity {
    private static final float SHOCK_RANGE = 3.75F;
    private static final int MAX_DELAY_TICKS = 10;
    private static final int FATIGUE_DURATION = 180;
    private static final int TICKS_BETWEEN_EFFECTS = 3;
    private static final float STRONG_FATIGUE_RANGE = 2.0F;
    private static final double ANGLE_BETWEEN_EFFECTS = Math.PI / 12.0F;
    private static final ParticleEffect SHOCK_EFFECT = ParticleTypes.ELECTRIC_SPARK;
    private static final GrenadesModSoundProfile UPHEAVAL_SOUND_PROFILE = new GrenadesModSoundProfile(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0F, 1.5F);

    public StormGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public StormGrenadeEntity(World world, PlayerEntity user) {
        super(GrenadesModEntities.STORM_GRENADE_ENTITY, world, user);
    }

    public StormGrenadeEntity(World world, double x, double y, double z) {
        super(GrenadesModEntities.STORM_GRENADE_ENTITY, world, x, y, z);
    }

    @Override
    protected void initialise() {
        this.maxLingeringTicks = MAX_DELAY_TICKS;
        this.setPower(SHOCK_RANGE);
        this.setExplosionEffect(SHOCK_EFFECT);
        this.setExplosionSoundProfile(UPHEAVAL_SOUND_PROFILE);
    }

    @Override
    protected void handleParticleEffects() {
        if (this.lingeringTicks > 0 && this.lingeringTicks % TICKS_BETWEEN_EFFECTS == 0) {
            float angle = (float) ANGLE_BETWEEN_EFFECTS * (this.lingeringTicks / TICKS_BETWEEN_EFFECTS - 2);
            Vec3d pos = new Vec3d(this.getPower(), 0, 0).rotateZ(angle);
            for (int i = 0; i < 8; ++i) {
                Vec3d spawnPos = this.getPos().add(pos.rotateY((float) (Math.PI * i / 4.0D)));
                this.world.addParticle(SHOCK_EFFECT, spawnPos.x, spawnPos.y, spawnPos.z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected void handleDisplacement(Entity entity, BlockPos pos, Set<Entity> entities) {
        if (entity instanceof PlayerEntity player && player.isFallFlying()) {
            player.stopFallFlying();
        }
        entity.move(MovementType.SELF, new Vec3d(0, entity.world.getBottomY(), 0));
        if (entity instanceof LivingEntity alive) {
            alive.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, FATIGUE_DURATION, this.blockDistanceTo(entity.getBlockPos()) <= STRONG_FATIGUE_RANGE ? 1 : 0));
            if (!this.world.isClient) {
                LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, entity.world);
                lightning.setChanneler(this.getOwner() instanceof ServerPlayerEntity sPlayer ? sPlayer : null);
                lightning.setPos(entity.getX(), entity.getY(), entity.getZ());
                entity.world.spawnEntity(lightning);
            }
        }
    }

    protected Item getDefaultItem() {
        return GrenadesModItems.GRENADE_STORM;
    }
}
