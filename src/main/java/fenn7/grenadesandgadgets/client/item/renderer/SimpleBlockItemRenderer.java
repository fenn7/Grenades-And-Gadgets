package fenn7.grenadesandgadgets.client.item.renderer;

import fenn7.grenadesandgadgets.client.item.model.SimpleBlockItemModel;
import net.minecraft.item.BlockItem;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class SimpleBlockItemRenderer<T extends BlockItem & IAnimatable> extends GeoItemRenderer<T> {
    public SimpleBlockItemRenderer(String blockName) {
        super(new SimpleBlockItemModel<>(blockName));
    }
}
