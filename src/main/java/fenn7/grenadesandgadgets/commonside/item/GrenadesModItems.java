package fenn7.grenadesandgadgets.commonside.item;


import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.block.GrenadesModBlocks;
import fenn7.grenadesandgadgets.commonside.item.custom.block.DisguisedExplosiveBlockItem;
import fenn7.grenadesandgadgets.commonside.item.custom.block.GrenadierTableBlockItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.ConvergenceGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.DecoyGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.DivergenceGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.FireGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.FragmentationGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.GrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.HighExplosiveGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.IceGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.MagicGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.RadiantGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.SmokeBallGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.SmokeFlareGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.StormGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.TemporalFissureGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.grenades.UpheavalGrenadeItem;
import fenn7.grenadesandgadgets.commonside.item.custom.misc.RemoteDetonatorItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class GrenadesModItems {
    public static final Item GRENADE = register("grenade",
        new GrenadeItem(new FabricItemSettings().group(GrenadesModItemGroup.GRENADESMOD_MISC).maxCount(16)));
    public static final Item GRENADE_FIRE = register("grenade_fire",
        new FireGrenadeItem(new FabricItemSettings().group(GrenadesModItemGroup.GRENADESMOD_MISC).maxCount(8)));
    public static final Item GRENADE_SMOKE_BALL = register("grenade_smoke_ball",
        new SmokeBallGrenadeItem(new FabricItemSettings().group(GrenadesModItemGroup.GRENADESMOD_MISC).maxCount(10)));
    public static final Item GRENADE_SMOKE_FLARE = register("grenade_smoke_flare",
        new SmokeFlareGrenadeItem(new FabricItemSettings().group(GrenadesModItemGroup.GRENADESMOD_MISC).maxCount(10)));
    public static final Item GRENADE_HIGH_EXPLOSIVE = register("grenade_high_explosive",
        new HighExplosiveGrenadeItem(new FabricItemSettings().group(GrenadesModItemGroup.GRENADESMOD_MISC).maxCount(8)));
    public static final Item GRENADE_FRAGMENTATION = register("grenade_fragmentation",
        new FragmentationGrenadeItem(new FabricItemSettings().group(GrenadesModItemGroup.GRENADESMOD_MISC).maxCount(10)));
    public static final Item GRENADE_RADIANT = register("grenade_radiant",
        new RadiantGrenadeItem(new FabricItemSettings().group(GrenadesModItemGroup.GRENADESMOD_MISC).maxCount(8)));
    public static final Item GRENADE_MAGIC = register("grenade_magic",
        new MagicGrenadeItem(new FabricItemSettings().group(GrenadesModItemGroup.GRENADESMOD_MISC).maxCount(6)));
    public static final Item GRENADE_ICE = register("grenade_ice",
        new IceGrenadeItem(new FabricItemSettings().group(GrenadesModItemGroup.GRENADESMOD_MISC).maxCount(6)));
    public static final Item GRENADE_CONVERGENCE = register("grenade_convergence",
        new ConvergenceGrenadeItem(new FabricItemSettings().group(GrenadesModItemGroup.GRENADESMOD_MISC).maxCount(6)));
    public static final Item GRENADE_DIVERGENCE = register("grenade_divergence",
        new DivergenceGrenadeItem(new FabricItemSettings().group(GrenadesModItemGroup.GRENADESMOD_MISC).maxCount(6)));
    public static final Item GRENADE_UPHEAVAL = register("grenade_upheaval",
        new UpheavalGrenadeItem(new FabricItemSettings().group(GrenadesModItemGroup.GRENADESMOD_MISC).maxCount(6)));
    public static final Item GRENADE_DECOY = register("grenade_decoy",
        new DecoyGrenadeItem(new FabricItemSettings().group(GrenadesModItemGroup.GRENADESMOD_MISC).maxCount(8)));
    public static final Item GRENADE_STORM = register("grenade_storm",
        new StormGrenadeItem(new FabricItemSettings().group(GrenadesModItemGroup.GRENADESMOD_MISC).maxCount(6)));
    public static final Item GRENADE_TEMPORAL_FISSURE = register("grenade_temporal_fissure_3d",
        new TemporalFissureGrenadeItem(new FabricItemSettings().group(GrenadesModItemGroup.GRENADESMOD_MISC).maxCount(4).rarity(Rarity.EPIC)));

    // BLOCKITEMS
    public static final Item HIDDEN_EXPLOSIVE_BLOCK = register("hidden_explosive_block",
        new DisguisedExplosiveBlockItem(GrenadesModBlocks.HIDDEN_EXPLOSIVE_BLOCK, new FabricItemSettings().group(GrenadesModItemGroup.GRENADESMOD_MISC).maxCount(4)));
    public static final Item REMOTE_EXPLOSIVE_BLOCK = register("remote_explosive_block",
        new DisguisedExplosiveBlockItem(GrenadesModBlocks.REMOTE_EXPLOSIVE_BLOCK, new FabricItemSettings().group(GrenadesModItemGroup.GRENADESMOD_MISC).maxCount(8)));
    public static final Item GRENADIER_TABLE_BLOCK = register("grenadier_table_block",
        new GrenadierTableBlockItem(GrenadesModBlocks.GRENADIER_TABLE_BLOCK, new FabricItemSettings().group(GrenadesModItemGroup.GRENADESMOD_MISC)));

    public static final Item REMOTE_DETONATOR = register("remote_detonator",
        new RemoteDetonatorItem(new FabricItemSettings().group(GrenadesModItemGroup.GRENADESMOD_MISC).maxCount(1)));

    public static void registerItems() {
        GrenadesMod.LOGGER.debug("Initialising Grenades And Gadgets Items...");
    }

    private static Item register(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(GrenadesMod.MOD_ID, name), item);
    }
}
