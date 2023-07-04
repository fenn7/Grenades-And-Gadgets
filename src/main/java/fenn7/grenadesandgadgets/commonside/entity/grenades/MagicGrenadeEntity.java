package fenn7.grenadesandgadgets.commonside.entity.grenades;

import static fenn7.grenadesandgadgets.commonside.item.custom.grenades.MagicGrenadeItem.EFFECTS;
import static fenn7.grenadesandgadgets.commonside.item.custom.grenades.MagicGrenadeItem.EFFECT_COUNT;
import static fenn7.grenadesandgadgets.commonside.item.custom.grenades.MagicGrenadeItem.EFFECT_TYPE;

import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class MagicGrenadeEntity extends AbstractGrenadeEntity {
    private static final int DURATION_PER_EFFECT = 160;
    private static final float MAGIC_RANGE = 3.0F;
    private static final ParticleEffect MAGIC_GRENADE_EFFECT = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.LAPIS_BLOCK.getDefaultState());
    private static final GrenadesModSoundProfile MAGIC_SOUND_PROFILE = new GrenadesModSoundProfile(SoundEvents.ENTITY_SPLASH_POTION_BREAK, 1.25F, 0.5F);

    public MagicGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public MagicGrenadeEntity(World world, LivingEntity owner) {
        super(GrenadesModEntities.MAGIC_GRENADE_ENTITY, world, owner);
    }

    public MagicGrenadeEntity(World world, double x, double y, double z) {
        super(GrenadesModEntities.MAGIC_GRENADE_ENTITY, world, x, y, z);
    }

    @Override
    protected void initialise() {
        this.setPower(MAGIC_RANGE);
        this.setExplosionEffect(MAGIC_GRENADE_EFFECT);
        this.setExplosionSoundProfile(MAGIC_SOUND_PROFILE);
    }

    @Override
    protected void explode(float power) {
        NbtCompound stackNbt = this.getItem().getOrCreateNbt();
        if (stackNbt.contains(EFFECTS) && stackNbt.get(EFFECTS) instanceof NbtList) {
            this.getLivingEntitiesFromBlocks(this.getAffectedBlocksAtRange(power)).forEach(entity -> {
                NbtList effectNbtList = stackNbt.getList(EFFECTS, 10);
                effectNbtList.forEach(effectNbt -> {
                    if (effectNbt instanceof NbtCompound effectNbtCompound && effectNbtCompound.contains(EFFECT_TYPE) && effectNbtCompound.contains(EFFECT_COUNT)) {
                        var effectType = StatusEffect.byRawId(effectNbtCompound.getInt(EFFECT_TYPE));
                        int effectCount = effectNbtCompound.getInt(EFFECT_COUNT) - 1;
                        if (effectType != null && effectType.isInstant()) {
                            double proximity = 1.0D - this.proportionalDistanceTo(entity);
                            effectType.applyInstantEffect(this, this.getOwner(), entity, effectCount, proximity);
                        } else {
                            entity.addStatusEffect(new StatusEffectInstance(effectType, effectCount * DURATION_PER_EFFECT, effectCount));
                        }
                    }
                });
            });
        }
        this.discard();
    }

    @Override
    protected Item getDefaultItem() {
        return GrenadesModItems.GRENADE_MAGIC;
    }
}
