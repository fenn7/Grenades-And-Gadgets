package fenn7.grenadesandgadgets.client.network.packets;


import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

public class RemoveFrozenS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        int entityID = buf.readInt();
        LivingEntity entity = (LivingEntity) client.player.world.getEntityById(entityID);
        entity.removeStatusEffect(GrenadesModStatus.FROZEN);
        client.player.sendMessage(Text.of("PACKETT " + entity), true);
    }
}
