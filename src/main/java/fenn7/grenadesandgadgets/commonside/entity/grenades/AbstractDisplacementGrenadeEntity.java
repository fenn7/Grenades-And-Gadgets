package fenn7.grenadesandgadgets.commonside.entity.grenades;

import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.util.math.BlockPos;
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
        if (this.state == LingeringState.LINGERING) {
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
            var entities = this.getEntitiesFromBlocks(this.getAffectedBlocksAtRange(power));
            entities.forEach(entity -> this.handleDisplacement(entity, this.getBlockPos(), entities));
        }
    }

    protected abstract void handleDisplacement(Entity entity, BlockPos pos, Set<Entity> entities);
}
