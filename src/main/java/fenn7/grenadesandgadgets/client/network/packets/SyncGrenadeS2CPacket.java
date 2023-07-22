package fenn7.grenadesandgadgets.client.network.packets;

import fenn7.grenadesandgadgets.commonside.entity.grenades.AbstractGrenadeEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;

public class SyncGrenadeS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int grenadeId = buf.readInt();
        int maxAge = buf.readInt();
        boolean shouldBounce = buf.readBoolean();
        float bounceMultiplier = buf.readFloat();
        float power = buf.readFloat();
        Entity entity = client.world.getEntityById(grenadeId);
        if (entity instanceof AbstractGrenadeEntity grenade) {
            grenade.setMaxAgeTicks(maxAge);
            grenade.setShouldBounce(shouldBounce);
            grenade.setBounceMultiplier(bounceMultiplier);
            grenade.setPower(power);
        }
    }
}
