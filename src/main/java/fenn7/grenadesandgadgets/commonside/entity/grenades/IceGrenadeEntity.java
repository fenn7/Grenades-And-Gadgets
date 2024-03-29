package fenn7.grenadesandgadgets.commonside.entity.grenades;

import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class IceGrenadeEntity extends AbstractLingeringGrenadeEntity {
    private static final float ICE_RANGE = 2.8F;
    private static final float PERMAFROST_THRESHOLD = 4.0F;
    private static final float MAX_IMPACT_DAMAGE = 4.0F;
    private static final float MAX_DAMAGE_PROPORTION_RANGE = 0.4F;
    private static final int MAX_DELAY_TICKS = 10;
    private static final int FROZEN_DURATION = 80;
    private static final ParticleEffect ICE_GRENADE_EFFECT = ParticleTypes.SNOWFLAKE;
    private static final GrenadesModSoundProfile ICE_GRENADE_SOUND_PROFILE = new GrenadesModSoundProfile(SoundEvents.ENTITY_PLAYER_HURT_FREEZE, 1.25F, 1.0F);

    public IceGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public IceGrenadeEntity(World world, PlayerEntity user) {
        super(GrenadesModEntities.ICE_GRENADE_ENTITY, world, user);
    }

    public IceGrenadeEntity(World world, double x, double y, double z) {
        super(GrenadesModEntities.ICE_GRENADE_ENTITY, world, x, y, z);
    }

    @Override
    protected void initialise() {
        this.maxLingeringTicks = MAX_DELAY_TICKS;
        this.setPower(ICE_RANGE);
        this.setExplosionEffect(ICE_GRENADE_EFFECT);
        this.setExplosionSoundProfile(ICE_GRENADE_SOUND_PROFILE);
    }

    @Override
    public void tick() {
        if (this.state == LingeringState.LINGERING && this.world.isClient) {
            double randomX = this.random.nextDouble(-0.25D, 0.25D);
            double randomY = this.random.nextDouble(-0.1D, 0.1D);
            double randomZ = this.random.nextDouble(-0.25D, 0.25D);
            this.world.addParticle(ICE_GRENADE_EFFECT, this.getX(), this.getY(), this.getZ(), randomX, randomY, randomZ);
        }
        super.tick();
    }

    @Override
    protected void handleDiscard() {
        this.explode();
        super.handleDiscard();
    }

    @Override
    protected void explode() {
        super.explode();
        if (this.state == LingeringState.DISCARDED) {
            this.getAffectedBlocksAtRange(this.getPower()).forEach(pos -> {
                this.world.getNonSpectatingEntities(LivingEntity.class, new Box(pos)).forEach(entity -> {
                    if (entity.canFreeze()) {
                        entity.damage(DamageSource.FREEZE, this.handleImpactDamage(entity));
                        if (!entity.hasStatusEffect(GrenadesModStatus.FROZEN)) {
                            GrenadesModUtil.addEffectServerAndClient(entity, new StatusEffectInstance(GrenadesModStatus.FROZEN, FROZEN_DURATION,
                                Math.min(4, (int) Math.floor(this.getPower()))));
                        }
                    }
                });
                if (this.world.getBlockState(pos).isOf(Blocks.WATER)) {
                    this.world.setBlockState(pos, this.getPower() <= PERMAFROST_THRESHOLD ? Blocks.FROSTED_ICE.getDefaultState() : Blocks.ICE.getDefaultState());
                } else if (this.world.getBlockState(pos).isOf(Blocks.WATER_CAULDRON)) {
                    this.world.setBlockState(pos, Blocks.POWDER_SNOW_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, Math.round(this.getPower()) + 1));
                } else if (Blocks.SNOW.getDefaultState().canPlaceAt(this.world, pos)) {
                    this.world.setBlockState(pos, Blocks.SNOW.getStateManager().getDefaultState().with(SnowBlock.LAYERS, this.random.nextInt(1, Math.round(this.getPower()) + 1)));
                }
            });
        }
    }

    private float handleImpactDamage(LivingEntity entity) {
        float damage = this.blockDistanceTo(entity.getBlockPos()) <= MAX_DAMAGE_PROPORTION_RANGE ? MAX_IMPACT_DAMAGE : MAX_IMPACT_DAMAGE / 2;
        if (entity.isOnFire()) {
            entity.extinguish();
            damage *= 2.0F;
        }
        return damage;
    }

    @Override
    protected Item getDefaultItem() {
        return GrenadesModItems.GRENADE_ICE;
    }
}
