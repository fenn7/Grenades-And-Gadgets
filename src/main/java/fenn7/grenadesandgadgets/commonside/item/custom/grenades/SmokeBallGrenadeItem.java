package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import java.util.List;

import fenn7.grenadesandgadgets.client.network.GrenadesModS2CPackets;
import fenn7.grenadesandgadgets.commonside.entity.projectiles.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.projectiles.SmokeBallGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModEntityData;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SmokeBallGrenadeItem extends AbstractGrenadeItem {
    public static final String SMOKE_BALL_COLOUR = "smoke.colour";
    public static final String COLOUR_SUB_TAG = "colours";

    public SmokeBallGrenadeItem(Settings settings) {
        super(settings);
        this.defaultSpeed = 0.7F;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        NbtCompound nbt = stack.getSubNbt(SMOKE_BALL_COLOUR);
        if (nbt != null) {
            tooltip.add(GrenadesModUtil.textOf("§o" + "Possible Colours:"));
            int[] colours = nbt.getIntArray(COLOUR_SUB_TAG);
            MutableText colourNames = GrenadesModUtil.mutableTextOf("");
            for (int i = 0; i < colours.length; ++i) {
                DyeColor colour = DyeColor.byFireworkColor(colours[i]);
                colourNames.append(new TranslatableText("item.minecraft.firework_star." + colour.getName())
                    .append(i == (colours.length - 1) ? GrenadesModUtil.textOf("") : GrenadesModUtil.textOf(", ")));
            }
            tooltip.add(colourNames);
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    protected AbstractGrenadeEntity createGrenadeAt(World world, PlayerEntity player, Hand hand) {
        return new SmokeBallGrenadeEntity(world, player);
    }
}
