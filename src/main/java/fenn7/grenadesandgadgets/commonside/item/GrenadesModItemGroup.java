package fenn7.grenadesandgadgets.commonside.item;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class GrenadesModItemGroup {
    public static final ItemGroup GRENADESMOD_MISC = FabricItemGroupBuilder.build(new Identifier(GrenadesMod.MOD_ID,
        ""), () -> new ItemStack(Items.ACACIA_LEAVES));
}
