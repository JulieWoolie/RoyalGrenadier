package net.forthecrown.grenadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import net.forthecrown.royalgrenadier.types.EnumArgumentImpl;

public interface EnumArgument<E extends Enum<E>> extends ArgumentType<E> {

    static <T extends Enum<T>> EnumArgument<T> of(Class<T> clazz){
        return new EnumArgumentImpl<>(clazz);
    }
}
