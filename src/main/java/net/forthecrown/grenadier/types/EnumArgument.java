package net.forthecrown.grenadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import net.forthecrown.royalgrenadier.types.EnumArgumentImpl;

/**
 * Represents an argument type which parses a specified Enum class
 * @param <E> The enum class to parse
 */
public interface EnumArgument<E extends Enum<E>> extends ArgumentType<E> {

    /**
     * Returns an EnumArgument for the specified enum class
     * @param clazz The class of the enum
     * @param <T> The enum itself
     * @return An EnumArgument for that enum
     */
    static <T extends Enum<T>> EnumArgument<T> of(Class<T> clazz){
        return new EnumArgumentImpl<>(clazz);
    }
}
