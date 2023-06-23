package fenn7.grenadesandgadgets.client.entity.misc.model;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.entity.misc.DecoyEntity;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModEntityData;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DecoyModel extends AnimatedGeoModel<DecoyEntity> {
    private static final String MODEL_LOCATION = "geo/entity/decoy.geo.json";
    private static final String ANIMATION_LOCATION = "animations/entity/decoy.animation.json";

    @Override
    public Identifier getModelLocation(DecoyEntity object) {
        return new Identifier(GrenadesMod.MOD_ID, MODEL_LOCATION);
    }

    @Override
    public Identifier getTextureLocation(DecoyEntity object) {
        NbtCompound data = ((GrenadesModEntityData) object).getPersistentData();
        Entity nbtEntity = object.world.getEntityById(data.getCompound("player_owner").getInt("id"));
        return nbtEntity instanceof ClientPlayerEntity cPlayer ? cPlayer.getSkinTexture() : new Identifier("textures/entity/steve.png");
    }

    @Override
    public Identifier getAnimationFileLocation(DecoyEntity animatable) {
        return new Identifier(GrenadesMod.MOD_ID, ANIMATION_LOCATION);
    }
}
