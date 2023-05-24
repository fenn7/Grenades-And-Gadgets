package fenn7.grenadesandgadgets.commonside.entity.projectiles;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.world.World;

public abstract class AbstractLingeringGrenadeEntity extends AbstractGrenadeEntity {
    protected int maxLingeringTicks;
    protected int lingeringTicks = 0;

    public AbstractLingeringGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }



    protected enum LingeringState {
        LINGERING,
        DISCARDED
    }
}
