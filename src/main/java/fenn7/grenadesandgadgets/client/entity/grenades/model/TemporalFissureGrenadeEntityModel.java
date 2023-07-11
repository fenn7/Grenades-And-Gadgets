package fenn7.grenadesandgadgets.client.entity.grenades.model;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.entity.grenades.TemporalFissureGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.TemporalFissureGrenadeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class TemporalFissureGrenadeEntityModel extends SimpleGrenadeModel<TemporalFissureGrenadeEntity> {
    private static final String TEXTURE_LOCATION = "textures/entity/grenade3d/";

    public TemporalFissureGrenadeEntityModel() {
        super("grenade_temporal_fissure");
    }

    @Override
    public Identifier getTextureLocation(TemporalFissureGrenadeEntity object) {
        ItemStack stack = object.getGrenadeItemStack();
        if (stack != null && stack.getOrCreateNbt().contains(TemporalFissureGrenadeItem.NBT_DIMENSION_KEY)) {
            return switch (stack.getOrCreateNbt().getInt(TemporalFissureGrenadeItem.NBT_DIMENSION_KEY)) {
                case -1 -> new Identifier(GrenadesMod.MOD_ID, TEXTURE_LOCATION + this.name + "_nether.png");
                case 1 -> new Identifier(GrenadesMod.MOD_ID, TEXTURE_LOCATION + this.name + "_end.png");
                default -> new Identifier(GrenadesMod.MOD_ID, TEXTURE_LOCATION + this.name + "_overworld.png");
            };
        }
        return new Identifier(GrenadesMod.MOD_ID,  TEXTURE_LOCATION + this.name + "_end.png");
    }
}
