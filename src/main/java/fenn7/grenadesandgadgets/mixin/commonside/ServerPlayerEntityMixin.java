package fenn7.grenadesandgadgets.mixin.commonside;

import java.util.Optional;

import com.mojang.authlib.GameProfile;
import fenn7.grenadesandgadgets.commonside.entity.misc.TemporalFissureEntity;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModEntityData;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.Heightmap;
import net.minecraft.world.PortalForcer;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Shadow private @Nullable Vec3d enteredNetherPos;
    @Shadow public abstract ServerWorld getWorld();
    @Shadow protected abstract void createEndSpawnPlatform(ServerWorld world, BlockPos centerPos);
    @Shadow @Nullable protected abstract TeleportTarget getTeleportTarget(ServerWorld destination);

    @Shadow protected abstract void worldChanged(ServerWorld origin);

    @Shadow public abstract void playerTick();

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "moveToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", ordinal = 0, shift = At.Shift.AFTER))
    private void grenadesandgadgets$setupEndToNetherDisplacement(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
        RegistryKey<World> registryKey = this.getWorld().getRegistryKey();
        if (registryKey == World.END && destination.getRegistryKey() == World.NETHER) {
            Vec3d position = this.getTeleportTarget(destination).position;
            BlockPos.stream(this.getDimensions(this.getPose()).getBoxAt(position).expand(1.0)).forEach(pos -> {
                BlockState state = destination.getBlockState(pos);
                if (!state.getBlock().canMobSpawnInside() && state.getBlock().getHardness() > 0) {
                    destination.breakBlock(pos, true);
                }
            });
        }
    }

    @Redirect(method = "getPortalRect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/PortalForcer;createPortal(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction$Axis;)Ljava/util/Optional;"))
    private Optional<BlockLocating.Rectangle> grenadesAndGadgets$cancelPortalCreation(PortalForcer instance, BlockPos pos, Direction.Axis axis, ServerWorld destWorld, BlockPos destPos, boolean destIsNether, WorldBorder worldBorder0) {
        return ((GrenadesModEntityData) this).getPersistentData().getBoolean(TemporalFissureEntity.NO_PORTAL_KEY)
            ? this.getRectNoPortal(destPos, axis)
            : destWorld.getPortalForcer().createPortal(destPos, axis);
    }

    private Optional<BlockLocating.Rectangle> getRectNoPortal(BlockPos pos, Direction.Axis axis) {
        int m, l;
        Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        double d = -1.0;
        BlockPos blockPos = null;
        double e = -1.0;
        BlockPos blockPos2 = null;
        WorldBorder worldBorder = this.world.getWorldBorder();
        int i = Math.min(this.world.getTopY(), this.world.getBottomY() + this.world.getDimension().getLogicalHeight()) - 1;
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (BlockPos.Mutable mutable2 : BlockPos.iterateInSquare(pos, 16, Direction.EAST, Direction.SOUTH)) {
            int j = Math.min(i, this.world.getTopY(Heightmap.Type.MOTION_BLOCKING, mutable2.getX(), mutable2.getZ()));
            if (!worldBorder.contains(mutable2) || !worldBorder.contains(mutable2.move(direction, 1))) continue;
            mutable2.move(direction.getOpposite(), 1);
            for (l = j; l >= this.world.getBottomY(); --l) {
                int n;
                mutable2.setY(l);
                if (!this.world.isAir(mutable2)) continue;
                m = l;
                while (l > this.world.getBottomY() && this.world.isAir(mutable2.move(Direction.DOWN))) {
                    --l;
                }
                if (l + 4 > i || (n = m - l) > 0 && n < 3) continue;
                mutable2.setY(l);
                if (!this.isValidPortalPos(mutable2, mutable, direction, 0)) continue;
                double f = pos.getSquaredDistance(mutable2);
                if (this.isValidPortalPos(mutable2, mutable, direction, -1) && this.isValidPortalPos(mutable2, mutable, direction, 1) && (d == -1.0 || d > f)) {
                    d = f;
                    blockPos = mutable2.toImmutable();
                }
                if (d != -1.0 || e != -1.0 && !(e > f)) continue;
                e = f;
                blockPos2 = mutable2.toImmutable();
            }
        }
        if (d == -1.0 && e != -1.0) {
            blockPos = blockPos2;
            d = e;
        }
        ((GrenadesModEntityData) this).getPersistentData().putBoolean(TemporalFissureEntity.NO_PORTAL_KEY, false);
        if (d == -1.0) {
            int p = i - 9;
            int o = Math.max(this.world.getBottomY() + 1, 70);
            if (p < o) {
                return Optional.empty();
            }
            blockPos = new BlockPos(pos.getX(), MathHelper.clamp(pos.getY(), o, p), pos.getZ()).toImmutable();
            if (!worldBorder.contains(blockPos)) {
                return Optional.empty();
            }
        }
        return Optional.of(new BlockLocating.Rectangle(blockPos.toImmutable(), 2, 3));
    }

    private boolean isValidPortalPos(BlockPos pos, BlockPos.Mutable temp, Direction portalDirection, int distanceOrthogonalToPortal) {
        Direction direction = portalDirection.rotateYClockwise();
        for (int i = -1; i < 3; ++i) {
            for (int j = -1; j < 4; ++j) {
                temp.set(pos, portalDirection.getOffsetX() * i + direction.getOffsetX() * distanceOrthogonalToPortal, j, portalDirection.getOffsetZ() * i + direction.getOffsetZ() * distanceOrthogonalToPortal);
                if (j < 0 && !this.world.getBlockState(temp).getMaterial().isSolid()) {
                    return false;
                }
                if (j < 0 || this.world.isAir(temp)) continue;
                return false;
            }
        }
        return true;
    }
}
