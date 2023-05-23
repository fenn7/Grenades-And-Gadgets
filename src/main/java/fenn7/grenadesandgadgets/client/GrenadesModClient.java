package fenn7.grenadesandgadgets.client;

import fenn7.grenadesandgadgets.client.entity.projectiles.renderer.GrenadeRenderer;
import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class GrenadesModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(GrenadesModEntities.GRENADE_ENTITY, GrenadeRenderer::new);
    }
}