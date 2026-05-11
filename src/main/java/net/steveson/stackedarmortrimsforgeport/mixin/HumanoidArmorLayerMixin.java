package net.steveson.stackedarmortrimsforgeport.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.steveson.stackedarmortrimsforgeport.ArmorTrimList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collections;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    @Shadow
    @Mutable
    @Final
    private final TextureAtlas armorTrimAtlas;

    @Shadow protected abstract void renderTrim(ArmorMaterial pArmorMaterial, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, ArmorTrim pTrim, net.minecraft.client.model.Model pModel, boolean pInnerTexture);

//    @Shadow protected abstract void renderGlint(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, A pModel);


    public HumanoidArmorLayerMixin(RenderLayerParent<T, M> pRenderer, TextureAtlas armorTrimsAtlas) {
        super(pRenderer);
        this.armorTrimAtlas = armorTrimsAtlas;
    }

    @Inject(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasFoil()Z", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void mixinRenderTrim(PoseStack pPoseStack, MultiBufferSource pBuffer, T pLivingEntity, EquipmentSlot pSlot, int pPackedLight, A pModel, CallbackInfo ci, ItemStack itemstack, Item $$9, ArmorItem armoritem, Model model, boolean flag) {
        ArmorTrimList.getTrims(pLivingEntity.level().registryAccess(), itemstack).ifPresent((armorTrims) -> {
            Collections.reverse(armorTrims);
            for (ArmorTrim armorTrim : armorTrims) {
                renderTrim(armoritem.getMaterial(), pPoseStack, pBuffer, pPackedLight, armorTrim, model, flag);
            }
        });
//        if (itemstack.hasFoil()) {
//            renderGlint(pPoseStack, pBuffer, pPackedLight, pModel);
//        }
    }
}
