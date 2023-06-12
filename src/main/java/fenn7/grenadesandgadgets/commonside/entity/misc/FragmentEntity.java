package fenn7.grenadesandgadgets.commonside.entity.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class FragmentEntity extends ThrownItemEntity implements IAnimatable, FlyingItemEntity {
    private final AnimationFactory factory = new AnimationFactory(this);
    private static final int MAX_AGE = 200;
    private static final Map<Item, Pair<Float, StatusEffectInstance>> FRAGMENT_EFFECTS = new HashMap<>();

    public FragmentEntity(World world, ItemStack fragmentStack) {
        super(GrenadesModEntities.FRAGMENT_ENTITY, world);
        this.setItem(fragmentStack);
    }

    public FragmentEntity(EntityType<FragmentEntity> fragmentEntityEntityType, World world) {
        super(fragmentEntityEntityType, world);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity target = entityHitResult.getEntity();
        GrenadesMod.LOGGER.warn(target.toString());
        var dmg = FRAGMENT_EFFECTS.getOrDefault(this.getFragmentItem(), new Pair<>(2.0F, null)).getLeft();
        GrenadesMod.LOGGER.warn(dmg.toString());
        GrenadesMod.LOGGER.warn(FRAGMENT_EFFECTS.toString());
        target.damage(DamageSource.thrownProjectile(this, this.getOwner()),
            FRAGMENT_EFFECTS.getOrDefault(this.getFragmentItem(), new Pair<>(2.0F, null)).getLeft());
        super.onEntityHit(entityHitResult);
    }

    @Override
    protected Item getDefaultItem() {
        return this.getItem() == null ? Items.IRON_INGOT : this.getItem().getItem();
    }

    public Item getFragmentItem() {
        return this.getDefaultItem();
    }

    @Override
    public void tick() {
        if (this.age >= MAX_AGE) {
            this.discard();
        }
        super.tick();
    }

    protected <E extends IAnimatable> PlayState flyingAnimation(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.model.spin", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "controller", 0, this::flyingAnimation));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    static {
        FRAGMENT_EFFECTS.put(Items.GOLD_INGOT, new Pair<>(2.0F, null));
        FRAGMENT_EFFECTS.put(Items.COPPER_INGOT, new Pair<>(3.0F, null));
        FRAGMENT_EFFECTS.put(Items.IRON_INGOT, new Pair<>(5.0F, null));
        FRAGMENT_EFFECTS.put(Items.AMETHYST_SHARD, new Pair<>(6.0F, null));
        FRAGMENT_EFFECTS.put(Items.PRISMARINE_SHARD, new Pair<>(6.0F, null));
        FRAGMENT_EFFECTS.put(Items.DIAMOND, new Pair<>(8.0F, null));
        FRAGMENT_EFFECTS.put(Items.OBSIDIAN, new Pair<>(8.0F, null));
        FRAGMENT_EFFECTS.put(Items.MAGMA_BLOCK, new Pair<>(7.0F, null));
        FRAGMENT_EFFECTS.put(Items.NETHERITE_SCRAP, new Pair<>(11.0F, null));
        FRAGMENT_EFFECTS.put(Items.SHULKER_SHELL, new Pair<>(9.0F, null));
    }
}
