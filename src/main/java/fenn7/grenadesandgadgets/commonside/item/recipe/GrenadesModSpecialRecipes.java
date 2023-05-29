package fenn7.grenadesandgadgets.commonside.item.recipe;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.item.recipe.custom.FragmentationGrenadeRecipe;
import fenn7.grenadesandgadgets.commonside.item.recipe.custom.SmokeBallGrenadeRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GrenadesModSpecialRecipes {
    public static final SpecialRecipeSerializer<SmokeBallGrenadeRecipe> SMOKE_BALL =
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(GrenadesMod.MOD_ID, "crafting_special_smokeball"),
            new SpecialRecipeSerializer<>(SmokeBallGrenadeRecipe::new));

    public static final SpecialRecipeSerializer<FragmentationGrenadeRecipe> FRAGMENTATION_GRENADE =
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(GrenadesMod.MOD_ID, "crafting_special_fragmentation"),
            new SpecialRecipeSerializer<>(FragmentationGrenadeRecipe::new));

    public static void registerRecipes() {
        GrenadesMod.LOGGER.debug("Initialising Grenades And Gadgets Recipes...");
    }
}
