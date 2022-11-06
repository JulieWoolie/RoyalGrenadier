package net.forthecrown.grenadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import net.forthecrown.royalgrenadier.types.ArrayArgumentImpl;

import java.util.List;

/**
 * Represents an argument which takes in an comma separated array of items
 * and parses them into the specified type
 * @param <V> The type of items the argument will parse
 */
public interface ArrayArgument<V> extends ArgumentType<List<V>> {
    /**
     * Gets the list's type
     * @return The list's type
     */
    ArgumentType<V> getType();

    /**
     * Creates an array argument with the given type
     * as the array's type
     * @param e The type of elements results will be made of
     * @return The created array argument
     * @param <A> The array type
     */
    static <A> ArrayArgument<A> of(ArgumentType<A> e) {
        return new ArrayArgumentImpl<>(e);
    }
}