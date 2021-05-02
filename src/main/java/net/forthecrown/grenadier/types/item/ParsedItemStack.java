package net.forthecrown.grenadier.types.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.inventory.ItemStack;

public interface ParsedItemStack {
    ItemStack create(int amount, boolean nbt) throws CommandSyntaxException;

    default ItemStack singular(boolean nbt) throws CommandSyntaxException {
        return create(1, nbt);
    }

    default ItemStack noNBT(int amount) throws CommandSyntaxException {
        return create(amount, false);
    }
}
