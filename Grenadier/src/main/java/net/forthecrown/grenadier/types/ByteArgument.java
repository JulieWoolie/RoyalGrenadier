package net.forthecrown.grenadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.types.ByteArgumentImpl;

public interface ByteArgument extends ArgumentType<Byte> {
    static ByteArgument byteArg() {
        return ByteArgumentImpl.SIMPLE_INSTANCE;
    }

    static ByteArgument byteArg(byte min) {
        return new ByteArgumentImpl(min, null);
    }

    static ByteArgument byteArg(byte min, byte max) {
        return new ByteArgumentImpl(min, max);
    }

    static byte getByte(CommandContext<CommandSource> c, String argument) {
        return c.getArgument(argument, Byte.class);
    }
}
