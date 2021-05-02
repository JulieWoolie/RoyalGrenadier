package net.forthecrown.grenadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import net.forthecrown.royalgrenadier.types.ArrayArgumentImpl;

import java.util.Collection;

public interface ArrayArgument<V> extends ArgumentType<Collection<V>> {
    ArgumentType<V> getType();

    static <A> ArrayArgument<A> of(ArgumentType<A> e){
        return new ArrayArgumentImpl<>(e);
    }
}
