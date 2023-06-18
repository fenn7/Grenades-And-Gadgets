package fenn7.grenadesandgadgets.client.entity.block.model;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SimpleBlockModel<T extends BlockEntity & IAnimatable> extends AnimatedGeoModel<T> {
    private static final String MODEL_LOCATION = "geo/block/";
    private static final String TEXTURE_LOCATION = "textures/block/";
    private static final String ANIMATION_LOCATION = "animations/block/";
    private final String name;

    public SimpleBlockModel(String name) {
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
