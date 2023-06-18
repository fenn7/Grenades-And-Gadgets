package fenn7.grenadesandgadgets.commonside.block;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.block.entity.RadiantLightBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GrenadesModBlockEntities {
    public static BlockEntityType<RadiantLightBlockEntity> RADIANT_LIGHT_BLOCK_ENTITY =
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(GrenadesMod.MOD_ID, "radiant_light_block_entity"),
        FabricBlockEntityTypeBuilder.create(RadiantLightBlockEntity::new, GrenadesModBlocks.RADIANT_LIGHT_BLOCK).build(null));

    public static void registerBlockEntities() {
        GrenadesMod.LOGGER.warn("Initialising Grenades and Gadgets Block Entities...");
    }
}
