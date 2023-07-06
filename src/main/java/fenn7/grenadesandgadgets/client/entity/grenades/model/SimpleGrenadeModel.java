package fenn7.grenadesandgadgets.client.entity.grenades.model;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.entity.grenades.AbstractGrenadeEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SimpleGrenadeModel<T extends AbstractGrenadeEntity> extends AnimatedGeoModel<T> {
    private static final String MODEL_LOCATION = "geo/entity/";
    private static final String TEXTURE_LOCATION = "textures/entity/grenade3d/";
    private static final String ANIMATION_LOCATION = "animations/entity/grenade.animation.json";
    protected final String name;

    public SimpleGrenadeModel(String name) {
        this.name = name;
    }

    @Override
    public Identifier getModelLocation(T object) {
        return new Identifier(GrenadesMod.MOD_ID, MODEL_LOCATION + this.name + ".geo.json");
    }

    @Override
    public Identifier getTextureLocation(T object) {
        return new Identifier(GrenadesMod.MOD_ID,  TEXTURE_LOCATION + this.name + ".png");
    }

    @Override
    public Identifier getAnimationFileLocation(T animatable) {
        return new Identifier(GrenadesMod.MOD_ID, ANIMATION_LOCATION);
    }
}
