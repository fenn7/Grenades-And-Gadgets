package fenn7.grenadesandgadgets.commonside.entity;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.entity.projectiles.FireGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.projectiles.GrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.projectiles.HighExplosiveGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.projectiles.SmokeBallGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.projectiles.SmokeFlareGrenadeEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GrenadesModEntities {
    public static final EntityType<GrenadeEntity> GRENADE_ENTITY = Registry.register(
        Registry.ENTITY_TYPE, new Identifier(GrenadesMod.MOD_ID, "grenade"),
        FabricEntityTypeBuilder.<GrenadeEntity>create(SpawnGroup.MISC, GrenadeEntity::new)
            .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
            .trackRangeBlocks(48).trackedUpdateRate(10).build());

    public static final EntityType<FireGrenadeEntity> FIRE_GRENADE_ENTITY = Registry.register(
        Registry.ENTITY_TYPE, new Identifier(GrenadesMod.MOD_ID, "grenade_fire"),
        FabricEntityTypeBuilder.<FireGrenadeEntity>create(SpawnGroup.MISC, FireGrenadeEntity::new)
            .dimensions(EntityDimensions.fixed(0.275F, 0.275F))
            .trackRangeBlocks(48).trackedUpdateRate(10).build());

    public static final EntityType<SmokeBallGrenadeEntity> SMOKE_BALL_GRENADE_ENTITY = Registry.register(
        Registry.ENTITY_TYPE, new Identifier(GrenadesMod.MOD_ID, "grenade_smoke_ball"),
        FabricEntityTypeBuilder.<SmokeBallGrenadeEntity>create(SpawnGroup.MISC, SmokeBallGrenadeEntity::new)
            .dimensions(EntityDimensions.fixed(0.27F, 0.27F))
            .trackRangeBlocks(48).trackedUpdateRate(10).build());

    public static final EntityType<SmokeFlareGrenadeEntity> SMOKE_FLARE_GRENADE_ENTITY = Registry.register(
        Registry.ENTITY_TYPE, new Identifier(GrenadesMod.MOD_ID, "grenade_smoke_flare"),
        FabricEntityTypeBuilder.<SmokeFlareGrenadeEntity>create(SpawnGroup.MISC, SmokeFlareGrenadeEntity::new)
            .dimensions(EntityDimensions.fixed(0.27F, 0.27F))
            .trackRangeBlocks(48).trackedUpdateRate(10).build());

    public static final EntityType<HighExplosiveGrenadeEntity> HIGH_EXPLOSIVE_GRENADE_ENTITY = Registry.register(
        Registry.ENTITY_TYPE, new Identifier(GrenadesMod.MOD_ID, "grenade_high_explosive"),
        FabricEntityTypeBuilder.<HighExplosiveGrenadeEntity>create(SpawnGroup.MISC, HighExplosiveGrenadeEntity::new)
            .dimensions(EntityDimensions.fixed(0.275F, 0.45F))
            .trackRangeBlocks(48).trackedUpdateRate(10).build());
}
