package net.steveson.stackedarmortrimsforgeport.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
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

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
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
        if (!this.base.test(pContainer.getItem(1)))
            return false;
        if (!this.addition.test(pContainer.getItem(2)))
            return false;
//        if(this.template.isEmpty() && pContainer.getItem(0).isEmpty())
//            return true;
        if(pContainer.getItem(0).isEmpty() && this.template.test(Items.BARRIER.getDefaultInstance())) {
//            System.out.println(pContainer.getItem(0)); //0 air
//            System.out.println(pContainer.getItem(2));
//            System.out.println(this.template.toString()); //random hexcode
//            System.out.println(Arrays.stream(this.addition.getItems()).toList());
            System.out.println(Arrays.stream(this.template.getItems()).toList());
//            System.out.println(this.template.isEmpty());
//            System.out.println(this.template.test(ItemStack.EMPTY));
//            System.out.println(Items.AIR.getDefaultInstance()); //0 air
            System.out.println(this.template.test(Items.BARRIER.getDefaultInstance()));
            return true;
        }
//        if (this.template.test(Items.AIR) == null)
//            return false;
        return this.template.test(pContainer.getItem(0));
//        return true;
    }

    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        ItemStack itemstack = pContainer.getItem(1);
        if (this.base.test(itemstack)) {
//            Optional<Holder.Reference<TrimMaterial>> optional = TrimMaterials.getFromIngredient(pRegistryAccess, pContainer.getItem(2));
            Optional<Holder.Reference<TrimPattern>> optional1 = TrimPatterns.getFromTemplate(pRegistryAccess, pContainer.getItem(0));
            if (pContainer.getItem(2).is(ItemTags.AXES)) {
                Optional<ArmorTrim> optional2 = ArmorTrim.getTrim(pRegistryAccess, itemstack);


                if (optional2.isEmpty()) {
                    return ItemStack.EMPTY;
                }

                ItemStack itemstack1 = itemstack.copy();

                CompoundTag nbt = itemstack.getOrCreateTag();
                if(nbt.contains("Trims")){
                    ListTag nbtList = nbt.getList("Trims", 10);

                    int indexToRemove = -1;

                    if(optional1.isPresent()){
                        for (int i = nbtList.size() - 1; i >= 0; i--) {
                            Tag tag = nbtList.get(i);
                            DataResult<ArmorTrim> result = ArmorTrim.CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, pRegistryAccess), tag);

                            if (result.result().isPresent() && result.result().get().pattern() == optional1.get()) {
                                indexToRemove = i;
                                break;
                            }
                        }
                    }
                    else {
                        indexToRemove = nbtList.size() - 1;
                    }

                    if (indexToRemove == -1) {
                        return ItemStack.EMPTY;
                    }

                    CompoundTag nbtOutput = itemstack1.getOrCreateTag();
                    ListTag nbtListOutput = nbtOutput.getList("Trims", 10);

                    nbtListOutput.remove(indexToRemove);

                    if (nbtList.size() == 1) {
                        nbtOutput.remove("Trims");
                        nbtOutput.remove("Trim");
                    }
                    else if (indexToRemove == nbtList.size() - 1) {
                        System.out.println("I removed the last item!");
                        nbtOutput.put("Trim", nbtListOutput.get(nbtListOutput.size() - 1).copy());
                    }

                    return itemstack1;
                }
                else if(itemstack.getOrCreateTag().contains("Trim")){
                    if(optional1.isPresent()){
                        if(optional2.get().pattern() == optional1.get()){
                            itemstack1.getTag().remove("Trim");
                        }
                        else {
                            return ItemStack.EMPTY;
                        }
                    }
                    else {
                        itemstack1.getTag().remove("Trim");
                    }
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
