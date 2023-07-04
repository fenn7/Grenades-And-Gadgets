package fenn7.grenadesandgadgets.commonside.entity.grenades;

import static fenn7.grenadesandgadgets.commonside.item.custom.grenades.FragmentationGrenadeItem.FRAGMENTS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.entity.misc.FragmentEntity;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class FragmentationGrenadeEntity extends AbstractGrenadeEntity {
    private static final float EXPLOSION_POWER = 0.75F;
    private static final float TRACKING_RANGE = 10.0F;
    private static final int FRAGMENTS_PER_MATERIAL = 3;
    private static final ParticleEffect FRAGMENTATION_EFFECT = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.STONE.getDefaultState());
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
        if (!this.world.isClient) {
            this.world.createExplosion(null, this.getX(), this.getY(), this.getZ(), power, Explosion.DestructionType.NONE);
            NbtCompound nbt = this.getItem().getOrCreateNbt();
            if (nbt.contains(FRAGMENTS) && nbt.get(FRAGMENTS) instanceof NbtList) {
                NbtList nbtList = nbt.getList(FRAGMENTS, 10);
                List<LivingEntity> nearbyEntities =
                    GrenadesModUtil.getLivingEntitiesAtRangeFromEntity(this.world, this, TRACKING_RANGE).stream()
                        .filter(entity -> entity.canSee(this) && !entity.equals(this.getOwner()))
                        .toList();
                GrenadesMod.LOGGER.warn(nearbyEntities.toString());
                List<FragmentEntity> fragmentList = new ArrayList<>();
                for (int i = 0; i < nbtList.size(); ++i) {
                    for (int j = 0; j < FRAGMENTS_PER_MATERIAL; ++j) {
                        ItemStack fragmentStack = ItemStack.fromNbt(nbtList.getCompound(i));
                        FragmentEntity fragment = new FragmentEntity(this.world, fragmentStack);
                        fragment.setPos(this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 0.5, this.getBlockPos().getZ() + 0.5);
                        fragment.setOwner(this.getOwner());
                        fragment.setNoGravity(true);
                        fragmentList.add(fragment);
                    }
                }
                Collections.shuffle(fragmentList);
                for (int k = 0; k < fragmentList.size(); ++k) {
                    FragmentEntity fragment = fragmentList.get(k);
                    fragment.setVelocity(k < nearbyEntities.size()
                        ? nearbyEntities.get(k).getPos().subtract(fragment.getPos()).normalize()
                        : this.getRandomFragmentVelocity(fragment.getPos(), k % 2 == 0));
                    GrenadesMod.LOGGER.warn("SPAWNING FRAGMENT " + fragment + " WITH " + fragment.getFragmentItem()
                    + " TARGETING " + (k < nearbyEntities.size() ? nearbyEntities.get(k) : "RANDOM"));
                    this.world.spawnEntity(fragment);
                }
            }
        }
        this.discard();
    }

    private Vec3d getRandomFragmentVelocity(Vec3d fragmentPos, boolean above2Pi) {
        return fragmentPos.add(1, 0, 0).rotateY((float) (above2Pi ? this.random.nextDouble(Math.PI, Math.PI * 2) : this.random.nextDouble(Math.PI)))
            .subtract(fragmentPos).normalize();
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
