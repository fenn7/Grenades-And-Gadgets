package fenn7.grenadesandgadgets.commonside.entity.grenades;

import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class FireGrenadeEntity extends AbstractLingeringGrenadeEntity {
    private static final float FIRE_RANGE = 2.8F;
    private static final float MAX_IMPACT_DAMAGE = 4.0F;
    private static final float MAX_DAMAGE_PROPORTION_RANGE = 0.4F;
    private static final int MAX_LINGERING_TICKS = 10;
    private static final ParticleEffect FIRE_GRENADE_EFFECT = ParticleTypes.LAVA;
    private static final GrenadesModSoundProfile FIRE_GRENADE_SOUND_PROFILE = new GrenadesModSoundProfile(SoundEvents.ENTITY_BLAZE_SHOOT, 1.5F, 0.675F);

    public FireGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public FireGrenadeEntity(World world, PlayerEntity user) {
        super(GrenadesModEntities.FIRE_GRENADE_ENTITY, world, user);
    }

    public FireGrenadeEntity(World world, double x, double y, double z) {
        super(GrenadesModEntities.FIRE_GRENADE_ENTITY, world, x, y, z);
    }

    @Override
    protected void initialise() {
        this.maxLingeringTicks = MAX_LINGERING_TICKS;
        this.setPower(FIRE_RANGE);
        this.setExplosionEffect(FIRE_GRENADE_EFFECT);
        this.setExplosionSoundProfile(FIRE_GRENADE_SOUND_PROFILE);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (!this.world.isClient()) {
            this.explodeWithEffects(this.power * 0.66F);
        }
        super.onCollision(hitResult);
    }

    @Override
    protected void handleDiscard() {
        this.explode(this.power);
        super.handleDiscard();
    }

    @Override
    protected Item getDefaultItem() {
        return GrenadesModItems.GRENADE_FIRE;
    }

    @Override
    protected void explode(float power) {
        super.explode(power);
        this.getAffectedBlocksAtRange(power).forEach(pos -> {
            this.world.getNonSpectatingEntities(LivingEntity.class, new Box(pos)).forEach(entity -> {
                if (!entity.isFireImmune() && this.state != LingeringState.DISCARDED) {
                    entity.damage(DamageSource.LAVA, this.handleImpactDamage(entity));
                }
            });
            if (AbstractFireBlock.canPlaceAt(this.world, pos, this.getMovementDirection())) {
                BlockState fireState = AbstractFireBlock.getState(this.world, pos.offset(this.getMovementDirection()));
                this.world.setBlockState(pos, fireState, 11);
            }
        });
    }

    private float handleImpactDamage(LivingEntity entity) {
        float damage = this.blockDistanceTo(entity.getBlockPos()) <= MAX_DAMAGE_PROPORTION_RANGE ? MAX_IMPACT_DAMAGE : MAX_IMPACT_DAMAGE / 2;
        return entity.hasStatusEffect(GrenadesModStatus.FROZEN) ? damage * 2 : damage;
    }
}
