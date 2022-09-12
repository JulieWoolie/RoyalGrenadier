package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.types.EnumArgument;
import net.forthecrown.royalgrenadier.GrenadierUtils;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class EnumArgumentImpl<E extends Enum<E>> implements EnumArgument<E> {
    private final Dynamic2CommandExceptionType
            UNKNOWN = new Dynamic2CommandExceptionType((type, obj) -> () -> "Unknown " + type + ": " + obj.toString());

    @Getter
    private final Class<E> enumType;

    @Override
    public E parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        String name = reader.readUnquotedString();

        try {
            return Enum.valueOf(enumType, name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw UNKNOWN.createWithContext(
                    GrenadierUtils.correctReader(reader, cursor),
                    enumType.getSimpleName(), name
            );
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return listSuggestions(context, builder, true);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder, boolean lowerCase) {
        return CompletionProvider.suggestMatching(builder,
                Stream.of(enumType.getEnumConstants())
                        .map(e -> lowerCase ? e.name().toLowerCase() : e.name())
        );
    }

    @Override
    public Collection<String> getExamples() {
        return Stream.of(enumType.getEnumConstants())
                .map(e -> e.name().toLowerCase())
                .collect(Collectors.toList());
    }
}