package fenn7.grenadesandgadgets.commonside.entity.grenades;

import java.util.HashSet;
import java.util.Set;

import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.entity.misc.TemporalFissureEntity;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.TemporalFissureGrenadeItem;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModEntityData;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TemporalFissureGrenadeEntity extends AbstractDisplacementGrenadeEntity {
    private static final float BASE_PORTAL_RANGE = 7.0F;
    private static final int MAX_DELAY_TICKS = 30;
    private static final int INTERVAL_BETWEEN_EFFECTS = 6;
    private static final ParticleEffect TEMPORAL_GRENADE_EFFECT = ParticleTypes.END_ROD;
    private static final GrenadesModSoundProfile TEMPORAL_GRENADE_SOUND_PROFILE = new GrenadesModSoundProfile(SoundEvents.ITEM_TRIDENT_THUNDER, 1.25F,  0.4F);

    public TemporalFissureGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public TemporalFissureGrenadeEntity(World world, PlayerEntity user) {
        super(GrenadesModEntities.TEMPORAL_FISSURE_GRENADE_ENTITY, world, user);
    }

    public TemporalFissureGrenadeEntity(World world, double x, double y, double z) {
        super(GrenadesModEntities.TEMPORAL_FISSURE_GRENADE_ENTITY, world, x, y, z);
    }

    @Override
    protected void initialise() {
        this.maxLingeringTicks = MAX_DELAY_TICKS;
        this.setPower(BASE_PORTAL_RANGE);
        this.setExplosionEffect(TEMPORAL_GRENADE_EFFECT);
        this.setExplosionSoundProfile(TEMPORAL_GRENADE_SOUND_PROFILE);
    }

    @Override
    protected void handleParticleEffects() {
        if (this.world.isClient && this.lingeringTicks % INTERVAL_BETWEEN_EFFECTS == 1) {
            Vec3d startPosHorizontal = new Vec3d(this.power - (float) (this.lingeringTicks / INTERVAL_BETWEEN_EFFECTS), 0, 0);
            Vec3d startPosVertical = startPosHorizontal.rotateY((float) Math.PI / 2F);
            Set<Vec3d> positions = new HashSet<>(Set.of(startPosVertical, startPosHorizontal));
            for (int i = 1; i < 9; ++i) {
                positions.add(startPosHorizontal.rotateY((float) Math.PI / 4F * i));
                positions.add(startPosHorizontal.rotateZ((float) Math.PI / 4F * i));
                positions.add(startPosVertical.rotateZ((float) Math.PI / 4F * i));
            }
            positions.forEach(pos -> {
                Vec3d spawnPos = this.getPos().add(pos);
                this.world.addParticle(TEMPORAL_GRENADE_EFFECT, spawnPos.x, spawnPos.y, spawnPos.z, 0.0D, 0.0D, 0.0D);
            });
        }
    }

    @Override
    protected void explode(float power) {
        super.explode(power);
        if (this.state == LingeringState.DISCARDED && !this.world.isClient) {
            int dimKey = this.getItem().getOrCreateNbt().getInt(TemporalFissureGrenadeItem.NBT_DIMENSION_KEY);
            TemporalFissureEntity entity = new TemporalFissureEntity(this.world, this.power, this.getOwner() instanceof PlayerEntity player ? player : null, dimKey);
            Vec3d newPos = this.getPos().subtract(0, 1, 0);
            entity.setPosition(newPos);
            entity.refreshPositionAndAngles(newPos.x, newPos.y, newPos.z, entity.getYaw(), entity.getPitch());
            this.world.spawnEntity(entity);
        }
    }

    @Override
    protected void handleDisplacement(Entity entity, BlockPos pos, Set<Entity> entities) {}

    @Override
    protected Item getDefaultItem() {
        return GrenadesModItems.GRENADE_TEMPORAL_FISSURE;
    }
}
