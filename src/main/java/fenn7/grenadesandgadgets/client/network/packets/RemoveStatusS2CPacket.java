package fenn7.grenadesandgadgets.client.network.packets;


import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;

public class RemoveStatusS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        IntList intList = buf.readIntList();
        if (intList.size() == 2) {
            Entity entity = client.player.world.getEntityById(intList.getInt(0));
            StatusEffect effect = StatusEffect.byRawId(intList.getInt(1));
            if (entity instanceof LivingEntity alive && effect != null && alive.hasStatusEffect(effect)) {
                alive.removeStatusEffect(effect);
                alive.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.ICE.getDefaultState()),
                    alive.getX(), alive.getBodyY(0.5), alive.getZ(), 0, 0, 0);
            }
        }
    }
}
