package fenn7.grenadesandgadgets.commonside.entity.misc;

import java.util.HashMap;
import java.util.Map;

import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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

public class FragmentEntity extends ThrownItemEntity implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);
    private static final Map<Item, Pair<Float, StatusEffectInstance>> FRAGMENT_EFFECTS = new HashMap<>();
    static {
        FRAGMENT_EFFECTS.put(Items.COPPER_INGOT, new Pair<>(3.0F, null));
        FRAGMENT_EFFECTS.put(Items.GOLD_INGOT, new Pair<>(2.0F, null));
    }

    public FragmentEntity(World world, ItemStack fragmentStack) {
        super(GrenadesModEntities.FRAGMENTATION_GRENADE_ENTITY, world);
        this.setItem(fragmentStack);
    }

    public FragmentEntity(EntityType<FragmentEntity> fragmentEntityEntityType, World world) {
        super(fragmentEntityEntityType, world);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity target = entityHitResult.getEntity();
        super.onEntityHit(entityHitResult);
    }

    @Override
    protected Item getDefaultItem() {
        return this.getItem() == null ? Items.IRON_INGOT : this.getItem().getItem();
    }

    public Item getFragmentItem() {
        return this.getDefaultItem();
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
}
