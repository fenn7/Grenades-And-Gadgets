package fenn7.grenadesandgadgets.client.entity.projectiles.renderer;

import fenn7.grenadesandgadgets.client.entity.projectiles.model.SimpleGrenadeModel;
import fenn7.grenadesandgadgets.commonside.entity.projectiles.AbstractGrenadeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class SimpleGrenadeRenderer<T extends AbstractGrenadeEntity> extends GeoProjectilesRenderer<T> {
    public SimpleGrenadeRenderer(EntityRendererFactory.Context ctx, String grenadeName) {
        super(ctx, new SimpleGrenadeModel<T>(grenadeName));
    }
}
