package net.forthecrown.grenadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.royalgrenadier.types.EnumArgumentImpl;

import java.util.concurrent.CompletableFuture;

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

    Class<E> getEnumType();

    <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder, boolean lowerCase);
}