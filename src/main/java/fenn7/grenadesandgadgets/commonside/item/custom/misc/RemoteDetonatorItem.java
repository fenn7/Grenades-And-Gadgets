package fenn7.grenadesandgadgets.commonside.item.custom.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fenn7.grenadesandgadgets.commonside.block.GrenadesModBlocks;
import fenn7.grenadesandgadgets.commonside.block.custom.RemoteExplosiveBlock;
import fenn7.grenadesandgadgets.commonside.block.entity.RemoteExplosiveBlockEntity;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RemoteDetonatorItem extends Item {
    private static final String NBT_TAG = "explosive.list";
    private static final String ALREADY = "container.grenadesandgadgets.already";
    private static final String ADDED = "container.grenadesandgadgets.added";
    private static final String MAXSIZE = "container.grenadesandgadgets.maxsize";
    private static final int MAX_LINKED_EXPLOSIVES = 8;

    public RemoteDetonatorItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().getBlockState(context.getBlockPos()).getBlock().equals(GrenadesModBlocks.REMOTE_EXPLOSIVE_BLOCK)) {
            this.addExplosivePosToNbt(context.getBlockPos(), context.getStack().getOrCreateNbt(), context.getPlayer());
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(context);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        var positions = stack.getOrCreateNbt().getLongArray(NBT_TAG);
        tooltip.add(GrenadesModUtil.textOf("Linked Explosives: " + positions.length));
    }

    private void addExplosivePosToNbt(BlockPos explosivePos, NbtCompound nbt, PlayerEntity player) {
        var explosivePosList = new ArrayList<>(Arrays.stream(nbt.getLongArray(NBT_TAG)).mapToObj(BlockPos::fromLong).toList());
        if (explosivePosList.size() >= MAX_LINKED_EXPLOSIVES) {
            player.sendMessage(GrenadesModUtil.translatableTextOf(MAXSIZE), false);
        } else if (explosivePosList.contains(explosivePos)) {
            player.sendMessage(GrenadesModUtil.translatableTextOf(ALREADY), false);
        } else {
            if (!player.world.isClient) {
                player.sendMessage(GrenadesModUtil.translatableTextOf(ADDED).append(explosivePos.toShortString()), false);
            }
            explosivePosList.add(explosivePos);
        }
        nbt.putLongArray(NBT_TAG, explosivePosList.stream().mapToLong(BlockPos::asLong).toArray());
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        NbtCompound nbt = user.getStackInHand(hand).getOrCreateNbt();
        var explosivePosList = new ArrayList<>(Arrays.stream(nbt.getLongArray(NBT_TAG)).mapToObj(BlockPos::fromLong).toList());
        var usedPosList = new ArrayList<BlockPos>();
        explosivePosList.forEach(pos -> {
            if (!world.isClient) {
                user.sendMessage(Text.of(pos.toShortString()), false);
            }
            if (world.getBlockEntity(pos) instanceof RemoteExplosiveBlockEntity) {
                world.setBlockState(pos, world.getBlockState(pos).with(RemoteExplosiveBlock.ARMED, true));
                usedPosList.add(pos);
            }
        });
        explosivePosList.removeAll(usedPosList);
        nbt.putLongArray(NBT_TAG, explosivePosList.stream().mapToLong(BlockPos::asLong).toArray());
        return TypedActionResult.consume(user.getStackInHand(hand));
    }
}
