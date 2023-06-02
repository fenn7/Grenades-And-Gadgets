package fenn7.grenadesandgadgets.commonside.entity.grenades;

import java.util.List;

import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import fenn7.grenadesandgadgets.mixin.client.WorldRendererAccessorMixin;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.lwjgl.system.CallbackI;

public class RadiantGrenadeEntity extends AbstractGrenadeEntity {
    private static final float RADIANCE_RANGE = 4.0F;
    private static final ParticleEffect RADIANT_GRENADE_EFFECT = ParticleTypes.END_ROD;
    private static final GrenadesModSoundProfile RADIANT_SOUND_PROFILE = new GrenadesModSoundProfile(SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1.5F, 1.25F);

    private static final int MAX_RADIANCE_DURATION = 50;
    private static final int MAX_RADIANCE_LEVEL = 5;

    public RadiantGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public RadiantGrenadeEntity(World world, LivingEntity owner) {
        super(GrenadesModEntities.RADIANT_GRENADE_ENTITY, world, owner);
    }

    public RadiantGrenadeEntity(World world, double x, double y, double z) {
        super(GrenadesModEntities.RADIANT_GRENADE_ENTITY, world, x, y, z);
    }

    @Override
    protected void explode(float power) {
        BlockPos impactPos = this.getBlockPos();
        if (this.world.getBlockState(impactPos).isAir()) {
            this.world.setBlockState(impactPos, Blocks.LIGHT.getDefaultState());
            if (this.getOwner() instanceof PlayerEntity player) {
                player.sendMessage(GrenadesModUtil.textOf("§6Illuminated block at " + impactPos.getX() + ", " + impactPos.getY() + ", " + impactPos.getZ()), false);
            }
        }
        List<BlockPos> affectedBlocks = GrenadesModUtil.getBlocksInSphereAroundPos(this.getBlockPos(), this.power)
            .stream().filter(pos -> this.shouldSmokeAt(this.world, pos)).toList();
        affectedBlocks.forEach(pos -> {
            this.world.getNonSpectatingEntities(LivingEntity.class, new Box(pos)).forEach(entity -> {
                if ((entity instanceof PlayerEntity && this.canPlayerSeeThis()) || entity != null) {
                    Pair<Integer, Integer> parameters = this.getDurationAndAmplifier(this.distanceTo(entity));
                    entity.addStatusEffect(new StatusEffectInstance(GrenadesModStatus.RADIANT_LIGHT,parameters.getLeft(), parameters.getRight()));
                }
            });
        });
        this.discard();
    }

    private Pair<Integer, Integer> getDurationAndAmplifier(float distance) {
        float proportionalDistance = distance / this.power;
        return proportionalDistance <= 0.2F
            ? new Pair<>(MAX_RADIANCE_DURATION, MAX_RADIANCE_LEVEL)
            : new Pair<>(MAX_RADIANCE_LEVEL - (int) (proportionalDistance * 20), MAX_RADIANCE_LEVEL - (int) (proportionalDistance * 2));
    }

    private boolean canPlayerSeeThis() {
        WorldRenderer renderer = MinecraftClient.getInstance().worldRenderer;
        return ((WorldRendererAccessorMixin) renderer).getFrustum().isVisible(this.getBoundingBox());
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
    protected void initialise() {
        this.setPower(RADIANCE_RANGE);
        this.setExplosionEffect(RADIANT_GRENADE_EFFECT);
        this.setExplosionSoundProfile(RADIANT_SOUND_PROFILE);
    }

    @Override
    protected Item getDefaultItem() {
        return GrenadesModItems.GRENADE_RADIANT;
    }
}
