package net.forthecrown.grenadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import net.kyori.adventure.text.Component;

import java.util.function.Function;

/**
 * Represents an exception type with a single object input
 */
public class MutableCommandExceptionType implements CommandExceptionType {

    /**
     * The function which turns the input into the message
     */
    private final Function<Object, Component> function;

    public MutableCommandExceptionType(Function<Object, Component> function) {
        this.function = function;
    }

    /**
     * Creates the exception with the given input
     * @param arg The input
     * @return The created exception
     */
    public RoyalCommandException create(Object arg){
        return new RoyalCommandException(this, function.apply(arg));
    }

    /**
     * Creates the exception with the given input and reader
     * @param reader The context
     * @param arg The input
     * @return The created exception
     */
    public RoyalCommandException createWithContext(ImmutableStringReader reader, Object arg){
        return new RoyalCommandException(this, function.apply(arg), reader.getString(), reader.getCursor());
    }

    public Function<Object, Component> getFunction() {
        return function;
    }
}
