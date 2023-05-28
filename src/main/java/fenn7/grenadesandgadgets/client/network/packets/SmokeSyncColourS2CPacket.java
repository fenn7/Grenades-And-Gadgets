package fenn7.grenadesandgadgets.client.network.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class SmokeSyncColourS2CPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity serverPlayer, ServerPlayNetworkHandler serverPlayNetworkHandler,
                               PacketByteBuf packetByteBuf, PacketSender packetSender) {
    }
}
