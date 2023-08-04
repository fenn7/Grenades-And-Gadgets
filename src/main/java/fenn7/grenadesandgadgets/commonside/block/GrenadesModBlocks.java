package fenn7.grenadesandgadgets.commonside.block;

import java.util.List;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.block.custom.GrenadierTableBlock;
import fenn7.grenadesandgadgets.commonside.block.custom.HiddenExplosiveBlock;
import fenn7.grenadesandgadgets.commonside.block.custom.RadiantLightBlock;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItemGroup;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GrenadesModBlocks {
    public static final Block RADIANT_LIGHT_BLOCK = registerBlockWithoutBlockItem("radiant_light_block",
        new RadiantLightBlock(FabricBlockSettings.of(Material.AIR).luminance(20).nonOpaque().dropsNothing().breakInstantly().noCollision().blockVision((state, world, pos) -> false)));

    public static final Block HIDDEN_EXPLOSIVE_BLOCK = registerBlockWithoutBlockItem("hidden_explosive_block",
        new HiddenExplosiveBlock(FabricBlockSettings.of(Material.METAL)));

    public static final Block GRENADIER_TABLE_BLOCK = registerBlockWithoutBlockItem("grenadier_table_block",
        new GrenadierTableBlock(FabricBlockSettings.of(Material.METAL)));

    private static Block registerBlockTooltip(String name, Block block, ItemGroup group, String tooltipKey) {
        registerBlockItemTooltip(name, block, group, tooltipKey);
        return Registry.register(Registry.BLOCK, new Identifier(GrenadesMod.MOD_ID, name), block);
    }

    private static Item registerBlockItemTooltip(String name, Block block, ItemGroup group, String tooltipKey) {
        return Registry.register(Registry.ITEM, new Identifier(GrenadesMod.MOD_ID, name),
            new BlockItem(block, new FabricItemSettings().group(group)) {
                @Override
                public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
                    tooltip.add(GrenadesModUtil.translatableTextOf(tooltipKey));
                }
            });
    }

    private static Block registerBlockWithoutBlockItem(String name, Block block) {
        return Registry.register(Registry.BLOCK, new Identifier(GrenadesMod.MOD_ID, name), block);
    }

    private static Block registerBlock(String name, Block block, ItemGroup group) {
        registerBlockItem(name, block, group);
        return Registry.register(Registry.BLOCK, new Identifier(GrenadesMod.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block, ItemGroup group) {
        return Registry.register(Registry.ITEM, new Identifier(GrenadesMod.MOD_ID, name),
            new BlockItem(block, new FabricItemSettings().group(group)));
    }

    public static void registerBlocks() {
        GrenadesMod.LOGGER.warn("Initialising Grenades and Gadgets Blocks...");
    }
}
