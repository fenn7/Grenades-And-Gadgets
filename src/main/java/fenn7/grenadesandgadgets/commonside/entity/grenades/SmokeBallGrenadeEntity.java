package fenn7.grenadesandgadgets.commonside.entity.grenades;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fenn7.grenadesandgadgets.client.GrenadesModClientUtil;
import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.SmokeBallGrenadeItem;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;

public class SmokeBallGrenadeEntity extends AbstractLingeringGrenadeEntity implements IAnimatable {
    private static final float SMOKE_RANGE = 2.8F;
    private static final int MAX_LINGERING_TICKS = 300;
    private static final int DEFAULT_COLOR = 0x696969;
    private static final GrenadesModSoundProfile SMOKEBALL_SOUND_PROFILE = new GrenadesModSoundProfile(SoundEvents.ENTITY_GENERIC_BURN, 1.35F, 0.8F);
    private List<Integer> colours;
    private List<BlockPos> smokeBlocks;

    public SmokeBallGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public SmokeBallGrenadeEntity(World world, PlayerEntity user) {
        super(GrenadesModEntities.SMOKE_BALL_GRENADE_ENTITY, world, user);
    }

    public SmokeBallGrenadeEntity(World world, double x, double y, double z) {
        super(GrenadesModEntities.SMOKE_BALL_GRENADE_ENTITY, world, x, y, z);
    }

    @Override
    protected void initialise() {
        this.maxLingeringTicks = MAX_LINGERING_TICKS;
        this.setPower(SMOKE_RANGE);
        this.setExplosionSoundProfile(SMOKEBALL_SOUND_PROFILE);
    }

    @Override
    public void tick() {
        if (this.state == LingeringState.LINGERING && this.lingeringTicks % 20 == 0) {
            var smokeBlocks = this.getOrCreateSmokeBlocks();
            if (this.world.isClient) {
                smokeBlocks.forEach(pos -> {
                        ParticleEffect smokeEffect = GrenadesModClientUtil.getDustParticleType(
                            this.getOrCreateColours().get(this.random.nextInt(this.getOrCreateColours().size()))
                        );
                        double xRand = this.random.nextDouble(0.25D, 0.75D);
                        double yRand = this.random.nextDouble(0.25D, 0.75D);
                        double zRand = this.random.nextDouble(0.25D, 0.75D);
                        this.world.addParticle(smokeEffect, pos.getX() + xRand, pos.getY() + yRand, pos.getZ() + zRand,
                            0, 0, 0);
                    }
                );
            } else {
                smokeBlocks.forEach(pos -> {
                        BlockState blockState = this.world.getBlockState(pos);
                        if (blockState.getProperties().contains(Properties.LIT)) {
                            this.world.setBlockState(pos, blockState.with(Properties.LIT, false), 11);
                        } else if (this.world.getBlockState(pos).isIn(BlockTags.FIRE)) {
                            this.world.removeBlock(pos, false);
                        }

                        this.world.getNonSpectatingEntities(Entity.class, new Box(pos)).forEach(entity -> {
                            entity.setOnFire(false);
                            if (entity instanceof LivingEntity alive) {
                                alive.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 50, 0));
                            }
                        });
                    }
                );
            }
        }
        super.tick();
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

    private List<Integer> getOrCreateColours() {
        if (this.colours != null) {
            return this.colours;
        } else {
            List<Integer> newColours = new ArrayList<>();
            NbtCompound colourNBT = this.getItem().getOrCreateSubNbt(SmokeBallGrenadeItem.SMOKE_BALL_COLOUR);
            int[] colours = (colourNBT.isEmpty()) ? new int[]{DEFAULT_COLOR} : colourNBT.getIntArray(SmokeBallGrenadeItem.COLOUR_SUB_TAG);
            Arrays.stream(colours).forEach(newColours::add);
            this.colours = newColours;
            return newColours;
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
