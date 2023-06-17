package fenn7.grenadesandgadgets.client.network.packets;

import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;

public class AddFreezeNBTS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        int intList = buf.readInt();
        Entity entity = client.player.world.getEntityById(intList);
        if (entity instanceof LivingEntity alive) {
            alive.addStatusEffect(new StatusEffectInstance(GrenadesModStatus.FROZEN, 60, 1));
        }
    }
}
