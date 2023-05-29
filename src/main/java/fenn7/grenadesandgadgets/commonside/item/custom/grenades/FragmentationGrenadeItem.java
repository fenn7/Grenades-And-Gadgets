package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import java.util.List;

import fenn7.grenadesandgadgets.commonside.entity.projectiles.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.projectiles.FragmentationGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.projectiles.GrenadeEntity;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FragmentationGrenadeItem extends AbstractGrenadeItem {
    public static final String FRAGMENTS = "Fragments";

    public FragmentationGrenadeItem(Settings settings) {
        super(settings);
        this.defaultSpeed = 0.9F;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        NbtCompound stackNbt = stack.getOrCreateNbt();
        if (stackNbt.contains(FRAGMENTS) && stackNbt.get(FRAGMENTS) instanceof NbtList) {
            tooltip.add(GrenadesModUtil.textOf("§l" + "Fragments:"));
            NbtList nbtList = stackNbt.getList(FRAGMENTS, 10);
            for (int i = 0; i < nbtList.size(); ++i) {
                ItemStack fragmentStack = ItemStack.fromNbt(nbtList.getCompound(i));
                tooltip.add(new TranslatableText(fragmentStack.getTranslationKey()));
            }
        }
    }

    @Override
    public AbstractGrenadeEntity createGrenadeAt(World world, PlayerEntity player, Hand hand) {
        return new FragmentationGrenadeEntity(world, player);
    }
}
