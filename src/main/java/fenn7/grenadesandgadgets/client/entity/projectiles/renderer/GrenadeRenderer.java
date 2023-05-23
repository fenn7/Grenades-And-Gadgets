package fenn7.grenadesandgadgets.client.entity.projectiles.renderer;

import fenn7.grenadesandgadgets.client.entity.projectiles.model.GrenadeModel;
import fenn7.grenadesandgadgets.commonside.entity.projectiles.GrenadeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class GrenadeRenderer extends GeoProjectilesRenderer<GrenadeEntity> {
    public GrenadeRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new GrenadeModel());
    }
}
