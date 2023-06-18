package fenn7.grenadesandgadgets.client.network;

import fenn7.grenadesandgadgets.client.network.packets.AddStatusS2CPacket;
import fenn7.grenadesandgadgets.client.network.packets.RemoveStatusS2CPacket;
import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class GrenadesModS2CPackets {
    public static final Identifier REMOVE_EFFECT_S2C = new Identifier(GrenadesMod.MOD_ID, "remove_effect_s2c");
    public static final Identifier ADD_EFFECT_S2C = new Identifier(GrenadesMod.MOD_ID, "add_effect_s2c");

    public static void registerS2CPackets() {
        GrenadesMod.LOGGER.warn("Initialising Grenades And Gadgets S2C Packets...");
        ClientPlayNetworking.registerGlobalReceiver(REMOVE_EFFECT_S2C, RemoveStatusS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ADD_EFFECT_S2C, AddStatusS2CPacket::receive);
    }
}
