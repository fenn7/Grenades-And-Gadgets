package fenn7.grenadesandgadgets.commonside.entity.grenades;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
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

public class IceGrenadeEntity extends AbstractGrenadeEntity {
    private static final float ICE_RANGE = 2.8F;
    private static final int MAX_DELAY_TICKS = 10;
    private static final ParticleEffect ICE_GRENADE_EFFECT = ParticleTypes.SNOWFLAKE;
    private static final GrenadesModSoundProfile ICE_GRENADE_SOUND_PROFILE = new GrenadesModSoundProfile(SoundEvents.ENTITY_PLAYER_HURT_FREEZE, 1.25F, 1.0F);

    public IceGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public IceGrenadeEntity(World world, PlayerEntity user) {
        super(GrenadesModEntities.ICE_GRENADE_ENTITY, world, user);
    }

    public IceGrenadeEntity(World world, double x, double y, double z) {
        super(GrenadesModEntities.ICE_GRENADE_ENTITY, world, x, y, z);
    }

    @Override
    protected void initialise() {
        this.setPower(ICE_RANGE);
        this.setExplosionEffect(ICE_GRENADE_EFFECT);
        this.setExplosionSoundProfile(ICE_GRENADE_SOUND_PROFILE);
    }

    @Override
    protected void explode(float power) {
        GrenadesMod.LOGGER.warn("I'M BLOOOOOOOOOOOMING !!!!!");
        this.discard();
    }

    @Override
    protected Item getDefaultItem() {
        return GrenadesModItems.GRENADE_ICE;
    }
}
