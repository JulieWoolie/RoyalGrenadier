package net.forthecrown.grenadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import net.forthecrown.royalgrenadier.types.ArrayArgumentImpl;

import java.util.Collection;

/**
 * Represents an argument which takes in an comma separated array of items
 * and parses them into the specified type
 * @param <V> The type of items the argument will parse
 */
public interface ArrayArgument<V> extends ArgumentType<Collection<V>> {
    ArgumentType<V> getType();

    static <A> ArrayArgument<A> of(ArgumentType<A> e) {
        return new ArrayArgumentImpl<>(e);
    }
}