package fenn7.grenadesandgadgets.commonside.entity.grenades;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import fenn7.grenadesandgadgets.client.GrenadesModClientUtil;
import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class UpheavalGrenadeEntity extends AbstractDisplacementGrenadeEntity {
    private static final float UPHEAVAL_RANGE = 3.5F;
    private static final int MAX_DELAY_TICKS = 10;
    private static final int MAX_UPHEAVAL_ROUNDS = 5;
    private static final int NAUSEA_DURATION = 120;
    private static final float MIN_DISPLACEMENT_HEIGHT = 5.0F;
    private static final float BASE_DISPLACEMENT_HEIGHT = 16.0F;
    private static final ParticleEffect UPHEAVAL_EFFECT = GrenadesModClientUtil.getDustParticleType(0, 1.5F);
    private static final GrenadesModSoundProfile UPHEAVAL_SOUND_PROFILE = new GrenadesModSoundProfile(SoundEvents.BLOCK_PISTON_EXTEND, 2.0F, 0.5F);

    public UpheavalGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public UpheavalGrenadeEntity(World world, PlayerEntity user) {
        super(GrenadesModEntities.UPHEAVAL_GRENADE_ENTITY, world, user);
    }

    public UpheavalGrenadeEntity(World world, double x, double y, double z) {
        super(GrenadesModEntities.UPHEAVAL_GRENADE_ENTITY, world, x, y, z);
    }

    @Override
    protected void initialise() {
        this.maxLingeringTicks = MAX_DELAY_TICKS;
        this.setPower(UPHEAVAL_RANGE);
        this.setExplosionEffect(UPHEAVAL_EFFECT);
        this.setExplosionSoundProfile(UPHEAVAL_SOUND_PROFILE);
    }

    @Override
    protected void handleParticleEffects() {
        int interval = MAX_DELAY_TICKS / MAX_UPHEAVAL_ROUNDS;
        int remainder = this.lingeringTicks % interval;
        if (this.lingeringTicks > 0 && remainder == (interval - 1)) {
            Vec3d pos = new Vec3d(this.power * this.lingeringTicks / MAX_DELAY_TICKS, 0, 0);
            for (int i = 0; i < 8; ++i) {
                Vec3d spawnPos = this.getPos().add(pos.rotateY((float) (Math.PI * i / 4.0D)));
                this.world.addParticle(UPHEAVAL_EFFECT, spawnPos.x, spawnPos.y, spawnPos.z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected void handleDisplacement(LivingEntity entity, BlockPos pos, List<LivingEntity> entities) {
        double distanceFrom = this.blockDistanceTo(entity.getBlockPos());
        double displacement = Math.max(MIN_DISPLACEMENT_HEIGHT, 1 / distanceFrom * BASE_DISPLACEMENT_HEIGHT + entities.size());
        entity.move(MovementType.SELF, new Vec3d(0, displacement, 0));
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, NAUSEA_DURATION));
    }

    protected Item getDefaultItem() {
        return GrenadesModItems.GRENADE_UPHEAVAL;
    }
}
