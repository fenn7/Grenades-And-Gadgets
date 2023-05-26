package fenn7.grenadesandgadgets.commonside.entity.projectiles;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;

public class SmokeBallGrenadeEntity extends AbstractLingeringGrenadeEntity implements IAnimatable {
    private static final float SMOKE_RANGE = 2.8F;
    private static final int MAX_LINGERING_TICKS = 180;
    private static final GrenadesModSoundProfile SMOKE_GRENADE_SOUND_PROFILE = new GrenadesModSoundProfile(SoundEvents.ENTITY_GENERIC_BURN, 1.35F, 0.8F);
    private List<BlockPos> smokeBlocks;

    public SmokeBallGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public SmokeBallGrenadeEntity(World world, PlayerEntity user) {
        super(GrenadesModEntities.SMOKE_GRENADE_ENTITY, world, user);
    }

    public SmokeBallGrenadeEntity(World world, double x, double y, double z) {
        super(GrenadesModEntities.SMOKE_GRENADE_ENTITY, world, x, y, z);
    }

    @Override
    protected void initialise() {
        this.maxLingeringTicks = MAX_LINGERING_TICKS;
        this.setPower(SMOKE_RANGE);
        this.setExplosionSoundProfile(SMOKE_GRENADE_SOUND_PROFILE);
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 33) {
            Box smokeBox = new Box(this.getBlockPos()).expand(this.power, this.power, this.power);
            Stream<BlockPos> posStream = BlockPos.stream(smokeBox);
            posStream.filter(pos -> Math.sqrt(pos.getSquaredDistance(this.getPos())) <= this.power)
                    .filter(pos -> this.world.getBlockState(pos).isAir())
                    .forEach(pos -> {
                        ParticleEffect smoke1 = ParticleTypes.CAMPFIRE_COSY_SMOKE;
                        ParticleEffect smoke2 = ParticleTypes.CAMPFIRE_SIGNAL_SMOKE;
                        double x = ThreadLocalRandom.current().nextDouble(-0.5D, 0.5D);
                        double z = ThreadLocalRandom.current().nextDouble(-0.5D, 0.5D);
                        this.world.addParticle(smoke1, pos.getX() - x, pos.getY() + 0.4, pos.getZ() - z, 0, 0.01, 0);
                        this.world.addParticle(smoke2, pos.getX() + x, pos.getY() - 0.4, pos.getZ() + z, 0, 0.01, 0);
                    });

            List<LivingEntity> list = world.getNonSpectatingEntities(LivingEntity.class, smokeBox);
            list.stream().filter(e -> Math.sqrt(e.squaredDistanceTo(this.getX(), this.getY(), this.getZ())) <= this.power)
                    .forEach(e -> {
                        if (e.isOnFire()) {
                            e.setOnFire(false);
                        }
                    });
        } else {
            super.handleStatus(status);
        }
    }

    @Override
    public void tick() {
        if (this.state == LingeringState.LINGERING) {
            if (this.lingeringTicks >= 0 && this.lingeringTicks % 5 == 0) {
                if (this.world.isClient) {
                    this.world.addParticle(ParticleTypes.ANGRY_VILLAGER, this.getX(), this.getY() + 0.4, this.getZ(), 0, 0.01, 0);
                }
                /*this.world.sendEntityStatus(this, (byte) 33);*/
            }
        }
        super.tick();
    }

    @Override
    protected void explode(float power) {
        super.explode(power);
        this.getOrCreateSmokeBlocks().forEach(pos -> {
                BlockState blockState = this.world.getBlockState(pos);
                if (blockState.getProperties().contains(Properties.LIT)) {
                    this.world.setBlockState(pos, blockState.with(Properties.LIT, false), 11);
                } else if (this.world.getBlockState(pos).isIn(BlockTags.FIRE)) {
                    this.world.removeBlock(pos, false);
                }
            }
        );
    }

    private List<BlockPos> getOrCreateSmokeBlocks() {
        if (this.smokeBlocks != null) {
            return this.smokeBlocks;
        } else {
            List<BlockPos> newSmokeBlocks = GrenadesModUtil.getBlocksInSphereAroundPos(this.getBlockPos(), this.power)
                .stream().filter(pos -> this.shouldSmokeAt(this.world, pos)).toList();
            this.smokeBlocks = newSmokeBlocks;
            return newSmokeBlocks;
        }
    }

    private boolean shouldSmokeAt(World world, BlockPos firePos) {
        for (double x = Math.min(firePos.getX(), this.getX()); x <= Math.max(firePos.getX(), this.getX()); x++) {
            for (double y = Math.min(firePos.getY(), this.getY()); y <= Math.max(firePos.getY(), this.getY()); y++) {
                for (double z = Math.min(firePos.getZ(), this.getZ()); z <= Math.max(firePos.getZ(), this.getZ()); z++) {
                    BlockState between = world.getBlockState(new BlockPos(x, y, z));
                    if (between != null && between.getMaterial().isSolid()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected Item getDefaultItem() {
        return GrenadesModItems.GRENADE_SMOKE_BALL;
    }
}
