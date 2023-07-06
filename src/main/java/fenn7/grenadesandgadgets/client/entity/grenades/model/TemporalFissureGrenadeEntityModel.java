package fenn7.grenadesandgadgets.client.entity.grenades.model;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.entity.grenades.TemporalFissureGrenadeEntity;
import net.minecraft.util.Identifier;

public class TemporalFissureGrenadeEntityModel extends SimpleGrenadeModel<TemporalFissureGrenadeEntity> {
    private static final String TEXTURE_LOCATION = "textures/entity/grenade3d/";

    public TemporalFissureGrenadeEntityModel() {
        super("grenade_temporal_fissure");
    }

    @Override
    public Identifier getTextureLocation(TemporalFissureGrenadeEntity object) {
        // TODO: IMPLEMENT
        return new Identifier(GrenadesMod.MOD_ID,  TEXTURE_LOCATION + this.name + "_end.png");
    }
}
