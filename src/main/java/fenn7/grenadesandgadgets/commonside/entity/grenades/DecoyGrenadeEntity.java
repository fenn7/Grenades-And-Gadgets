package fenn7.grenadesandgadgets.commonside.entity.grenades;

import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.entity.misc.DecoyEntity;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class DecoyGrenadeEntity extends AbstractGrenadeEntity {
    private static final float DECOY_RANGE = 4.0F;
    private static final float ABSORPTION_MULTIPLIER = 1.5F;
    private static final ParticleEffect DECOY_EFFECT = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.PLAYER_HEAD.getDefaultState());
    private static final GrenadesModSoundProfile DECOY_SOUND_PROFILE = new GrenadesModSoundProfile(SoundEvents.BLOCK_DISPENSER_DISPENSE, 2.0F, 1.5F);

    public DecoyGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public DecoyGrenadeEntity(World world, LivingEntity owner) {
        super(GrenadesModEntities.DECOY_GRENADE_ENTITY, world, owner);
    }

    public DecoyGrenadeEntity(World world, double x, double y, double z) {
        super(GrenadesModEntities.DECOY_GRENADE_ENTITY, world, x, y, z);
    }

    @Override
    protected void initialise() {
        this.setPower(DECOY_RANGE);
        this.setExplosionEffect(DECOY_EFFECT);
        this.setExplosionSoundProfile(DECOY_SOUND_PROFILE);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void explode(float power) {
        if (this.getOwner() instanceof PlayerEntity player) {
            DecoyEntity decoyEntity = new DecoyEntity(this.world, player, power);
            decoyEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).addPersistentModifier(
                new EntityAttributeModifier("bonus_health", power * ABSORPTION_MULTIPLIER, EntityAttributeModifier.Operation.ADDITION));
            decoyEntity.setHealth(decoyEntity.getMaxHealth());
            decoyEntity.setPosition(this.getPos());
            this.world.spawnEntity(decoyEntity);
        }
        this.discard();
    }

    @Override
    protected Item getDefaultItem() {
        return GrenadesModItems.GRENADE_DECOY;
    }
}
