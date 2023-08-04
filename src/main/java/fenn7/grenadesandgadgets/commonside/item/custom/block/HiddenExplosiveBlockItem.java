package fenn7.grenadesandgadgets.commonside.item.custom.block;

import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class HiddenExplosiveBlockItem extends BlockItem implements IAnimatable {
    private final AnimationFactory factory = GrenadesModUtil.getAnimationFactoryFor(this);

    public HiddenExplosiveBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "controller", 0, this::spinPredicate));
    }

    private <E extends IAnimatable> PlayState spinPredicate(AnimationEvent<E> event) {
        //event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.model.spin", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
