package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.forthecrown.grenadier.types.ByteArgument;

public class ByteArgumentImpl implements ByteArgument {
    public static final ByteArgumentImpl SIMPLE_INSTANCE = new ByteArgumentImpl(null, null);

    private final IntegerArgumentType handle;

    public ByteArgumentImpl(Byte min, Byte max) {
        handle = IntegerArgumentType.integer(min == null ? Byte.MIN_VALUE : min, max == null ? Byte.MAX_VALUE : max);
    }

    @Override
    public Byte parse(StringReader reader) throws CommandSyntaxException {
        int result = handle.parse(reader);

        return (byte) result;
    }

    public IntegerArgumentType getHandle() {
        return handle;
    }
}
