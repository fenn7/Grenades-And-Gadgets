package fenn7.grenadesandgadgets.client.entity.grenades.renderer;

import fenn7.grenadesandgadgets.client.entity.grenades.model.SimpleGrenadeModel;
import fenn7.grenadesandgadgets.client.entity.grenades.model.TemporalFissureGrenadeEntityModel;
import fenn7.grenadesandgadgets.commonside.entity.grenades.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.TemporalFissureGrenadeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class TemporalFissureGrenadeEntityRenderer extends GeoProjectilesRenderer<TemporalFissureGrenadeEntity> {
    public TemporalFissureGrenadeEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new TemporalFissureGrenadeEntityModel());
    }
}
