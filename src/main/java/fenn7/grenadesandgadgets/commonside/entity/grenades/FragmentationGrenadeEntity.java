package fenn7.grenadesandgadgets.commonside.entity.grenades;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class FragmentationGrenadeEntity extends AbstractGrenadeEntity {
    private static final float EXPLOSION_POWER = 0.75F;
    private static final ParticleEffect FRAGMENTATION_EFFECT = ParticleTypes.LARGE_SMOKE;
    private static final GrenadesModSoundProfile FRAGMENTATION_SOUND_PROFILE = new GrenadesModSoundProfile(SoundEvents.BLOCK_DISPENSER_LAUNCH, 0.5F, 0.4F);

    public FragmentationGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public FragmentationGrenadeEntity(World world, LivingEntity owner) {
        super(GrenadesModEntities.FRAGMENTATION_GRENADE_ENTITY, world, owner);
    }

    public FragmentationGrenadeEntity(World world, double x, double y, double z) {
        super(GrenadesModEntities.FRAGMENTATION_GRENADE_ENTITY, world, x, y, z);
    }

    @Override
    protected void initialise() {
        this.setPower(EXPLOSION_POWER);
        this.setExplosionEffect(FRAGMENTATION_EFFECT);
        this.setExplosionSoundProfile(FRAGMENTATION_SOUND_PROFILE);
    }


    @Override
    protected void explode(float power) {
        GrenadesMod.LOGGER.warn(this.getItem().getOrCreateNbt().toString());
        this.discard();
    }

    @Override
    protected <E extends IAnimatable> PlayState flyingAnimation(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.grenade.flying_large", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    protected Item getDefaultItem() {
        return GrenadesModItems.GRENADE_FRAGMENTATION;
    }
}
