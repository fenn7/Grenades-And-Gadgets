package fenn7.grenadesandgadgets.commonside.entity.grenades;

import java.util.Set;

import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
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
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class GrenadeEntity extends AbstractGrenadeEntity {
    private static final float EXPLOSION_POWER = 1.25F;
    private static final ParticleEffect GRENADE_EFFECT = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.IRON_BLOCK.getDefaultState());
    private static final float PROXIMITY_DAMAGE = 3.0F;

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
            Set<LivingEntity> list =
                GrenadesModUtil.getLivingEntitiesAtRangeFromEntity(this.world, this, power);
            list.stream().forEach(e -> e.damage(DamageSource.thrownProjectile(this, this.getOwner()), PROXIMITY_DAMAGE));
            this.world.createExplosion(null, this.getX(), this.getY(), this.getZ(), power, Explosion.DestructionType.NONE);
        }
        this.discard();
    }

    protected Item getDefaultItem() {
        return GrenadesModItems.GRENADE;
    }
}
