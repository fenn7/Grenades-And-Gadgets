package fenn7.grenadesandgadgets.client.entity;

import fenn7.grenadesandgadgets.client.entity.block.renderer.SimpleBlockRenderer;
import fenn7.grenadesandgadgets.client.entity.grenades.renderer.SimpleGrenadeRenderer;
import fenn7.grenadesandgadgets.client.entity.misc.renderer.DecoyRenderer;
import fenn7.grenadesandgadgets.client.entity.misc.renderer.FragmentRenderer;
import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.block.GrenadesModBlockEntities;
import fenn7.grenadesandgadgets.commonside.block.entity.RadiantLightBlockEntity;
import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class GrenadesModEntityRenderers {
    public static void registerEntityRenderers() {
        GrenadesMod.LOGGER.warn("Initialising Grenades And Gadgets Entity Renderers...");
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
        EntityRendererRegistry.register(GrenadesModEntities.ICE_GRENADE_ENTITY,
            ctx -> new SimpleGrenadeRenderer<>(ctx,"grenade_ice"));
        EntityRendererRegistry.register(GrenadesModEntities.CONVERGENCE_GRENADE_ENTITY,
            ctx -> new SimpleGrenadeRenderer<>(ctx,"grenade_convergence"));
        EntityRendererRegistry.register(GrenadesModEntities.DIVERGENCE_GRENADE_ENTITY,
            ctx -> new SimpleGrenadeRenderer<>(ctx,"grenade_divergence"));
        EntityRendererRegistry.register(GrenadesModEntities.UPHEAVAL_GRENADE_ENTITY,
            ctx -> new SimpleGrenadeRenderer<>(ctx,"grenade_upheaval"));
        EntityRendererRegistry.register(GrenadesModEntities.DECOY_GRENADE_ENTITY,
            ctx -> new SimpleGrenadeRenderer<>(ctx,"grenade_decoy"));

        // misc
        EntityRendererRegistry.register(GrenadesModEntities.FRAGMENT_ENTITY, FragmentRenderer::new);
        EntityRendererRegistry.register(GrenadesModEntities.DECOY_ENTITY, DecoyRenderer::new);

        // block entities
        BlockEntityRendererRegistry.register(GrenadesModBlockEntities.RADIANT_LIGHT_BLOCK_ENTITY,
            ctx -> new SimpleBlockRenderer<RadiantLightBlockEntity>(ctx, "radiant_light_block"));
    }
}
