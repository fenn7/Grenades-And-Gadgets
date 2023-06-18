package fenn7.grenadesandgadgets.client.entity.misc.model;

import java.util.HashMap;
import java.util.Map;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.entity.grenades.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.misc.FragmentEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class FragmentModel extends AnimatedGeoModel<FragmentEntity> {
    private static final String MODEL_LOCATION = "geo/entity/fragment.geo.json";
    private static final String ANIMATION_LOCATION = "animations/entity/fragment.animation.json";
    private static final String TEXTURE_LOCATION = "textures/block/";
    private static final Map<Item, String> textureLocations = new HashMap<>();
    static {
        textureLocations.put(Items.GOLD_INGOT, "gold_block");
        textureLocations.put(Items.COPPER_INGOT, "copper_block");
        textureLocations.put(Items.IRON_INGOT, "iron_block");
        textureLocations.put(Items.AMETHYST_SHARD, "amethyst_block");
        textureLocations.put(Items.PRISMARINE_SHARD, "prismarine");
        textureLocations.put(Items.DIAMOND, "diamond_block");
        textureLocations.put(Items.OBSIDIAN, "obsidian");
        textureLocations.put(Items.MAGMA_BLOCK, "magma");
        textureLocations.put(Items.NETHERITE_SCRAP, "netherite_block");
        textureLocations.put(Items.SHULKER_SHELL, "purpur_block");
    }

    @Override
    public Identifier getModelLocation(FragmentEntity object) {
        return new Identifier(GrenadesMod.MOD_ID, MODEL_LOCATION);
    }

    @Override
    public Identifier getTextureLocation(FragmentEntity object) {
        return new Identifier(TEXTURE_LOCATION + textureLocations.getOrDefault(object.getFragmentItem(), "iron_block") + ".png");
    }

    @Override
    public Identifier getAnimationFileLocation(FragmentEntity animatable) {
        return new Identifier(GrenadesMod.MOD_ID, ANIMATION_LOCATION);
    }
}
