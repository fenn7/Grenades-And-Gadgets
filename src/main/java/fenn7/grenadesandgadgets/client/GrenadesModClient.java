package fenn7.grenadesandgadgets.client;

import fenn7.grenadesandgadgets.client.entity.projectiles.renderer.SimpleGrenadeRenderer;
import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.entity.projectiles.FireGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.projectiles.GrenadeEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class GrenadesModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(GrenadesModEntities.GRENADE_ENTITY,
            ctx -> new SimpleGrenadeRenderer<GrenadeEntity>(ctx, "grenade"));
        EntityRendererRegistry.register(GrenadesModEntities.FIRE_GRENADE_ENTITY,
            ctx -> new SimpleGrenadeRenderer<FireGrenadeEntity>(ctx,"grenade_fire"));
    }
}