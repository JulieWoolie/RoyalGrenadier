package net.forthecrown.royalgrenadier.types.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.forthecrown.grenadier.types.item.ParsedItemStack;
import net.minecraft.commands.arguments.item.ItemInput;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ParsedItemImpl implements ParsedItemStack {

    private final ItemInput nms;

    ParsedItemImpl(ItemInput nms){
        this.nms = nms;
    }

    @Override
    public ItemStack create(int amount, boolean nbt) throws CommandSyntaxException {
        return CraftItemStack.asBukkitCopy(nms.createItemStack(amount, nbt));
    }

    public ItemInput getNms() {
        return nms;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack i = CraftItemStack.asNMSCopy(itemStack);
        return nms.test(i);
    }
}
