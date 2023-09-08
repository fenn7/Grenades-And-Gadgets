package fenn7.grenadesandgadgets.commonside.entity.misc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fenn7.grenadesandgadgets.commonside.damage.GrenadesModDamageSources;
import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.entity.grenades.TemporalFissureGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import fenn7.grenadesandgadgets.commonside.tags.GrenadesModTags;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModEntityData;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
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
    private static final List<Block> OW_BLOCKS = GrenadesModUtil.loadBlocksFromTag(GrenadesModTags.Blocks.OVERWORLD_FISSURE_CORRUPTION).stream().toList();
    private static final List<Block> NETHER_BLOCKS = GrenadesModUtil.loadBlocksFromTag(GrenadesModTags.Blocks.NETHER_FISSURE_CORRUPTION).stream().toList();
    private static final List<Block> END_BLOCKS = GrenadesModUtil.loadBlocksFromTag(GrenadesModTags.Blocks.END_FISSURE_CORRUPTION).stream().toList();
    public static final String NO_PORTAL_KEY = "no_portal_spawn";
    private static final String AGE_KEY = "age_key";
    private static final String DIM_KEY = "dim_key";
    private static final String RANGE_KEY = "range_key";
    private static final float BASE_CORRUPTION_CHANCE = 0.6F;
    private static final float DRAG_PER_RANGE = 0.15F;
    private static final float DISPLACEMENT_DAMAGE = 10.0F;
    private static final float MAX_DISPLACEMENT_THRESHOLD = 100.0F;
    private static final float MAX_CATASTROPHIC_DAMAGE = 80.0F;
    private static final int MAX_LIFE_BASE = 600;
    private static final int EXTRA_LIFE_PER_RANGE = 20;
    private static final int TICKS_BETWEEN_SPREAD = 15;
    private final AnimationFactory factory = GrenadesModUtil.getAnimationFactoryFor(this);
    private Set<BlockPos> affectedBlocks;
    private float range;

    public TemporalFissureEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    public TemporalFissureEntity(World world, float range, int dimKey) {
        super(GrenadesModEntities.TEMPORAL_FISSURE, world);
        this.range = range;
        this.getDataTracker().set(DIMENSION_KEY, dimKey);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(DIMENSION_KEY, 0);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void tick() {
        super.tick();
        if (!this.world.isClient) {
            if (this.age > MAX_LIFE_BASE + (this.range * EXTRA_LIFE_PER_RANGE) || (this.world.equals(this.getServer().getWorld(World.END)) && this.squaredDistanceTo(Vec3d.ofCenter(ServerWorld.END_SPAWN_POS)) <= 16)) {
                this.discard();
                return;
            }
            ServerWorld destination = this.getDestinationWorld();
            boolean worldsMatch = this.world.getDimension().equals(destination.getDimension());
            this.handleEntityCollisions(destination, worldsMatch);

            if (this.age % TICKS_BETWEEN_SPREAD == 0) {
                var affectedBlocks = this.getOrCreateAffectedBlocks();
                this.getAffectedEntities(affectedBlocks).forEach(entity -> {
                    if (entity instanceof LivingEntity alive) {
                        alive.addStatusEffect(new StatusEffectInstance(GrenadesModStatus.DECELERATE, 100, alive.hasStatusEffect(GrenadesModStatus.DECELERATE) ? alive.getStatusEffect(GrenadesModStatus.DECELERATE).getAmplifier() + 1 : 0));
                    }
                    Vec3d displacement = this.getPos().subtract(entity.getPos()).normalize().multiply(DRAG_PER_RANGE * this.range);
                    entity.addVelocity(displacement.x, displacement.y, displacement.z);
                });

                int corruptingRange = this.age / TICKS_BETWEEN_SPREAD;
                if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) && corruptingRange <= this.range && !worldsMatch) {
                    var blockList = switch (this.dataTracker.get(DIMENSION_KEY)) {
                        case -1 -> NETHER_BLOCKS;
                        case 1 -> END_BLOCKS;
                        default -> OW_BLOCKS;
                    };
                    affectedBlocks.stream().filter(pos -> this.canOverwrite(pos) && (int) Math.sqrt(pos.getSquaredDistance(this.getBlockPos())) == corruptingRange).forEach(pos -> {
                        if (this.random.nextFloat() < BASE_CORRUPTION_CHANCE + (this.range / 100F)) {
                            this.world.breakBlock(pos, false, this);
                            this.world.setBlockState(pos, blockList.get(this.random.nextInt(blockList.size())).getDefaultState());
                        }
                    });
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void handleEntityCollisions(ServerWorld destination, boolean worldsMatch) {
        Box collisionBox = this.getDimensions(this.getPose()).getBoxAt(this.getPos());
        var positions = new HashSet<BlockPos>();
        BlockPos.stream(collisionBox).forEach(pos -> {
            if (this.world.getBlockState(pos).getMaterial().equals(Material.PORTAL)) {
                this.discard();
                return;
            }
            positions.add(pos.toImmutable());
        });
        if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            positions.stream().filter(this::canOverwrite).forEach(pos -> this.world.breakBlock(pos, true, this));
        }
        this.world.getNonSpectatingEntities(Entity.class, collisionBox).stream()
            .filter(entity -> !(entity instanceof TemporalFissureEntity || entity instanceof TemporalFissureGrenadeEntity)).forEach(entity -> {
                this.spawnHitParticles(entity);
                if (entity instanceof LivingEntity alive) {
                    var displacementStats = this.calculateDisplacementDamage(alive, worldsMatch);
                    alive.damage(GrenadesModDamageSources.DIMENSIONAL_DISPLACEMENT, displacementStats.getLeft());
                    if (displacementStats.getRight()) {
                        this.discard();
                        return;
                    }
                }
                if (!worldsMatch) {
                    // TODO: DEDICATED WORLD MOVEMENT HANDLER
                    // FOR: NETHER -> OVERWORLD, OVERWORLD -> NETHER;
                    // END -> NETHER WILL DO END -> OVERWORLD -> NETHER.
                    ServerWorld nether = this.getServer().getWorld(World.NETHER);
                    if (destination.equals(nether) || this.world.equals(nether)) {
                        entity.setInNetherPortal(entity.getBlockPos());
                        entity.resetNetherPortalCooldown();
                    }
                    entity.moveToWorld(destination);
                } else {
                    Vec3d repulsion = entity.getPos().subtract(this.getPos()).normalize().multiply(this.range);
                    entity.addVelocity(repulsion.x / 2F, repulsion.y / this.range, repulsion.z / 2F);
                }
            });
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

    private Set<Entity> getAffectedEntities(Set<BlockPos> blocks) {
        Set<Entity> entities = new HashSet<>();
        blocks.forEach(pos -> entities.addAll(this.world.getNonSpectatingEntities(Entity.class, new Box(pos)).stream()
            .filter(entity -> !(entity instanceof TemporalFissureEntity) && this.isExposedTo(entity)).toList()));
        return entities;
    }

    private boolean isExposedTo(Entity entity) {
        var positions = Set.of(new Pair<>(this.getBodyY(0F), entity.getBodyY(0F)), new Pair<>(this.getBodyY(1F), entity.getBodyY(1F)));
        for (var pair : positions) {
            Vec3d thisPos = new Vec3d(this.getPos().getX(), pair.getLeft(), this.getPos().getZ());
            Vec3d entityPos = new Vec3d(entity.getPos().getX(), pair.getRight(), entity.getPos().getZ());
            if (this.world.raycast(new RaycastContext(thisPos, entityPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this)).getType().equals(HitResult.Type.MISS)) {
                return true;
            }
        }
        return false;
    }

    private boolean canOverwrite(BlockPos pos) {
        BlockState state = this.world.getBlockState(pos);
        return state.getMaterial().isSolid() && !state.isIn(GrenadesModTags.Blocks.TEMPORAL_FISSURE_IMMUNE);
    }

    private void spawnHitParticles(Entity entity) {
        for (int i = 0; i < 3; ++i) {
            float randX = this.random.nextFloat(-0.5F, 0.5F);
            float randY = this.random.nextFloat(-0.5F, 0.5F);
            float randZ = this.random.nextFloat(-0.5F, 0.5F);
            ((ServerWorld) this.world).spawnParticles(ParticleTypes.ENCHANT, entity.getX(), entity.getBodyY(0.5), entity.getZ(), 3, randX, randY, randZ, 0.15);
        }
        ((ServerWorld) this.world).spawnParticles(ParticleTypes.POOF, entity.getX(), entity.getBodyY(0.5), entity.getZ(), 1, 0, 0, 0, 0);
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        //((GrenadesModEntityData) player).getPersistentData().putBoolean(NO_PORTAL_KEY, true);
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
        this.age = nbt.getInt(AGE_KEY);
        this.range = nbt.getFloat(RANGE_KEY);
        this.dataTracker.set(DIMENSION_KEY, nbt.getInt(DIM_KEY));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt(AGE_KEY, this.age);
        nbt.putFloat(RANGE_KEY, this.range);
        nbt.putInt(DIM_KEY, this.dataTracker.get(DIMENSION_KEY));
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    // animation
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