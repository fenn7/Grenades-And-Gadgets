package fenn7.grenadesandgadgets.commonside.entity.grenades;

import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class SmokeFlareGrenadeEntity extends AbstractLingeringGrenadeEntity {
    private static final float SMOKE_VELOCITY = 0.6F;
    private static final int MAX_LINGERING_TICKS = 1200;
    private static final int DEFAULT_COLOR = 0x696969;
    private static final GrenadesModSoundProfile SMOKEFLARE_SOUND_PROFILE = new GrenadesModSoundProfile(SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 1.7F, 1.35F);

    public SmokeFlareGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public SmokeFlareGrenadeEntity(World world, PlayerEntity user) {
        super(GrenadesModEntities.SMOKE_FLARE_GRENADE_ENTITY, world, user);
    }

    @Override
    protected void initialise() {
        this.maxLingeringTicks = MAX_LINGERING_TICKS;
        this.setPower(SMOKE_VELOCITY);
        this.setExplosionSoundProfile(SMOKEFLARE_SOUND_PROFILE);
    }

    @Override
    public void tick() {
        if (this.world.isClient && this.state == LingeringState.LINGERING && this.lingeringTicks % 3 == 0) {
            double xRand = this.random.nextDouble(-0.03D, 0.03D);
            double zRand = this.random.nextDouble(-0.03D, 0.03D);
            ParticleEffect smokeEffect = ParticleTypes.CAMPFIRE_SIGNAL_SMOKE;
            this.world.addParticle(smokeEffect, this.getX(), this.getY(), this.getZ(),
                xRand, this.power, zRand);
        }
        super.tick();
    }

    @Override
    protected Item getDefaultItem() {
        return GrenadesModItems.GRENADE_SMOKE_FLARE;
    }
}
