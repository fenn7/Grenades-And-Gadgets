package fenn7.grenadesandgadgets.client;

import fenn7.grenadesandgadgets.client.entity.GrenadesModEntityRenderers;
import fenn7.grenadesandgadgets.client.entity.grenades.renderer.SimpleGrenadeRenderer;
import fenn7.grenadesandgadgets.client.entity.misc.renderer.FragmentRenderer;
import fenn7.grenadesandgadgets.client.network.GrenadesModS2CPackets;
import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class GrenadesModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GrenadesMod.LOGGER.warn("Initialising Grenades And Gadgets Clientside...");
        GrenadesModEntityRenderers.registerEntityRenderers();
        GrenadesModS2CPackets.registerS2CPackets();
    }
}