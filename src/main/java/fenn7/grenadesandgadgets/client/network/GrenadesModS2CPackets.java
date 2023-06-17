package fenn7.grenadesandgadgets.client.network;

import fenn7.grenadesandgadgets.client.network.packets.AddFreezeNBTS2CPacket;
import fenn7.grenadesandgadgets.client.network.packets.RemoveStatusS2CPacket;
import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class GrenadesModS2CPackets {
    public static final Identifier REMOVE_EFFECT_S2C = new Identifier(GrenadesMod.MOD_ID, "remove_effect_s2c");
    public static final Identifier FREEZE_NBT_S2C = new Identifier(GrenadesMod.MOD_ID, "freeze_effect_s2c");

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(REMOVE_EFFECT_S2C, RemoveStatusS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(FREEZE_NBT_S2C, AddFreezeNBTS2CPacket::receive);
    }
}
