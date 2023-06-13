package fenn7.grenadesandgadgets.client;

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
        EntityRendererRegistry.register(GrenadesModEntities.GRENADE_ENTITY,
            ctx -> new SimpleGrenadeRenderer<>(ctx, "grenade"));
        EntityRendererRegistry.register(GrenadesModEntities.FIRE_GRENADE_ENTITY,
            ctx -> new SimpleGrenadeRenderer<>(ctx,"grenade_fire"));
        EntityRendererRegistry.register(GrenadesModEntities.SMOKE_BALL_GRENADE_ENTITY,
            ctx -> new SimpleGrenadeRenderer<>(ctx,"grenade_smoke_ball"));
        EntityRendererRegistry.register(GrenadesModEntities.SMOKE_FLARE_GRENADE_ENTITY,
            ctx -> new SimpleGrenadeRenderer<>(ctx,"grenade_smoke_flare"));
        EntityRendererRegistry.register(GrenadesModEntities.HIGH_EXPLOSIVE_GRENADE_ENTITY,
            ctx -> new SimpleGrenadeRenderer<>(ctx,"grenade_high_explosive"));
        EntityRendererRegistry.register(GrenadesModEntities.FRAGMENTATION_GRENADE_ENTITY,
            ctx -> new SimpleGrenadeRenderer<>(ctx,"grenade_fragmentation"));
        EntityRendererRegistry.register(GrenadesModEntities.RADIANT_GRENADE_ENTITY,
            ctx -> new SimpleGrenadeRenderer<>(ctx,"grenade_radiant"));
        EntityRendererRegistry.register(GrenadesModEntities.MAGIC_GRENADE_ENTITY,
            ctx -> new SimpleGrenadeRenderer<>(ctx,"grenade_magic"));

        EntityRendererRegistry.register(GrenadesModEntities.FRAGMENT_ENTITY, FragmentRenderer::new);

        GrenadesModS2CPackets.registerS2CPackets();
    }
}