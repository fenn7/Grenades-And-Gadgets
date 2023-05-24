package fenn7.grenadesandgadgets.commonside.entity.projectiles;

import java.util.List;

import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class GrenadeEntity extends AbstractGrenadeEntity {
    private static final float EXPLOSION_POWER = 1.2F;
    private static final ParticleEffect GRENADE_EFFECT = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.IRON_BLOCK.getDefaultState());

    public GrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public GrenadeEntity(World world, PlayerEntity user) {
        super(GrenadesModEntities.GRENADE_ENTITY, world, user);
    }

    public GrenadeEntity(World world, double x, double y, double z) {
        super(GrenadesModEntities.GRENADE_ENTITY, world, x, y, z);
    }

    @Override
    protected void initialise() {
        this.setPower(EXPLOSION_POWER);
        this.setExplosionEffect(GRENADE_EFFECT);
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
    }

    @Override
    protected void explode(float power) {
        if (!this.world.isClient()) {
            List<LivingEntity> list = this.world.getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(this.power, this.power, this.power));
            list.stream().forEach(e -> e.damage(DamageSource.thrownProjectile(this, this.getOwner()), 3.0F));
            this.world.createExplosion(null, this.getX(), this.getY(), this.getZ(), power, Explosion.DestructionType.NONE);
        }
        this.discard();
    }

    protected Item getDefaultItem() {
        return GrenadesModItems.GRENADE;
    }
}
