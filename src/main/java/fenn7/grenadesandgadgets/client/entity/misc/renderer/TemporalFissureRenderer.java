package fenn7.grenadesandgadgets.client.entity.misc.renderer;

import fenn7.grenadesandgadgets.client.entity.misc.model.FragmentModel;
import fenn7.grenadesandgadgets.client.entity.misc.model.TemporalFissureModel;
import fenn7.grenadesandgadgets.commonside.entity.misc.FragmentEntity;
import fenn7.grenadesandgadgets.commonside.entity.misc.TemporalFissureEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class TemporalFissureRenderer extends GeoProjectilesRenderer<TemporalFissureEntity> {
    public TemporalFissureRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new TemporalFissureModel());
    }
}
