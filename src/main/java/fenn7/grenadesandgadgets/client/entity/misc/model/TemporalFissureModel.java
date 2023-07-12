package fenn7.grenadesandgadgets.client.entity.misc.model;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.entity.misc.TemporalFissureEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class TemporalFissureModel extends AnimatedGeoModel<TemporalFissureEntity> {
    private static final String MODEL_LOCATION = "geo/entity/temporal_fissure.geo.json";
    private static final String TEXTURE_LOCATION = "textures/entity/misc/temporal_fissure_";
    private static final String ANIMATION_LOCATION = "animations/entity/temporal_fissure.animation.json";

    @Override
    public Identifier getModelLocation(TemporalFissureEntity object) {
        return new Identifier(GrenadesMod.MOD_ID, MODEL_LOCATION);
    }

    @Override
    public Identifier getTextureLocation(TemporalFissureEntity object) {
        return switch (object.getDataTracker().get(TemporalFissureEntity.DIMENSION_KEY)) {
            case -1 -> new Identifier(GrenadesMod.MOD_ID, TEXTURE_LOCATION + "nether.png");
            case 1 -> new Identifier(GrenadesMod.MOD_ID, TEXTURE_LOCATION + "end.png");
            default -> new Identifier(GrenadesMod.MOD_ID, TEXTURE_LOCATION + "overworld.png");
        };
    }

    @Override
    public Identifier getAnimationFileLocation(TemporalFissureEntity animatable) {
        return new Identifier(GrenadesMod.MOD_ID, ANIMATION_LOCATION);
    }
}
