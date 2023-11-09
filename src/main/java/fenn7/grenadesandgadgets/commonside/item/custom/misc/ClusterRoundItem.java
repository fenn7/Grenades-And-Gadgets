package fenn7.grenadesandgadgets.commonside.item.custom.misc;

import java.util.List;

import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ClusterRoundItem extends Item {
    public static final String GRENADES_KEY = "grenades";

    public ClusterRoundItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        NbtCompound stackNbt = stack.getOrCreateNbt();
        if (stackNbt.contains(GRENADES_KEY) && stackNbt.get(GRENADES_KEY) instanceof NbtList) {
            NbtList nbtList = stackNbt.getList(GRENADES_KEY, 10);
            for (int i = 0; i < nbtList.size(); ++i) {
                ItemStack fragmentStack = ItemStack.fromNbt(nbtList.getCompound(i));
                tooltip.add(new TranslatableText(fragmentStack.getTranslationKey()));
            }
        }
    }
}
