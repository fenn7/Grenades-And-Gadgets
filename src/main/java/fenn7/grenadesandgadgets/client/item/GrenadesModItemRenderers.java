package fenn7.grenadesandgadgets.client.item;

import fenn7.grenadesandgadgets.client.item.renderer.SimpleBlockItemRenderer;
import fenn7.grenadesandgadgets.client.item.renderer.TemporalFissureGrenadeItemRenderer;
import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class GrenadesModItemRenderers {
    public static void registerItemRenderers() {
        GrenadesMod.LOGGER.warn("Initialising Grenades And Gadgets Item Renderers...");
        GeoItemRenderer.registerItemRenderer(GrenadesModItems.GRENADE_TEMPORAL_FISSURE, new TemporalFissureGrenadeItemRenderer());
        GeoItemRenderer.registerItemRenderer(GrenadesModItems.GRENADIER_TABLE_BLOCK, new SimpleBlockItemRenderer<>("grenadier_table_block"));
        GeoItemRenderer.registerItemRenderer(GrenadesModItems.HIDDEN_EXPLOSIVE_BLOCK, new SimpleBlockItemRenderer<>("hidden_explosive_block"));
        GeoItemRenderer.registerItemRenderer(GrenadesModItems.REMOTE_EXPLOSIVE_BLOCK, new SimpleBlockItemRenderer<>("remote_explosive_block"));
    }
}
