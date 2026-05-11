package net.steveson.stackedarmortrimsforgeport.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.steveson.stackedarmortrimsforgeport.util.Helper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMixin extends ItemCombinerMenu{

    public SmithingMenuMixin(@Nullable MenuType<?> pType, int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(pType, pContainerId, pPlayerInventory, pAccess);
    }


    @Inject(method = "onTake", at = @At(value = "HEAD", remap = false), cancellable = true)
    protected void onTake(Player pPlayer, ItemStack pStack, CallbackInfo ci) {

        if (this.inputSlots.getItem(2).isDamageableItem() || this.inputSlots.getItem(2).getOrCreateTag().contains("Unbreakable")){
            pStack.onCraftedBy(pPlayer.level(), pPlayer, pStack.getCount());
            this.resultSlots.awardUsedRecipes(pPlayer, this.getRelevantItems());
            this.shrinkStackInSlot(1);

            if(this.inputSlots.getItem(2).isDamageableItem()){
                this.inputSlots.setItem(
                        2,
                        Helper.damageCraftingItem(this.inputSlots.getItem(2) , 1)
                );
            }

            this.access.execute((p_40263_, p_40264_) -> {
                p_40263_.levelEvent(1044, p_40264_, 0);
            });

            ci.cancel();
        }
    }

    @Shadow
    protected abstract List<ItemStack> getRelevantItems();

    @Shadow
    protected abstract void shrinkStackInSlot(int pIndex);
}
