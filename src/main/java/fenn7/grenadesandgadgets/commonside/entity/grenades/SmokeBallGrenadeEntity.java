package fenn7.grenadesandgadgets.commonside.entity.grenades;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import fenn7.grenadesandgadgets.client.GrenadesModClientUtil;
import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.SmokeBallGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModEntityData;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
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
import net.minecraft.nbt.NbtInt;
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
    private Set<BlockPos> smokeBlocks;

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
        if (this.state == LingeringState.LINGERING && this.lingeringTicks % 10 == 0) {
            Set<BlockPos> smokeBlocks = this.getOrCreateSmokeBlocks();
            if (this.world.isClient) {
                smokeBlocks.forEach(pos -> {
                    ParticleEffect smokeEffect = GrenadesModClientUtil.getMaxSizeDustParticleType(
                        this.getOrCreateColours().get(this.random.nextInt(this.getOrCreateColours().size()))
                    );
                    double xRand = this.random.nextDouble(0.3D, 0.7D);
                    double yRand = this.random.nextDouble(0.3D, 0.7D);
                    double zRand = this.random.nextDouble(0.3D, 0.7D);
                    this.world.addParticle(smokeEffect, pos.getX() + xRand, pos.getY() + yRand, pos.getZ() + zRand,
                        0, 0, 0);
                    this.world.addParticle(smokeEffect, pos.getX() - xRand, pos.getY() - yRand, pos.getZ() - zRand,
                        0, 0, 0);
                    }
                );
            }
            smokeBlocks.forEach(pos -> {
                BlockState blockState = this.world.getBlockState(pos);
                if (blockState.getProperties().contains(Properties.LIT)) {
                    this.world.setBlockState(pos, blockState.with(Properties.LIT, false), 11);
                } else if (this.world.getBlockState(pos).isIn(BlockTags.FIRE)) {
                    this.world.removeBlock(pos, false);
                }
            });
            this.getLivingEntitiesFromBlocks(smokeBlocks).forEach(entity -> {
                entity.extinguish();
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 50, 0));
            });
        }
        super.tick();
    }

    private Set<BlockPos> getOrCreateSmokeBlocks() {
        if (this.smokeBlocks != null && !this.isStuckToEntity()) {
            return this.smokeBlocks;
        } else {
            Set<BlockPos> newSmokeBlocks = this.getAffectedBlocksAtRange(this.power);
            this.smokeBlocks = newSmokeBlocks;
            return newSmokeBlocks;
        }
    }

    private boolean isStuckToEntity() {
        return this.getModifierName().equals(GrenadeModifierRecipe.STICKY) && ((GrenadesModEntityData) this).getPersistentData().get(STICK_TARGET) instanceof NbtInt;
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

    @Override
    protected Item getDefaultItem() {
        return GrenadesModItems.GRENADE_SMOKE_BALL;
    }
}
