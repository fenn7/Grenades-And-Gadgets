package fenn7.grenadesandgadgets.commonside.callback;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.block.custom.HiddenExplosiveBlock;
import fenn7.grenadesandgadgets.commonside.block.entity.HiddenExplosiveBlockEntity;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.ActionResult;

public class GrenadesModCallbacks {
    public static void registerCallbacks() {
        GrenadesMod.LOGGER.warn("Initialising Grenades And Gadgets Callbacks...");
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            BlockState state = world.getBlockState(pos);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (!player.isSpectator() && blockEntity instanceof HiddenExplosiveBlockEntity h && state.get(HiddenExplosiveBlock.ARMED)) {
                h.detonate(world, pos);
            }
            return ActionResult.PASS;
        });
    }
}
