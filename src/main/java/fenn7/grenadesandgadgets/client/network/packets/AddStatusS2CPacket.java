package fenn7.grenadesandgadgets.client.network.packets;

import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.PacketByteBuf;

public class AddStatusS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        IntList intList = buf.readIntList();
        if (intList.size() == 4) {
            Entity entity = client.player.world.getEntityById(intList.getInt(0));
            StatusEffect effect = StatusEffect.byRawId(intList.getInt(1));
            int duration = intList.getInt(2);
            int amplifier = intList.getInt(3);
            if (entity instanceof LivingEntity alive && effect != null) {
                alive.addStatusEffect(new StatusEffectInstance(effect, duration, amplifier));
            }
        }
    }
}
