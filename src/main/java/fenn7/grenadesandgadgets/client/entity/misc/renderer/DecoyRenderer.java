package fenn7.grenadesandgadgets.client.entity.misc.renderer;

import fenn7.grenadesandgadgets.client.entity.misc.model.DecoyModel;
import fenn7.grenadesandgadgets.commonside.entity.misc.DecoyEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class DecoyRenderer extends GeoProjectilesRenderer<DecoyEntity> {
    public DecoyRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new DecoyModel());
    }
}
