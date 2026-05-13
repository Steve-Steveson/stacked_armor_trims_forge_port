package io.github.apfelrauber.stacked_trims.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraftforge.server.ServerLifecycleHooks;
import io.github.apfelrauber.stacked_trims.ArmorTrimList;
import io.github.apfelrauber.stacked_trims.StackedTrimGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(SmithingTrimRecipe.class)
public class SmithingTrimRecipeMixin {
     @Inject(method = "assemble", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/armortrim/ArmorTrim;getTrim(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/ItemStack;)Ljava/util/Optional;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void allowDuplicateTrims(Container pContainer, RegistryAccess pRegistryAccess, CallbackInfoReturnable<ItemStack> cir, ItemStack itemstack, Optional optional, Optional optional1){

         MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if(server != null && !server.getGameRules().getBoolean(StackedTrimGameRules.ALLOW_DUPLICATE_TRIMS))return;

        ItemStack itemStack2 = itemstack.copy();
        itemStack2.setCount(1);
        ArmorTrim armorTrim = new ArmorTrim((Holder)optional.get(), (Holder)optional1.get());
        if (ArmorTrim.setTrim(pRegistryAccess, itemStack2, armorTrim)) {
            cir.setReturnValue(itemStack2);
            cir.cancel();
        }
    }

    @Inject(method = "assemble", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void checkForDuplicateTrims(Container pContainer, RegistryAccess pRegistryAccess, CallbackInfoReturnable<ItemStack> cir, ItemStack itemstack, Optional optional, Optional optional1, Optional optional2){
        ArmorTrimList.getTrims(pRegistryAccess, itemstack).ifPresent((armorTrims) -> {

            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server.getGameRules().getBoolean(StackedTrimGameRules.OVERRIDE_MATCHING_TRIM_PATTERNS)) {

                ArmorTrim armorTrim = armorTrims.get(armorTrims.size() - 1);
                if (armorTrim.hasPatternAndMaterial((Holder) optional1.get(), (Holder) optional.get())) {
                    cir.setReturnValue(ItemStack.EMPTY);
                    cir.cancel();
                    return;
                }
            } else {
                for (ArmorTrim armorTrim : armorTrims) {
                    if (armorTrim.hasPatternAndMaterial((Holder)optional1.get(), (Holder)optional.get())) {
                        cir.setReturnValue(ItemStack.EMPTY);
                        cir.cancel();
                        return;
                    }
                }
            }
        });
    }
}
