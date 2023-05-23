package fenn7.grenadesandgadgets.commonside.item;


import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.FireGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.GrenadeItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GrenadesModItems {
    public static final Item GRENADE = register("grenade",
        new GrenadeItem(new FabricItemSettings().group(GrenadesModItemGroup.GRENADESMOD_MISC).maxCount(16)));
    public static final Item GRENADE_FIRE = register("grenade_fire",
        new FireGrenadeItem(new FabricItemSettings().group(GrenadesModItemGroup.GRENADESMOD_MISC).maxCount(8)));

    public static void registerItems() {
        GrenadesMod.LOGGER.debug("Initialising Grenades And Gadgets Items...");
    }

    private static Item register(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(GrenadesMod.MOD_ID, name), item);
    }
}
