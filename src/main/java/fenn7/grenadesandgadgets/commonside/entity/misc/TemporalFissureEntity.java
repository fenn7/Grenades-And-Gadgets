package fenn7.grenadesandgadgets.commonside.entity.misc;

import java.util.Set;

import fenn7.grenadesandgadgets.commonside.damage.GrenadesModDamageSources;
import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
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
    private static final float DISPLACEMENT_DAMAGE = 10.0F;
    private static final float MAX_DISPLACEMENT_THRESHOLD = 100.0F;
    private static final float MAX_CATASTROPHIC_DAMAGE = 70.0F;
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

    public TemporalFissureEntity(World world, float range, Entity summoner) {
        super(GrenadesModEntities.TEMPORAL_FISSURE, world);
        this.range = range;
        this.summoner = summoner;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age > MAX_LIFE_BASE + (this.range * EXTRA_LIFE_PER_RANGE)) {
            this.remove(RemovalReason.CHANGED_DIMENSION);
        }
        var affectedBlocks = this.getOrCreateAffectedBlocks();
        affectedBlocks.forEach(
            pos -> {}
        );
        // suck in entities with particle effect
        // overwrite blocks
        // decelerate entities
        // expand to 3x3 size
        Box collisionBox = this.getDimensions(this.getPose()).getBoxAt(this.getPos());
        this.world.getNonSpectatingEntities(Entity.class, collisionBox).forEach(
            entity -> entity.damage(GrenadesModDamageSources.DIMENSIONAL_DISPLACEMENT, DISPLACEMENT_DAMAGE)
        );
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
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
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
