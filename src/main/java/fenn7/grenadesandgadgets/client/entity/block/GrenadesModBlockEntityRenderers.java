package fenn7.grenadesandgadgets.client.entity.block;

import fenn7.grenadesandgadgets.client.entity.block.renderer.HiddenExplosiveBlockRenderer;
import fenn7.grenadesandgadgets.client.entity.block.renderer.RemoteExplosiveBlockRenderer;
import fenn7.grenadesandgadgets.client.entity.block.renderer.SimpleBlockRenderer;
import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.block.GrenadesModBlockEntities;
import fenn7.grenadesandgadgets.commonside.block.entity.GrenadierTableBlockEntity;
import fenn7.grenadesandgadgets.commonside.block.entity.RadiantLightBlockEntity;
import fenn7.grenadesandgadgets.commonside.block.entity.RemoteExplosiveBlockEntity;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

public class GrenadesModBlockEntityRenderers {
    @SuppressWarnings("unchecked")
    public static void registerBlockEntityRenderers() {
        GrenadesMod.LOGGER.warn("Initialising Grenades And Gadgets Block Entity Renderers...");
        BlockEntityRendererRegistry.register(GrenadesModBlockEntities.RADIANT_LIGHT_BLOCK_ENTITY,
            ctx -> new SimpleBlockRenderer<RadiantLightBlockEntity>(ctx, "radiant_light_block"));

        BlockEntityRendererRegistry.register(GrenadesModBlockEntities.HIDDEN_EXPLOSIVE_BLOCK_ENTITY, HiddenExplosiveBlockRenderer::new);

        BlockEntityRendererRegistry.register(GrenadesModBlockEntities.REMOTE_EXPLOSIVE_BLOCK_ENTITY, RemoteExplosiveBlockRenderer::new);

        BlockEntityRendererRegistry.register(GrenadesModBlockEntities.GRENADIER_TABLE_BLOCK_ENTITY,
            ctx -> new SimpleBlockRenderer<GrenadierTableBlockEntity>(ctx, "grenadier_table_block"));
    }
}
