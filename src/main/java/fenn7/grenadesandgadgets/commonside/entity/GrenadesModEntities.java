package fenn7.grenadesandgadgets.commonside.entity;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.entity.grenades.ConvergenceGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.DivergenceGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.FireGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.FragmentationGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.GrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.HighExplosiveGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.IceGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.MagicGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.RadiantGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.SmokeBallGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.SmokeFlareGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.UpheavalGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.misc.DecoyEntity;
import fenn7.grenadesandgadgets.commonside.entity.misc.FragmentEntity;
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

    public static final EntityType<FragmentationGrenadeEntity> FRAGMENTATION_GRENADE_ENTITY = Registry.register(
        Registry.ENTITY_TYPE, new Identifier(GrenadesMod.MOD_ID, "grenade_fragmentation"),
        FabricEntityTypeBuilder.<FragmentationGrenadeEntity>create(SpawnGroup.MISC, FragmentationGrenadeEntity::new)
            .dimensions(EntityDimensions.fixed(0.225F, 0.35F))
            .trackRangeBlocks(48).trackedUpdateRate(10).build());

    public static final EntityType<RadiantGrenadeEntity> RADIANT_GRENADE_ENTITY = Registry.register(
        Registry.ENTITY_TYPE, new Identifier(GrenadesMod.MOD_ID, "grenade_radiant"),
        FabricEntityTypeBuilder.<RadiantGrenadeEntity>create(SpawnGroup.MISC, RadiantGrenadeEntity::new)
            .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
            .trackRangeBlocks(48).trackedUpdateRate(10).build());

    public static final EntityType<MagicGrenadeEntity> MAGIC_GRENADE_ENTITY = Registry.register(
        Registry.ENTITY_TYPE, new Identifier(GrenadesMod.MOD_ID, "grenade_magic"),
        FabricEntityTypeBuilder.<MagicGrenadeEntity>create(SpawnGroup.MISC, MagicGrenadeEntity::new)
            .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
            .trackRangeBlocks(48).trackedUpdateRate(10).build());

    public static final EntityType<IceGrenadeEntity> ICE_GRENADE_ENTITY = Registry.register(
        Registry.ENTITY_TYPE, new Identifier(GrenadesMod.MOD_ID, "grenade_ice"),
        FabricEntityTypeBuilder.<IceGrenadeEntity>create(SpawnGroup.MISC, IceGrenadeEntity::new)
            .dimensions(EntityDimensions.fixed(0.275F, 0.275F))
            .trackRangeBlocks(48).trackedUpdateRate(10).build());

    public static final EntityType<ConvergenceGrenadeEntity> CONVERGENCE_GRENADE_ENTITY = Registry.register(
        Registry.ENTITY_TYPE, new Identifier(GrenadesMod.MOD_ID, "grenade_convergence"),
        FabricEntityTypeBuilder.<ConvergenceGrenadeEntity>create(SpawnGroup.MISC, ConvergenceGrenadeEntity::new)
            .dimensions(EntityDimensions.fixed(0.275F, 0.35F))
            .trackRangeBlocks(48).trackedUpdateRate(10).build());

    public static final EntityType<DivergenceGrenadeEntity> DIVERGENCE_GRENADE_ENTITY = Registry.register(
        Registry.ENTITY_TYPE, new Identifier(GrenadesMod.MOD_ID, "grenade_divergence"),
        FabricEntityTypeBuilder.<DivergenceGrenadeEntity>create(SpawnGroup.MISC, DivergenceGrenadeEntity::new)
            .dimensions(EntityDimensions.fixed(0.275F, 0.35F))
            .trackRangeBlocks(48).trackedUpdateRate(10).build());

    public static final EntityType<UpheavalGrenadeEntity> UPHEAVAL_GRENADE_ENTITY = Registry.register(
        Registry.ENTITY_TYPE, new Identifier(GrenadesMod.MOD_ID, "grenade_upheaval"),
        FabricEntityTypeBuilder.<UpheavalGrenadeEntity>create(SpawnGroup.MISC, UpheavalGrenadeEntity::new)
            .dimensions(EntityDimensions.fixed(0.27F, 0.325F))
            .trackRangeBlocks(48).trackedUpdateRate(10).build());

    // misc
    public static final EntityType<FragmentEntity> FRAGMENT_ENTITY = Registry.register(
        Registry.ENTITY_TYPE, new Identifier(GrenadesMod.MOD_ID, "fragment"),
        FabricEntityTypeBuilder.<FragmentEntity>create(SpawnGroup.MISC, FragmentEntity::new)
            .dimensions(EntityDimensions.fixed(0.15F, 0.15F))
            .trackRangeBlocks(24).trackedUpdateRate(10).build());

    public static final EntityType<DecoyEntity> DECOY_ENTITY = Registry.register(
        Registry.ENTITY_TYPE, new Identifier(GrenadesMod.MOD_ID, "decoy"),
        FabricEntityTypeBuilder.<DecoyEntity>create(SpawnGroup.MISC, DecoyEntity::new)
            .dimensions(EntityDimensions.fixed(0.6F, 1.8F))
            .trackRangeBlocks(24).trackedUpdateRate(10).build());

    public static void registerEntities() {
        GrenadesMod.LOGGER.warn("Initialising Grenades And Gadgets Entities...");
    }
}
