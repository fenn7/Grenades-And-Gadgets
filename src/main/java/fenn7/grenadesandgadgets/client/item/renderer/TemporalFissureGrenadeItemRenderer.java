package fenn7.grenadesandgadgets.client.item.renderer;

import fenn7.grenadesandgadgets.client.item.model.TemporalFissureGrenadeItemModel;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.TemporalFissureGrenadeItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class TemporalFissureGrenadeItemRenderer extends GeoItemRenderer<TemporalFissureGrenadeItem> {
    public TemporalFissureGrenadeItemRenderer() {
        super(new TemporalFissureGrenadeItemModel());
    }
}
