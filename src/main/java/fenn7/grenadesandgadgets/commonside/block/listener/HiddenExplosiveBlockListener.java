package fenn7.grenadesandgadgets.commonside.block.listener;

import java.util.Optional;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.block.custom.HiddenExplosiveBlock;
import fenn7.grenadesandgadgets.commonside.block.entity.HiddenExplosiveBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.GameEventTags;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.Vibration;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

public class HiddenExplosiveBlockListener implements GameEventListener {
    protected final PositionSource positionSource;
    protected final HiddenExplosiveBlockEntity blockEntity;
    protected Optional<GameEvent> event = Optional.empty();
    protected int range;
    protected int distance;
    protected int delay = 0;

    public HiddenExplosiveBlockListener(PositionSource positionSource, HiddenExplosiveBlockEntity blockEntity) {
        this.positionSource = positionSource;
        this.blockEntity = blockEntity;
    }

    public void tick(World world) {
        this.range = this.blockEntity.getDetectRange();
        if (this.event.isPresent()) {
            --this.delay;
            if (this.delay <= 0) {
                this.delay = 0;
                this.blockEntity.detonate(world, this.blockEntity.getPos());
                this.event = Optional.empty();
            }
        }
    }

    @Override
    public PositionSource getPositionSource() {
        return this.positionSource;
    }

    @Override
    public int getRange() {
        return this.range;
    }

    @Override
    public boolean listen(World world, GameEvent event, @Nullable Entity entity, BlockPos pos) {
        if (!this.blockEntity.getCachedState().get(HiddenExplosiveBlock.ARMED) || !this.shouldActivate(event, entity)) {
            return false;
        }
        Optional<BlockPos> optional = this.positionSource.getPos(world);
        if (optional.isEmpty()) {
            return false;
        }
        BlockPos blockPos = optional.get();
        if (!this.accepts(world, this, pos, event, entity) || this.isBlocked(world, pos, blockPos)) {
            return false;
        }
        this.listen(world, event, pos, blockPos);
        return true;
    }

    private boolean shouldActivate(GameEvent event, @Nullable Entity entity) {
        if (this.event.isPresent() || !event.isIn(GameEventTags.VIBRATIONS)) {
            return false;
        }
        if (entity != null) {
            if (entity.occludeVibrationSignals() || event.isIn(GameEventTags.IGNORE_VIBRATIONS_SNEAKING) && entity.bypassesSteppingEffects()) {
                return false;
            }
        }
        return entity == null || !entity.isSpectator();
    }

    private void listen(World world, GameEvent event, BlockPos pos, BlockPos sourcePos) {
        this.event = Optional.of(event);
        if (world instanceof ServerWorld sw) {
            GrenadesMod.LOGGER.warn("Vibration detected at " + pos + " from " + sourcePos);
            this.delay = this.distance = MathHelper.floor(Math.sqrt(pos.getSquaredDistance(sourcePos)));
            sw.spawnParticles(new VibrationParticleEffect(new Vibration(pos, this.positionSource, this.delay)), pos.getX(), pos.getY(), pos.getZ(), 1,0, 0, 0, 0);
        }
    }

    private boolean isBlocked(World world, BlockPos pos, BlockPos sourcePos) {
        return world.raycast(new BlockStateRaycastContext(Vec3d.ofCenter(pos), Vec3d.ofCenter(sourcePos), s -> true)).getType() == HitResult.Type.BLOCK;
    }

    public boolean accepts(World world, HiddenExplosiveBlockListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity) {
        boolean placeEvent = event == GameEvent.BLOCK_PLACE && pos.equals(this.blockEntity.getPos());
        boolean destroyEvent = event == GameEvent.BLOCK_DESTROY && pos.equals(this.blockEntity.getPos());
        return !placeEvent && !destroyEvent;
    }

    public void accept(World world, HiddenExplosiveBlockListener listener, GameEvent event, int distance) {
        int y = 0;
    }
}
