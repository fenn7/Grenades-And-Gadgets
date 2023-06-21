package fenn7.grenadesandgadgets.commonside.entity.grenades;

import java.util.List;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public abstract class AbstractDisplacementGrenadeEntity extends AbstractLingeringGrenadeEntity {
    public AbstractDisplacementGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public AbstractDisplacementGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world, PlayerEntity user) {
        super(entityType, world, user);
    }

    public AbstractDisplacementGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world, double x, double y, double z) {
        super(entityType, world, x, y, z);
    }

    @Override
    public void tick() {
        if (this.state == LingeringState.LINGERING && this.world.isClient) {
            this.handleParticleEffects();
        }
        super.tick();
    }

    protected void handleParticleEffects() {
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
                affectedEntities.forEach(entity -> this.handleDisplacement(entity, pos, affectedEntities));
            });
        }
    }

    protected abstract void handleDisplacement(LivingEntity entity, BlockPos pos, List<LivingEntity> entities);
}
