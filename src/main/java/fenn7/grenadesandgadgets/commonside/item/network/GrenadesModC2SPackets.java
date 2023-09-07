package fenn7.grenadesandgadgets.commonside.item.network;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.item.network.packets.SyncHiddenExplosiveC2SPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class GrenadesModC2SPackets {
    public static Identifier SYNC_HIDDEN_EXPLOSIVE_C2S = new Identifier(GrenadesMod.MOD_ID, "hidden_explosive_c2s");

    public static void registerC2SPackets() {
        GrenadesMod.LOGGER.warn("Initialising Grenades And Gadgets S2C Packets...");
        ServerPlayNetworking.registerGlobalReceiver(SYNC_HIDDEN_EXPLOSIVE_C2S, SyncHiddenExplosiveC2SPacket::receive);
    }
}
