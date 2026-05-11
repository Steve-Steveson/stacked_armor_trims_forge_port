package net.steveson.stackedarmortrimsforgeport.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.steveson.stackedarmortrimsforgeport.StackedArmorTrimsForgeGameRules;
import net.steveson.stackedarmortrimsforgeport.StackedArmorTrimsForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(ArmorTrim.class)
public abstract class ArmorTrimMixin {
    private static final Component UPGRADE_TEXT;
    @Shadow
    private static final Codec<ArmorTrim> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(TrimMaterial.CODEC.fieldOf("material").forGetter(ArmorTrim::material), TrimPattern.CODEC.fieldOf("pattern").forGetter(ArmorTrim::pattern)).apply(instance, ArmorTrim::new);
    });

    @Inject(method = "setTrim", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getOrCreateTag()Lnet/minecraft/nbt/CompoundTag;"), cancellable = true)
    private static void setTrim(RegistryAccess pRegistryAccess, ItemStack pArmor, ArmorTrim pTrim, CallbackInfoReturnable<Boolean> cir) {

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        if (server == null) {
            cir.setReturnValue(false);
            return;
        }

        int limit = server.getGameRules().getInt(StackedArmorTrimsForgeGameRules.MAX_TRIM_STACK);

        if (limit == 0) {
            cir.setReturnValue(false);
            return;
        }

        CompoundTag nbt = pArmor.getOrCreateTag();
        if (!nbt.contains("Trims")) {
            ListTag nbtList = new ListTag();
            // There is a Trim NBT but no Trims NBT, that means Stacked Trims was installed after Trims were applied. This code merges these old trims.
            if(nbt.contains("Trim")) {
                ListTag nbtList1 = nbt.getList("Trim", 10);
                Tag nbtElement = nbt.get("Trim");
                if(nbtElement != null) {
                    nbtList.add(nbtElement);
                } else {
                    nbtList.add(nbtList1);
                }
//                assert pArmor.getTag() != null;
//                pArmor.getTag().remove("Trim");
            }

            nbtList.add(ArmorTrim.CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, pRegistryAccess), pTrim).result().orElseThrow());

            pArmor.getOrCreateTag().put("Trim", (Tag)CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, pRegistryAccess), pTrim).result().orElseThrow());
            nbt.put("Trims", nbtList);
            cir.setReturnValue(true);
            return;
        }

        ListTag nbtList = nbt.getList("Trims", 10);
        if(nbtList.isEmpty()) {
            Tag preStacked = nbt.get("Trims");
            if(preStacked != null) {
                nbtList.add(preStacked);
            }
        }

        if(server != null &&
//                !server.getGameRules().getBoolean(StackedArmorTrimsForgeGameRules.ALLOW_DUPLICATE_TRIMS) &&
                server.getGameRules().getBoolean(StackedArmorTrimsForgeGameRules.OVERRIDE_MATCHING_TRIM_PATTERNS)){
            for (int i = nbtList.size() - 1; i >= 0; i--) {
                Tag tag = nbtList.get(i);
                DataResult<ArmorTrim> result = ArmorTrim.CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, pRegistryAccess), tag);

                if (result.result().isPresent() && result.result().get().pattern() == pTrim.pattern()) {
                    nbtList.remove(tag);
                }
            }
        };

        if (nbtList.size() >= limit) {
            cir.setReturnValue(false);
            return;
        }

        nbtList.add(ArmorTrim.CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, pRegistryAccess), pTrim).result().orElseThrow());
        nbt.put("Trims", nbtList);
        pArmor.getOrCreateTag().put("Trim", (Tag)CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, pRegistryAccess), pTrim).result().orElseThrow());
        cir.setReturnValue(true);
    }

    @Inject(method = "getTrim", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getTagElement(Ljava/lang/String;)Lnet/minecraft/nbt/CompoundTag;"), cancellable = true)
//    private static void getLastTrim(DynamicRegistryManager registryManager, ItemStack stack, CallbackInfoReturnable<Optional<ArmorTrim>> cir) {
    private static void getLastTrim(RegistryAccess pRegistryAccess, ItemStack pArmor, CallbackInfoReturnable<Optional<ArmorTrim>> cir) {
        assert pArmor.getTag() != null;
        if(!pArmor.getTag().contains("Trims")) {
//            cir.setReturnValue(Optional.empty());
            return;
        }

        ListTag nbtList = pArmor.getTag().getList("Trims", 10);
        Tag nbtElement;
        if (nbtList.isEmpty())
            nbtElement = pArmor.getTag().get("Trim");
        else
            nbtElement = nbtList.get(nbtList.size()-1);
        if(nbtElement == null) {
            cir.setReturnValue(Optional.empty());
            return;
        }
        DataResult<ArmorTrim> result = ArmorTrim.CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, pRegistryAccess), nbtElement);
        cir.setReturnValue(result.result());
    }

    @Inject(method = "appendUpgradeHoverText", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", shift = At.Shift.BEFORE), cancellable = true)
    private static void appendAdditionalTooltips(ItemStack pArmor, RegistryAccess pRegistryAccess, List<Component> pTooltip, CallbackInfo ci) {

        //always false
        if(StackedArmorTrimsForgeMod.isBetterTrimTooltipsEnables) return; // If BetterTrimTooltips is installed it will handle the tooltip generation instead.

        assert pArmor.getTag() != null;
        ListTag nbtList = pArmor.getTag().getList("Trims", 10);
        if(nbtList == null || nbtList.isEmpty()) return;

        for (Tag nbtElement : nbtList) {
            DataResult<ArmorTrim> result = ArmorTrim.CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, pRegistryAccess), nbtElement);
            ArmorTrim armorTrim = result.result().orElse(null);
            if (armorTrim == null) continue;
            pTooltip.add(UPGRADE_TEXT);
            break;
        }

        for (Tag nbtElement : nbtList) {
            DataResult<ArmorTrim> result = ArmorTrim.CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, pRegistryAccess), nbtElement);
            ArmorTrim armorTrim = result.result().orElse(null);
            if (armorTrim == null) continue;

            pTooltip.add(CommonComponents.space().append(armorTrim.pattern().value().copyWithStyle(armorTrim.material())));
            pTooltip.add(CommonComponents.space().append(armorTrim.material().value().description()));
        }
        ci.cancel();
    }

    static {
        UPGRADE_TEXT = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.upgrade"))).withStyle(ChatFormatting.GRAY);
    }
}
