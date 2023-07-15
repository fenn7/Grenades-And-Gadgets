package fenn7.grenadesandgadgets.commonside.tags;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GrenadesModTags {
    public static class Blocks {
        public static final TagKey<Block> NETHER_FISSURE_CORRUPTION = createCommonTag("dim-1_fissure_corruption");
        public static final TagKey<Block> OVERWORLD_FISSURE_CORRUPTION = createCommonTag("dim0_fissure_corruption");
        public static final TagKey<Block> END_FISSURE_CORRUPTION = createCommonTag("dim1_fissure_corruption");
        public static final TagKey<Block> TEMPORAL_FISSURE_IMMUNE = createCommonTag("temporal_fissure_immune");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(Registry.BLOCK_KEY, new Identifier(GrenadesMod.MOD_ID, name));
        }

        private static TagKey<Block> createCommonTag(String name) {
            return TagKey.of(Registry.BLOCK_KEY, new Identifier("c", name));
        }
    }

    public static class Items {
        public static final TagKey<Item> FRAGMENT_MATERIALS = createCommonTag("fragment_materials");
        public static final TagKey<Item> DIMENSIONAL_ESSENCES = createCommonTag("dimensional_essences");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(Registry.ITEM_KEY, new Identifier(GrenadesMod.MOD_ID, name));
        }

        private static TagKey<Item> createCommonTag(String name) {
            return TagKey.of(Registry.ITEM_KEY, new Identifier("c", name));
        }
    }
}
