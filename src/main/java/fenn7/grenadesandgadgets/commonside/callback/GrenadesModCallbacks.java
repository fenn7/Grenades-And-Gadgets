package fenn7.grenadesandgadgets.commonside.callback;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.block.custom.HiddenExplosiveBlock;
import fenn7.grenadesandgadgets.commonside.block.custom.RemoteExplosiveBlock;
import fenn7.grenadesandgadgets.commonside.block.entity.AbstractDisguisedExplosiveBlockEntity;
import fenn7.grenadesandgadgets.commonside.block.entity.HiddenExplosiveBlockEntity;
import fenn7.grenadesandgadgets.commonside.block.entity.RemoteExplosiveBlockEntity;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.item.custom.misc.RemoteDetonatorItem;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public class GrenadesModCallbacks {
    public static void registerCallbacks() {
        GrenadesMod.LOGGER.warn("Initialising Grenades And Gadgets Callbacks...");
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            BlockState state = world.getBlockState(pos);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            ItemStack stack = player.getStackInHand(hand);
            if (!player.isSpectator()) {
                if (blockEntity instanceof AbstractDisguisedExplosiveBlockEntity h) {
                    if (state.get(HiddenExplosiveBlock.ARMED)) {
                        h.detonate(world, pos);
                    } else if (h instanceof RemoteExplosiveBlockEntity && stack.getItem() instanceof RemoteDetonatorItem r) {
                        r.removeExplosivePosFromNbt(pos, stack.getOrCreateNbt(), player);
                    }
                }
            }
            return ActionResult.PASS;
        });
    }
}
