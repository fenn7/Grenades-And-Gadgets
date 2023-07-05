package fenn7.grenadesandgadgets.client.item.renderer;

import fenn7.grenadesandgadgets.client.item.model.TemporalFissureGrenadeModel;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.TemporalFissureGrenadeItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class TemporalFissureGrenadeRenderer extends GeoItemRenderer<TemporalFissureGrenadeItem> {
    public TemporalFissureGrenadeRenderer() {
        super(new TemporalFissureGrenadeModel());
    }
}
