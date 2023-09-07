package fenn7.grenadesandgadgets.commonside;

import fenn7.grenadesandgadgets.commonside.block.GrenadesModBlockEntities;
import fenn7.grenadesandgadgets.commonside.block.GrenadesModBlocks;
import fenn7.grenadesandgadgets.commonside.damage.GrenadesModDamageSources;
import fenn7.grenadesandgadgets.commonside.entity.GrenadesModEntities;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import fenn7.grenadesandgadgets.commonside.item.network.GrenadesModC2SPackets;
import fenn7.grenadesandgadgets.commonside.item.recipe.GrenadesModSpecialRecipes;
import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModRegistries;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrenadesMod implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final String MOD_ID = "grenadesandgadgets";
    public static final Logger LOGGER = LoggerFactory.getLogger("Grenades And Gadgets");

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        LOGGER.info("Initializing Grenades And Gadgets...");
        GrenadesModItems.registerItems();
        GrenadesModEntities.registerEntities();
        GrenadesModBlocks.registerBlocks();
        GrenadesModBlockEntities.registerBlockEntities();
        GrenadesModSpecialRecipes.registerRecipes();
        GrenadesModStatus.registerEffects();
        GrenadesModRegistries.registerRegistries();
        GrenadesModC2SPackets.registerC2SPackets();
    }
}