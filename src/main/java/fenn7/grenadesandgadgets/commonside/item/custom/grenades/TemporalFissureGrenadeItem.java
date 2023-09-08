package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import java.util.List;

import fenn7.grenadesandgadgets.commonside.entity.grenades.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.TemporalFissureGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class TemporalFissureGrenadeItem extends AbstractGrenadeItem implements IAnimatable {
    public static final String NBT_DIMENSION_KEY = "dimension_key";
    public static final String ENTITY_SLOTS = "tfg_slots";
    private final AnimationFactory factory = GrenadesModUtil.getAnimationFactoryFor(this);

    public TemporalFissureGrenadeItem(Settings settings) {
        super(settings);
    }

    @Override
    public AbstractGrenadeEntity createGrenadeAt(World world, PlayerEntity player, ItemStack stack) {
        return new TemporalFissureGrenadeEntity(world, player);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        NbtCompound stackNbt = stack.getOrCreateNbt();
        if (stackNbt.contains(NBT_DIMENSION_KEY)) {
            String dimensionName;
            int dimKey = stackNbt.getInt(NBT_DIMENSION_KEY);
            switch (dimKey) {
                case -1 -> dimensionName = "§4Nether Fissure";
                case 0 -> dimensionName = "§2Overworld Fissure";
                case 1 -> dimensionName = "§5End Fissure";
                default -> dimensionName = "";
            }
            if (!dimensionName.isBlank()) {
                tooltip.add(GrenadesModUtil.textOf(dimensionName));
            }
        }
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.grenade_temporal_fissure.idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "controller",
            0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
