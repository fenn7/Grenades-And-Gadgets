package fenn7.grenadesandgadgets.commonside.entity.grenades;

import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
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
        super(entityType, world, x, y, z);
    }

    @Override
    public void tick() {
        if (this.state == LingeringState.LINGERING) {
            if (this.lingeringTicks < this.maxLingeringTicks) {
                if (!this.world.isClient) {
                    ++this.lingeringTicks;
                }
            } else {
                this.state = LingeringState.DISCARDED;
                this.handleDiscard();
            }
        }
        super.tick();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("lingering_ticks", this.lingeringTicks);
        nbt.putInt("max_lingering_ticks", this.maxLingeringTicks);
        nbt.putInt("lingering_state", this.state.ordinal());
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.lingeringTicks = nbt.getInt("lingering_ticks");
        this.maxLingeringTicks = nbt.getInt("max_lingering_ticks");

        int ordinal = nbt.getInt("lingering_state");
        if (ordinal >= 0) {
            this.setInactive();
        }
        this.state = LingeringState.values()[ordinal];
        super.readNbt(nbt);
    }

    protected void handleDiscard() {
        this.discard();
    }

    public void setState(LingeringState state) {
        this.state = state;
    }

    public LingeringState getState() {
        return this.state;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (this.state != LingeringState.UNEXPLODED) {
            return;
        }
        super.onBlockHit(blockHitResult);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (this.state != LingeringState.UNEXPLODED) {
            return;
        }
        super.onEntityHit(entityHitResult);
    }

    @Override
    protected void explode(float power) {
        if (this.state == LingeringState.UNEXPLODED) {
            this.setInactive();
            this.setState(LingeringState.LINGERING);
        }
    }

    protected void setInactive() {
        this.setVelocity(Vec3d.ZERO);
        this.setNoGravity(true);
        this.setInvisible(true);
        this.setInvulnerable(true);
    }

    protected enum LingeringState {
        UNEXPLODED,
        LINGERING,
        DISCARDED
    }
}
