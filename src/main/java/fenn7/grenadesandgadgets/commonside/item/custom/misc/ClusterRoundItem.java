package fenn7.grenadesandgadgets.commonside.item.custom.misc;

import java.util.List;

import fenn7.grenadesandgadgets.commonside.item.custom.grenades.AbstractGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public class ClusterRoundItem extends Item {
    public static final String GRENADES_KEY = "grenades";
    private static final int MAX_GRENADE_AGE = 30;

    public ClusterRoundItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        NbtList nbtList = this.getNbtList(stack);
        if (!nbtList.isEmpty()) {
            for (int i = 0; i < nbtList.size(); ++i) {
                ItemStack grenadeStack = ItemStack.fromNbt(nbtList.getCompound(i));
                tooltip.add(new TranslatableText(grenadeStack.getTranslationKey()));
                String modifier = grenadeStack.getOrCreateNbt().getString(GrenadeModifierRecipe.MODIFIER_KEY);
                if (!modifier.isBlank()) {
                    tooltip.add(GrenadesModUtil.textOf("   " + ("§n§6" + modifier)));
                }
            }
        }
    }

    private NbtList getNbtList(ItemStack stack) {
        NbtCompound stackNbt = stack.getOrCreateNbt();
        if (stackNbt.contains(GRENADES_KEY) && stackNbt.get(GRENADES_KEY) instanceof NbtList) {
            return stackNbt.getList(GRENADES_KEY, 10);
        }
        return new NbtList();
    }

    public void explodeAtLocation(ItemStack clusterStack, Vec3d pos, PlayerEntity player) {
        NbtList nbtList = this.getNbtList(clusterStack);
        if (!nbtList.isEmpty()) {
            player.world.createExplosion(player, pos.x, pos.y, pos.z, 2F + (nbtList.size() * 0.5F), false, Explosion.DestructionType.DESTROY);
            for (int i = 0; i < nbtList.size(); ++i) {
                ItemStack grenadeStack = ItemStack.fromNbt(nbtList.getCompound(i));
                if (grenadeStack.getItem() instanceof AbstractGrenadeItem g) {
                    var grenade = g.createGrenadeAt(player.world, player, grenadeStack);
                    AbstractGrenadeItem.addNbtModifier(grenadeStack, grenade);
                    grenade.setItem(grenadeStack);
                    grenade.setMaxAgeTicks(MAX_GRENADE_AGE);
                    grenade.setShouldBounce(false);
                    grenade.setPosition(pos.add(new Vec3d(i/2, 0, i/2)));
                    if (!player.world.isClient()) {
                        player.world.spawnEntity(grenade);
                    }
                }
            }
        }
    }
}
