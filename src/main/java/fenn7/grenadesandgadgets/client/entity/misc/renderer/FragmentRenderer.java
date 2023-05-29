package fenn7.grenadesandgadgets.client.entity.misc.renderer;

import fenn7.grenadesandgadgets.client.entity.misc.model.FragmentModel;
import fenn7.grenadesandgadgets.commonside.entity.misc.FragmentEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class FragmentRenderer extends GeoProjectilesRenderer<FragmentEntity> {
    public FragmentRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new FragmentModel());
    }
}
