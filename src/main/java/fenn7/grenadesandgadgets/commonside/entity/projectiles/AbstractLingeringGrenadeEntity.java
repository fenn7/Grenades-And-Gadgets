package fenn7.grenadesandgadgets.commonside.entity.projectiles;

import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class AbstractLingeringGrenadeEntity extends AbstractGrenadeEntity {
    protected LingeringState state = LingeringState.UNEXPLODED;
    protected int maxLingeringTicks;
    protected int lingeringTicks = 0;

    public AbstractLingeringGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public AbstractLingeringGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world, PlayerEntity user) {
        super(entityType, world, user);
    }

    public AbstractLingeringGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world, double x, double y, double z) {
        super(GrenadesModEntities.FIRE_GRENADE_ENTITY, world, x, y, z);
    }

    public void setState(LingeringState state) {
        this.state = state;
    }

    public LingeringState getState() {
        return this.state;
    }

    protected void setInactive() {
        this.setVelocity(Vec3d.ZERO);
        this.setNoGravity(true);
        this.setInvisible(true);
    }

    @Override
    public void tick() {
        if (this.state == LingeringState.LINGERING && !this.world.isClient) {
            if (this.lingeringTicks < this.maxLingeringTicks) {
                ++this.lingeringTicks;
            } else {
                this.state = LingeringState.DISCARDED;
                this.handleDiscard();
            }
        }
        super.tick();
    }

    protected void handleDiscard() {
        this.discard();
    }

    protected enum LingeringState {
        UNEXPLODED,
        LINGERING,
        DISCARDED
    }
}
