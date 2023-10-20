package fenn7.grenadesandgadgets.commonside.item.network.packets;

import fenn7.grenadesandgadgets.client.screen.HiddenExplosiveScreenHandler;
import fenn7.grenadesandgadgets.client.screen.RemoteExplosiveScreenHandler;
import fenn7.grenadesandgadgets.commonside.block.GrenadesModBlockEntities;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SyncRemoteExplosiveC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity serverPlayerEntity,
                               ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf buf, PacketSender packetSender) {
        BlockPos pos = buf.readBlockPos();
        var array = buf.readIntArray();
        if (pos != null && array != null && array.length == 2) {
            var optional = serverPlayerEntity.getWorld().getBlockEntity(pos, GrenadesModBlockEntities.HIDDEN_EXPLOSIVE_BLOCK_ENTITY);
            var handler = serverPlayerEntity.currentScreenHandler;
            if (optional.isPresent()) {
                optional.get().getDelegate().set(array[0], array[1]);
            } else if (handler instanceof RemoteExplosiveScreenHandler hiddenHandler) {
                hiddenHandler.setDelegateValue(array[0], array[1]);
            }
        }
    }
}
