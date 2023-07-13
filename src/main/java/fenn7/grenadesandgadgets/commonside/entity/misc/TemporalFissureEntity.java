package fenn7.grenadesandgadgets.commonside.entity.misc;

import java.util.Set;

import fenn7.grenadesandgadgets.commonside.damage.GrenadesModDamageSources;
import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class TemporalFissureEntity extends Entity implements IAnimatable {
    public static final TrackedData<Integer> DIMENSION_KEY = DataTracker.registerData(TemporalFissureEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final String DIM_KEY = "dimKey";
    private static final float DISPLACEMENT_DAMAGE = 10.0F;
    private static final float MAX_DISPLACEMENT_THRESHOLD = 100.0F;
    private static final float MAX_CATASTROPHIC_DAMAGE = 80.0F;
    private static final int MAX_LIFE_BASE = 800;
    private static final int EXTRA_LIFE_PER_RANGE = 20;
    private static final int SECONDS_BETWEEN_SPREAD = 15;
    private final AnimationFactory factory = GrenadesModUtil.getAnimationFactoryFor(this);
    private Set<BlockPos> affectedBlocks;
    private float range;
    private @Nullable Entity summoner;

    public TemporalFissureEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    public TemporalFissureEntity(World world, float range, Entity summoner, int dimKey) {
        super(GrenadesModEntities.TEMPORAL_FISSURE, world);
        this.range = range;
        this.summoner = summoner;
        this.getDataTracker().set(DIMENSION_KEY, dimKey);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(DIMENSION_KEY, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age > MAX_LIFE_BASE + (this.range * EXTRA_LIFE_PER_RANGE)) {
            this.discard();
        }
        var affectedBlocks = this.getOrCreateAffectedBlocks();
        affectedBlocks.forEach(
            pos -> {
            }
        );
        // suck in entities with particle effect
        // overwrite blocks
        // decelerate entities
        // expand to 3x3 size
        if (!this.world.isClient) {
            if (this.world.equals(this.world.getServer().getWorld(World.END)) && this.squaredDistanceTo(Vec3d.ofCenter(ServerWorld.END_SPAWN_POS)) <= 25) {
                this.discard();
            }
            this.handleEntityCollisions();
        }
    }

    private void handleEntityCollisions() {
        Box collisionBox = this.getDimensions(this.getPose()).getBoxAt(this.getPos());
        this.world.getNonSpectatingEntities(Entity.class, collisionBox).stream()
            .filter(entity -> !(entity instanceof TemporalFissureEntity)).forEach(entity -> {
                ServerWorld destination = this.getDestinationWorld();
                boolean worldsMatch = this.world.getDimension().equals(destination.getDimension());
                if (entity instanceof LivingEntity alive) {
                    var displacementStats = this.calculateDisplacementDamage(alive, worldsMatch);
                    alive.damage(GrenadesModDamageSources.DIMENSIONAL_DISPLACEMENT, displacementStats.getLeft());
                    if (displacementStats.getRight()) {
                        this.discard();
                    }
                }
                if (!worldsMatch) {
                    if (destination.equals(this.world.getServer().getWorld(World.NETHER))) {
                        entity.setInNetherPortal(entity.getBlockPos());
                        entity.resetNetherPortalCooldown();
                        // TODO: DESTROY PORTAL ?
                    }
                    entity.moveToWorld(destination);
                }
            });
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        super.onPlayerCollision(player);
    }

    @Override
    public boolean isImmuneToExplosion() {
        return true;
    }

    @Override
    public boolean canUsePortals() {
        return false;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.dataTracker.set(DIMENSION_KEY, nbt.getInt(DIM_KEY));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt(DIM_KEY, this.dataTracker.get(DIMENSION_KEY));
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    private Pair<Float, Boolean> calculateDisplacementDamage(LivingEntity entity, boolean shouldHalve) {
        boolean cantDisplace = entity.getMaxHealth() > MAX_DISPLACEMENT_THRESHOLD || !entity.canUsePortals();
        float baseDamage = cantDisplace ? MAX_CATASTROPHIC_DAMAGE : DISPLACEMENT_DAMAGE;
        return new Pair<>(shouldHalve ? baseDamage / 2F : baseDamage, cantDisplace);
    }

    private ServerWorld getDestinationWorld() {
        if (this.world.isClient()) {
            return null;
        }
        MinecraftServer server = ((ServerWorld) this.world).getServer();
        return switch (this.getDataTracker().get(DIMENSION_KEY)) {
            case -1 -> server.getWorld(World.NETHER);
            case 1 -> server.getWorld(World.END);
            default -> server.getOverworld();
        };
    }

    private Set<BlockPos> getOrCreateAffectedBlocks() {
        if (this.affectedBlocks != null) {
            return this.affectedBlocks;
        } else {
            Set<BlockPos> affectedBlocks = GrenadesModUtil.getBlocksInSphereAroundPos(this.getBlockPos(), this.range + 1F);
            this.affectedBlocks = affectedBlocks;
            return affectedBlocks;
        }
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.model.idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "controller",
            0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
