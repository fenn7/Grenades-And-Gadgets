package fenn7.grenadesandgadgets.client.network.packets;

import fenn7.grenadesandgadgets.commonside.util.GrenadesModEntityData;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public class SyncNbtS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        int a = buf.readInt();
        Entity entity = client.world.getEntityById(a);
        String nbtId = buf.readString();
        NbtCompound nbt = buf.readNbt();
        if (nbtId != null && nbt != null && entity != null) {
            ((GrenadesModEntityData) entity).getPersistentData().put(nbtId, nbt);
        }
    }
}
