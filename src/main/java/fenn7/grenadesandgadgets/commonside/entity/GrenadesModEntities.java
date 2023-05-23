package fenn7.grenadesandgadgets.commonside.entity;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.entity.projectiles.GrenadeEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GrenadesModEntities {
    public static final EntityType<GrenadeEntity> GRENADE_ENTITY = Registry.register(
        Registry.ENTITY_TYPE, new Identifier(GrenadesMod.MOD_ID, "grenade_entity"),
        FabricEntityTypeBuilder.<GrenadeEntity>create(SpawnGroup.MISC, GrenadeEntity::new)
            .dimensions(EntityDimensions.fixed(0.25F, 0.375F))
            .trackRangeBlocks(48).trackedUpdateRate(10).build());
}
