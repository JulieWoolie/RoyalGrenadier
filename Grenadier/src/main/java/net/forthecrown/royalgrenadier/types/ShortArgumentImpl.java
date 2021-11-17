package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.forthecrown.grenadier.types.ShortArgument;

public class ShortArgumentImpl implements ShortArgument {
    public static final ShortArgumentImpl SIMPLE_INSTANCE = new ShortArgumentImpl(null, null);

    private final IntegerArgumentType handle;

    public ShortArgumentImpl(Short min, Short max) {
        this.handle = IntegerArgumentType.integer(min == null ? Short.MIN_VALUE : min, max == null ? Short.MAX_VALUE : max);
    }

    @Override
    public Short parse(StringReader reader) throws CommandSyntaxException {
        int result = handle.parse(reader);

        return (short) result;
    }

    public IntegerArgumentType getHandle() {
        return handle;
    }
}
