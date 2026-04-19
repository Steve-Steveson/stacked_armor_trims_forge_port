package net.steveson.stackedarmortrimsforgeport;

import com.mojang.serialization.DataResult;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ArmorTrimList {
    public static Optional<List<ArmorTrim>> getTrims(RegistryAccess registryManager, ItemStack stack) {
        if (!stack.is(ItemTags.TRIMMABLE_ARMOR)) return Optional.empty();

        CompoundTag nbt = stack.getTag();
        if (nbt == null || !nbt.contains("Trims")) return Optional.empty();

        ListTag nbtList = nbt.getList("Trims", 10);
        if (nbtList.isEmpty()) return Optional.empty();

        List<ArmorTrim> armorTrims = new ArrayList<>(nbtList.size());
        for (Tag element : nbtList) {
            DataResult<ArmorTrim> result = ArmorTrim.CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, registryManager), element);
            result.result().ifPresent(armorTrims::add);
        }

        Collections.reverse(armorTrims);
        return Optional.of(armorTrims);
    }
}
