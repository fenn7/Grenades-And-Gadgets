package fenn7.grenadesandgadgets.client;

import fenn7.grenadesandgadgets.client.entity.GrenadesModEntityRenderers;
import fenn7.grenadesandgadgets.client.item.GrenadesModItemRenderers;
import fenn7.grenadesandgadgets.client.network.GrenadesModS2CPackets;
import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import net.fabricmc.api.ClientModInitializer;

public class GrenadesModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GrenadesMod.LOGGER.warn("Initialising Grenades And Gadgets Clientside...");
        GrenadesModEntityRenderers.registerEntityRenderers();
        GrenadesModItemRenderers.registerItemRenderers();
        GrenadesModS2CPackets.registerS2CPackets();
    }
}