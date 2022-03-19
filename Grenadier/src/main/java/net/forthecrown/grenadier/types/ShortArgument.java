package net.forthecrown.grenadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.forthecrown.royalgrenadier.types.ShortArgumentImpl;

public interface ShortArgument extends ArgumentType<Short> {
    static ShortArgument shortArg() {
        return ShortArgumentImpl.SIMPLE_INSTANCE;
    }

    static ShortArgument shortArg(short min) {
        return new ShortArgumentImpl(min, null);
    }

    static ShortArgument shortArg(short min, short max) {
        return new ShortArgumentImpl(min, max);
    }

    static short getShort(CommandContext<?> c, String argument) {
        return c.getArgument(argument, Short.class);
    }
}