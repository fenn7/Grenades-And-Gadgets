package fenn7.grenadesandgadgets.commonside.entity.grenades;

import static fenn7.grenadesandgadgets.commonside.item.custom.grenades.MagicGrenadeItem.EFFECTS;

import java.util.HashMap;
import java.util.List;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.MagicGrenadeItem;
import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class MagicGrenadeEntity extends AbstractGrenadeEntity {
    private static final int DURATION_PER_EFFECT = 160;
    private static final float MAGIC_RANGE = 3.0F;
    private static final ParticleEffect MAGIC_GRENADE_EFFECT = ParticleTypes.DRAGON_BREATH;
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
    protected void explode(float power) {
        var effectOccurenceMap = this.getItem().getItem() instanceof MagicGrenadeItem magic
            ? magic.getEffectOccurenceMap()
            : new HashMap<StatusEffectInstance, Integer>();
        this.getAffectedBlocksAtRange(this.power).forEach(pos ->
            this.world.getNonSpectatingEntities(LivingEntity.class, new Box(pos)).forEach(entity -> {
                effectOccurenceMap.forEach((effectInstance, occurence) -> {
                    var effectType = effectInstance.getEffectType();
                    if (effectType.isInstant()) {
                        double prox = 1.0D - (entity.distanceTo(this) / MAGIC_RANGE);
                        effectType.applyInstantEffect(this, this.getOwner(), entity, occurence, prox);
                    } else {
                        entity.addStatusEffect(new StatusEffectInstance(effectInstance.getEffectType(), effectOccurenceMap.size() * DURATION_PER_EFFECT, occurence));
                    }
                });
            })
        );
        this.discard();
    }

    @Override
    protected void initialise() {
        this.setPower(MAGIC_RANGE);
        this.setExplosionEffect(MAGIC_GRENADE_EFFECT);
        this.setExplosionSoundProfile(MAGIC_SOUND_PROFILE);
    }

    @Override
    protected Item getDefaultItem() {
        return GrenadesModItems.GRENADE_MAGIC;
    }
}
