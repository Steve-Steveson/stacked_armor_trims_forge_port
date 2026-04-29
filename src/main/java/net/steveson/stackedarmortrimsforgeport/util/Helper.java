package net.steveson.stackedarmortrimsforgeport.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;

public class Helper {

    public static ItemStack damageCraftingItem(ItemStack stack, int amount)
    {
        Player player = ForgeHooks.getCraftingPlayer(); // Mods may not set this properly
        if (player != null)
        {
            stack.hurtAndBreak(amount, player, entity -> {});
        }
        else
        {
            damageItem(stack, amount);
        }
        return stack;
    }


    public static void damageItem(ItemStack stack, int amount)
    {
        // There's no player here, so we can't safely do anything.
        //amount = stack.getItem().damageItem(stack, amount, null, e -> {});
        if (stack.hurt(amount, RandomSource.create(), null))
        {
            stack.shrink(1);
            stack.setDamageValue(0);
        }
    }

}
