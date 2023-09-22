package fenn7.grenadesandgadgets.commonside.entity.grenades;

import java.util.Set;

import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class HighExplosiveGrenadeEntity extends AbstractGrenadeEntity {
    private static final float EXPLOSION_POWER = 2.0F;
    private static final ParticleEffect GRENADE_EFFECT = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.POLISHED_BLACKSTONE.getDefaultState());
    private static final GrenadesModSoundProfile HIGHEXPLOSIVE_SOUND_PROFILE = new GrenadesModSoundProfile(SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.8F, 0.75F);
    private static final float PROXIMITY_DAMAGE = 5.0F;

    private static final int MAX_ARMOUR_BREAK_DURATION = 160;
    private static final int MAX_ARMOUR_BREAK_LEVEL = 2;

    public HighExplosiveGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public HighExplosiveGrenadeEntity(World world, PlayerEntity user) {
        super(GrenadesModEntities.HIGH_EXPLOSIVE_GRENADE_ENTITY, world, user);
    }

    public HighExplosiveGrenadeEntity(World world, double x, double y, double z) {
        super(GrenadesModEntities.HIGH_EXPLOSIVE_GRENADE_ENTITY, world, x, y, z);
    }

    @Override
    protected void initialise() {
        this.setPower(EXPLOSION_POWER);
        this.setExplosionEffect(GRENADE_EFFECT);
        this.setExplosionSoundProfile(HIGHEXPLOSIVE_SOUND_PROFILE);
    }

    @Override
    protected void explode() {
        if (!this.world.isClient()) {
            this.world.createExplosion(null, this.getX(), this.getY(), this.getZ(), this.getPower(), Explosion.DestructionType.NONE);
            Set<LivingEntity> list = GrenadesModUtil.getLivingEntitiesAtRangeFromEntity(this.world, this, this.getPower());
            list.forEach(e -> {
                e.damage(DamageSource.thrownProjectile(this, this.getOwner()), PROXIMITY_DAMAGE);
                StatusEffectInstance currentAB = e.getStatusEffect(GrenadesModStatus.ARMOUR_BREAK);
                int currentABLevel = currentAB == null ? -1 : currentAB.getAmplifier();
                e.addStatusEffect(new StatusEffectInstance(GrenadesModStatus.ARMOUR_BREAK,
                    MAX_ARMOUR_BREAK_DURATION, Math.min(currentABLevel + 1, MAX_ARMOUR_BREAK_LEVEL)));
            });
            // set fire ON ?
        }
        this.discard();
    }

    protected Item getDefaultItem() {
        return GrenadesModItems.GRENADE_HIGH_EXPLOSIVE;
    }
}
