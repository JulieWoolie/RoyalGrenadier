package net.forthecrown.grenadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import net.kyori.adventure.text.Component;

import java.util.function.Function;

/**
 * A component supporting version of {@link com.mojang.brigadier.exceptions.DynamicNCommandExceptionType}
 */
public class MutableNCommandExceptionType implements CommandExceptionType {

    private final Function<Object[], Component> function;

    public MutableNCommandExceptionType(Function<Object[], Component> function) {
        this.function = function;
    }

    public RoyalCommandException create(Object... args){
        return new RoyalCommandException(this, function.apply(args));
    }

    public RoyalCommandException createWithContext(ImmutableStringReader reader, Object... args){
        return new RoyalCommandException(this, function.apply(args), reader.getString(), reader.getCursor());
    }
}
