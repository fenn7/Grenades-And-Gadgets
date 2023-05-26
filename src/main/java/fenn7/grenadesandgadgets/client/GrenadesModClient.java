package fenn7.grenadesandgadgets.client;

import fenn7.grenadesandgadgets.client.entity.projectiles.renderer.SimpleGrenadeRenderer;
import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class GrenadesModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(GrenadesModEntities.GRENADE_ENTITY,
            ctx -> new SimpleGrenadeRenderer<>(ctx, "grenade"));
        EntityRendererRegistry.register(GrenadesModEntities.FIRE_GRENADE_ENTITY,
            ctx -> new SimpleGrenadeRenderer<>(ctx,"grenade_fire"));
        EntityRendererRegistry.register(GrenadesModEntities.SMOKE_BALL_GRENADE_ENTITY,
            ctx -> new SimpleGrenadeRenderer<>(ctx,"grenade_smoke_ball"));
        EntityRendererRegistry.register(GrenadesModEntities.SMOKE_FLARE_GRENADE_ENTITY,
            ctx -> new SimpleGrenadeRenderer<>(ctx,"grenade_smoke_flare"));
    }
}