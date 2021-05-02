package net.forthecrown.royalgrenadier.types.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.forthecrown.grenadier.types.item.ParsedItemStack;
import net.minecraft.server.v1_16_R3.ArgumentPredicateItemStack;
import org.bukkit.inventory.ItemStack;

public class ParsedItemImpl implements ParsedItemStack {

    private final ArgumentPredicateItemStack nms;

    ParsedItemImpl(ArgumentPredicateItemStack nms){
        this.nms = nms;
    }

    @Override
    public ItemStack create(int amount, boolean nbt) throws CommandSyntaxException {
        return nms.a(amount, nbt).asBukkitMirror();
    }

    public ArgumentPredicateItemStack getNms() {
        return nms;
    }
}
