package net.steveson.stackedarmortrimsforgeport.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.steveson.stackedarmortrimsforgeport.StackedArmorTrimsForgeMod;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, StackedArmorTrimsForgeMod.MOD_ID);

    public static final RegistryObject<RecipeSerializer<SmithingTrimRemoveRecipe>> SMITHING_TRIM_REMOVE_SERIALIZER =
            SERIALIZERS.register("smithing_trim_remove", ()-> SmithingTrimRemoveRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
