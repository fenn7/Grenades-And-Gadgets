package fenn7.grenadesandgadgets.client.entity.projectiles.model;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.entity.projectiles.GrenadeEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GrenadeModel extends AnimatedGeoModel<GrenadeEntity> {

    public Identifier getModelLocation(GrenadeEntity object) {
        return new Identifier(GrenadesMod.MOD_ID, "geo/grenade.geo.json");
    }


    public Identifier getTextureLocation(GrenadeEntity object) {
        return new Identifier(GrenadesMod.MOD_ID, "textures/entity/grenade3d/grenade.png");
    }


    public Identifier getAnimationFileLocation(GrenadeEntity animatable) {
        return new Identifier(GrenadesMod.MOD_ID, "animations/grenade.animation.json");
    }
}
