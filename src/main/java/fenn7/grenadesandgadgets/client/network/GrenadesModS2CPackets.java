package fenn7.grenadesandgadgets.client.network;

import fenn7.grenadesandgadgets.client.network.packets.SmokeSyncColourS2CPacket;
import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class GrenadesModS2CPackets {
    public static final Identifier SMOKE_SYNC_COLOUR = new Identifier(GrenadesMod.MOD_ID, "smoke_sync_colour");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(SMOKE_SYNC_COLOUR, SmokeSyncColourS2CPacket::receive);
    }
}
