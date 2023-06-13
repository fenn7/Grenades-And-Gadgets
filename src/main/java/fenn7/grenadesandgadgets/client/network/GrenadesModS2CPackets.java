package fenn7.grenadesandgadgets.client.network;

import fenn7.grenadesandgadgets.client.network.packets.FrozenNBTSyncPacket;
import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class GrenadesModS2CPackets {
    public static final Identifier FROZEN_NBT_SYNC = new Identifier(GrenadesMod.MOD_ID, "frozen_nbt_sync");

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(FROZEN_NBT_SYNC, FrozenNBTSyncPacket::receive);
    }
}
