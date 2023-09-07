package fenn7.grenadesandgadgets.commonside.item.network.packets;

import fenn7.grenadesandgadgets.client.screen.HiddenExplosiveScreenHandler;
import fenn7.grenadesandgadgets.commonside.block.GrenadesModBlockEntities;
import fenn7.grenadesandgadgets.commonside.block.entity.HiddenExplosiveBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SyncHiddenExplosiveC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity serverPlayerEntity,
                               ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf buf, PacketSender packetSender) {
        BlockPos pos = buf.readBlockPos();
        if (pos != null) {
            var optional = serverPlayerEntity.getWorld().getBlockEntity(pos, GrenadesModBlockEntities.HIDDEN_EXPLOSIVE_BLOCK_ENTITY);
            var handler = serverPlayerEntity.currentScreenHandler;
            if (optional.isPresent()) {
                optional.get().getDelegate().set(1, 1);
            } else if (handler instanceof HiddenExplosiveScreenHandler hiddenHandler) {
                hiddenHandler.setDelegateValue(1, 1);
            }
        }
    }
}
