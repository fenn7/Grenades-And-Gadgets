package fenn7.grenadesandgadgets.commonside.item.recipe;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.item.recipe.custom.ClusterRoundRecipe;
import fenn7.grenadesandgadgets.commonside.item.recipe.custom.ExplosiveDisguiseRecipe;
import fenn7.grenadesandgadgets.commonside.item.recipe.custom.FragmentationGrenadeRecipe;
import fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe;
import fenn7.grenadesandgadgets.commonside.item.recipe.custom.MagicGrenadeRecipe;
import fenn7.grenadesandgadgets.commonside.item.recipe.custom.SmokeBallGrenadeRecipe;
import fenn7.grenadesandgadgets.commonside.item.recipe.custom.TemporalFissureGrenadeRecipe;
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

    public static final SpecialRecipeSerializer<MagicGrenadeRecipe> MAGIC_GRENADE =
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(GrenadesMod.MOD_ID, "crafting_special_magic"),
            new SpecialRecipeSerializer<>(MagicGrenadeRecipe::new));

    public static final SpecialRecipeSerializer<TemporalFissureGrenadeRecipe> TEMPORAL_FISSURE_GRENADE =
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(GrenadesMod.MOD_ID, "crafting_special_temporal"),
            new SpecialRecipeSerializer<>(TemporalFissureGrenadeRecipe::new));

    public static final SpecialRecipeSerializer<GrenadeModifierRecipe> GRENADE_MODIFER =
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(GrenadesMod.MOD_ID, "crafting_special_grenade_modifier"),
            new SpecialRecipeSerializer<>(GrenadeModifierRecipe::new));

    public static final SpecialRecipeSerializer<ExplosiveDisguiseRecipe> HIDDEN_EXPLOSIVE_DISGUISE =
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(GrenadesMod.MOD_ID, "crafting_special_hidden_explosive_disguise"),
            new SpecialRecipeSerializer<>(ExplosiveDisguiseRecipe::new));

    public static final SpecialRecipeSerializer<ClusterRoundRecipe> CLUSTER_ROUND =
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(GrenadesMod.MOD_ID, "crafting_special_cluster_round"),
            new SpecialRecipeSerializer<>(ClusterRoundRecipe::new));

    public static void registerRecipes() {
        GrenadesMod.LOGGER.warn("Initialising Grenades And Gadgets Recipes...");
    }
}
