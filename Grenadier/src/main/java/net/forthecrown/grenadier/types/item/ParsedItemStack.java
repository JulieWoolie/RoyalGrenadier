package net.forthecrown.grenadier.types.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

public interface ParsedItemStack extends Predicate<ItemStack> {
    /**
     * Gets the parsed tags
     * @return The tags
     */
    String getTags();

    /**
     * Gets the type of the item
     * @return The parsed material
     */
    Material getType();

    /**
     * Creates an itemstack from this parse result
     * @param amount The amount of items
     * @param checkOverstack True, to check if the given amount
     *                       is greater than the max stack size
     * @return The parsed item
     * @throws CommandSyntaxException If checkOverstack == true && amount > max stack size
     */
    ItemStack create(int amount, boolean checkOverstack) throws CommandSyntaxException;
}