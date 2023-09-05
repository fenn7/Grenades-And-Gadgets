package fenn7.grenadesandgadgets.client.screen;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GrenadesModScreens {
    public static ScreenHandlerType<HiddenExplosiveScreenHandler> HIDDEN_EXPLOSIVE_SCREEN_HANDLER =
        Registry.register(Registry.SCREEN_HANDLER, new Identifier(GrenadesMod.MOD_ID, "hidden_explosive"),
            new ScreenHandlerType<>(HiddenExplosiveScreenHandler::new));

    public static void registerScreens() {
        GrenadesMod.LOGGER.warn("Initialising Grenades And Gadgets Screens...");
        ScreenRegistry.register(HIDDEN_EXPLOSIVE_SCREEN_HANDLER, HiddenExplosiveScreen::new);
    }
}
