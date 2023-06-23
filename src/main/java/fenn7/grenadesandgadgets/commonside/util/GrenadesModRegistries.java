package fenn7.grenadesandgadgets.commonside.util;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.entity.misc.DecoyEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

public class GrenadesModRegistries {
    public static void registerRegistries() {
        GrenadesMod.LOGGER.warn("Initialising Grenades And Gadgets Registries...");
        registerAttributes();
    }

    private static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(GrenadesModEntities.DECOY_ENTITY, DecoyEntity.setAttributes());
    }
}
