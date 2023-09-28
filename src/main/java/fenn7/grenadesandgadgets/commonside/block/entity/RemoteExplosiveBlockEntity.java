package fenn7.grenadesandgadgets.commonside.block.entity;

import fenn7.grenadesandgadgets.commonside.block.GrenadesModBlockEntities;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class RemoteExplosiveBlockEntity extends BlockEntity implements IAnimatable {
    private final AnimationFactory factory = GrenadesModUtil.getAnimationFactoryFor(this);

    public RemoteExplosiveBlockEntity(BlockPos pos, BlockState state) {
        super(GrenadesModBlockEntities.REMOTE_EXPLOSIVE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "controller", 0, ae -> {
            ae.getController().setAnimation(new AnimationBuilder().addAnimation("animation.remote_bomb.idle", ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
