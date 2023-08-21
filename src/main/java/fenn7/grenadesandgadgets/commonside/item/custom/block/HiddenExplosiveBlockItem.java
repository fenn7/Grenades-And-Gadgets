package fenn7.grenadesandgadgets.commonside.item.custom.block;

import java.util.List;

import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class HiddenExplosiveBlockItem extends BlockItem implements IAnimatable {
    public static final String DISGUISE_KEY = "disguise.key";
    private final AnimationFactory factory = GrenadesModUtil.getAnimationFactoryFor(this);

    public HiddenExplosiveBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        var disguiseKey = stack.getOrCreateNbt().getString(DISGUISE_KEY);
        if (!disguiseKey.isEmpty()) {
            tooltip.add(GrenadesModUtil.mutableTextOf("§lDisguise: ").append(GrenadesModUtil.translatableTextOf(disguiseKey)));
        }
    }

    @Override
    public void registerControllers(AnimationData animationData) {
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
