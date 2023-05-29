package fenn7.grenadesandgadgets.commonside.tags;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GrenadesModTags {
    public static class Blocks {
    }

    public static class Items {
        public static final TagKey<Item> SHRAPNEL_MATERIALS = createCommonTag("shrapnel_materials");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(Registry.ITEM_KEY, new Identifier(GrenadesMod.MOD_ID, name));
        }

        private static TagKey<Item> createCommonTag(String name) {
            return TagKey.of(Registry.ITEM_KEY, new Identifier("c", name));
        }
    }
}
