package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.recipes.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers
{
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TravelersBackpack.MODID);

    public static final RegistryObject<RecipeSerializer<?>> BACKPACK_SHAPED = SERIALIZERS.register("backpack_shaped", () -> ShapedBackpackRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<?>> BACKPACK_SHAPELESS = SERIALIZERS.register("backpack_shapeless", () -> ShapelessBackpackRecipe.Serializer.INSTANCE);
    public static final RegistryObject<SimpleCraftingRecipeSerializer<?>> BACKPACK_DYE = SERIALIZERS.register("backpack_dye", () -> new SimpleCraftingRecipeSerializer<>(BackpackDyeRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> BACKPACK_UPGRADE_LEGACY = SERIALIZERS.register("backpack_upgrade_legacy", BackpackUpgradeRecipeLegacy.Serializer::new);
    public static final RegistryObject<RecipeSerializer<?>> BACKPACK_UPGRADE = SERIALIZERS.register("backpack_upgrade", BackpackUpgradeRecipe.Serializer::new);
}