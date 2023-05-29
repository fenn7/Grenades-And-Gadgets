package fenn7.grenadesandgadgets.commonside.entity.projectiles;

import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class FragmentationGrenadeEntity extends AbstractGrenadeEntity {
    private static final float EXPLOSION_POWER = 0.75F;
    private static final GrenadesModSoundProfile FRAGMENTATION_SOUND_PROFILE = new GrenadesModSoundProfile(null, 1.0F, 1.0F);

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
    protected void explode(float power) {

    }

    @Override
    protected void initialise() {

    }

    @Override
    protected Item getDefaultItem() {
        return GrenadesModItems.GRENADE_FRAGMENTATION;
    }
}
