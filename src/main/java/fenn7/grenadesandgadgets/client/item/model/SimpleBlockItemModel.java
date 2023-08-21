package fenn7.grenadesandgadgets.client.item.model;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SimpleBlockItemModel<T extends BlockItem & IAnimatable> extends AnimatedGeoModel<T> {
    private static final String MODEL_LOCATION = "geo/block/";
    private static final String TEXTURE_LOCATION = "textures/block/";
    private static final String ANIMATION_LOCATION = "animations/block/";
    private final String name;

    public SimpleBlockItemModel(String name) {
        this.name = name;
    }

    @Override
    public Identifier getModelLocation(T object) {
        return new Identifier(GrenadesMod.MOD_ID, MODEL_LOCATION + name + ".geo.json");
    }

    @Override
    public Identifier getTextureLocation(T object) {
        return new Identifier(GrenadesMod.MOD_ID,  TEXTURE_LOCATION + name + ".png");
    }

    @Override
    public Identifier getAnimationFileLocation(T animatable) {
        return new Identifier(GrenadesMod.MOD_ID, ANIMATION_LOCATION + name + ".animation.json");
    }
}
