package net.forthecrown.grenadier.types.item;

import com.mojang.brigadier.arguments.ArgumentType;
import net.forthecrown.royalgrenadier.types.item.ItemArgumentImpl;

public interface ItemArgument extends ArgumentType<ParsedItemStack> {
    static ItemArgument itemStack(){
        return ItemArgumentImpl.INSTANCE;
    }
}
