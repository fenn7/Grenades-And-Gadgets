package fenn7.grenadesandgadgets.client.item.model;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.TemporalFissureGrenadeItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class TemporalFissureGrenadeModel extends AnimatedGeoModel<TemporalFissureGrenadeItem> {
    private static final String MODEL_LOCATION = "geo/item/grenade_temporal_fissure/grenade_temporal_fissure_overworld_3d.geo.json";
    private static final String ANIMATION_LOCATION = "animations/item/grenade_temporal_fissure.animation.json";
    private static final String TEXTURE_LOCATION = "textures/item/grenade3d/grenade_temporal_";

    @Override
    public Identifier getModelLocation(TemporalFissureGrenadeItem object) {
        return new Identifier(GrenadesMod.MOD_ID, MODEL_LOCATION);
    }

    @Override
    public Identifier getTextureLocation(TemporalFissureGrenadeItem object) {
        return new Identifier(GrenadesMod.MOD_ID, TEXTURE_LOCATION + "overworld.png");
    }

    @Override
    public Identifier getAnimationFileLocation(TemporalFissureGrenadeItem animatable) {
        return new Identifier(GrenadesMod.MOD_ID, ANIMATION_LOCATION);
    }
}
