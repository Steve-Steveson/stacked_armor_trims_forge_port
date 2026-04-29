package net.steveson.stackedarmortrimsforgeport.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.steveson.stackedarmortrimsforgeport.util.Helper;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

public class SmithingTrimRemoveRecipe implements SmithingRecipe {
    private final ResourceLocation id;
    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;

    public SmithingTrimRemoveRecipe(ResourceLocation pId, Ingredient pTemplate, Ingredient pBase, Ingredient pAddition) {
        this.id = pId;
        this.template = pTemplate;
        this.base = pBase;
        this.addition = pAddition;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(Container pContainer, Level pLevel) {
        return this.template.test(pContainer.getItem(0)) && this.base.test(pContainer.getItem(1)) && this.addition.test(pContainer.getItem(2));
    }

    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        ItemStack itemstack = pContainer.getItem(1);
        if (this.base.test(itemstack)) {
//            Optional<Holder.Reference<TrimMaterial>> optional = TrimMaterials.getFromIngredient(pRegistryAccess, pContainer.getItem(2));
//            Optional<Holder.Reference<TrimPattern>> optional1 = TrimPatterns.getFromTemplate(pRegistryAccess, pContainer.getItem(0));
            if (pContainer.getItem(2).is(ItemTags.AXES)) {
                Optional<ArmorTrim> optional2 = ArmorTrim.getTrim(pRegistryAccess, itemstack);


                if (optional2.isEmpty()) {
                    return ItemStack.EMPTY;
                }

                ItemStack itemstack1 = itemstack.copy();


                itemstack1.setCount(1);
                ArmorTrim armortrim = optional2.get();
                if (ArmorTrim.setTrim(pRegistryAccess, itemstack1, armortrim)) {
                    return itemstack1;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        ItemStack itemstack = new ItemStack(Items.IRON_CHESTPLATE);
        Optional<Holder.Reference<TrimPattern>> optional = pRegistryAccess.registryOrThrow(Registries.TRIM_PATTERN).holders().findFirst();
        if (optional.isPresent()) {
            Optional<Holder.Reference<TrimMaterial>> optional1 = pRegistryAccess.registryOrThrow(Registries.TRIM_MATERIAL).getHolder(TrimMaterials.REDSTONE);
            if (optional1.isPresent()) {
                ArmorTrim armortrim = new ArmorTrim(optional1.get(), optional.get());
                ArmorTrim.setTrim(pRegistryAccess, itemstack, armortrim);
            }
        }

        return itemstack;
    }

    public boolean isTemplateIngredient(ItemStack pStack) {
        return this.template.test(pStack);
    }

    public boolean isBaseIngredient(ItemStack pStack) {
        return this.base.test(pStack);
    }

    public boolean isAdditionIngredient(ItemStack pStack) {
        return this.addition.test(pStack);
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public RecipeSerializer<?> getSerializer() {
//        return RecipeSerializer.SMITHING_TRIM;
        return null;
    }

    public boolean isIncomplete() {
        return Stream.of(this.template, this.base, this.addition).anyMatch(net.minecraftforge.common.ForgeHooks::hasNoElements);
    }

    public static class Serializer implements RecipeSerializer<SmithingTrimRemoveRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        public SmithingTrimRemoveRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getNonNull(jsonObject, "template"));
            Ingredient ingredient1 = Ingredient.fromJson(GsonHelper.getNonNull(jsonObject, "base"));
            Ingredient ingredient2 = Ingredient.fromJson(GsonHelper.getNonNull(jsonObject, "addition"));
            return new SmithingTrimRemoveRecipe(resourceLocation, ingredient, ingredient1, ingredient2);
        }

        public SmithingTrimRemoveRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            Ingredient ingredient = Ingredient.fromNetwork(friendlyByteBuf);
            Ingredient ingredient1 = Ingredient.fromNetwork(friendlyByteBuf);
            Ingredient ingredient2 = Ingredient.fromNetwork(friendlyByteBuf);
            return new SmithingTrimRemoveRecipe(resourceLocation, ingredient, ingredient1, ingredient2);
        }

        public void toNetwork(FriendlyByteBuf friendlyByteBuf, SmithingTrimRemoveRecipe recipe) {
            recipe.template.toNetwork(friendlyByteBuf);
            recipe.base.toNetwork(friendlyByteBuf);
            recipe.addition.toNetwork(friendlyByteBuf);
        }
    }
}
