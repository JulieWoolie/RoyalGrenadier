package net.forthecrown.grenadier.types.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.forthecrown.royalgrenadier.types.item.ItemArgumentImpl;

public interface ItemArgument extends ArgumentType<ParsedItemStack> {
    static ItemArgument itemStack(){
        return ItemArgumentImpl.INSTANCE;
    }

    ParsedItemStack parse(StringReader reader, boolean allowNBT) throws CommandSyntaxException;

    @Override
    default ParsedItemStack parse(StringReader reader) throws CommandSyntaxException {
        return parse(reader, true);
    }
}