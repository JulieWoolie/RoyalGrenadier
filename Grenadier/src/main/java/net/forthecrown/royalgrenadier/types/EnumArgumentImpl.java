package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.types.EnumArgument;
import net.forthecrown.royalgrenadier.GrenadierUtils;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class EnumArgumentImpl<E extends Enum<E>> implements EnumArgument<E> {

    private final Class<E> clazz;
    private final DynamicCommandExceptionType unknownEnum;

    public EnumArgumentImpl(Class<E> clazz){
        this.clazz = clazz;
        this.unknownEnum = new DynamicCommandExceptionType(obj -> () -> "Unknown " + clazz.getSimpleName() + ": " + obj.toString());
    }

    @Override
    public E parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        String name = reader.readUnquotedString();

        try {
            return Enum.valueOf(clazz, name.toUpperCase());
        } catch (IllegalArgumentException e){
            throw unknownEnum.createWithContext(GrenadierUtils.correctReader(reader, cursor), name);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return listSuggestions(context, builder, true);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder, boolean lowerCase){
        return CompletionProvider.suggestMatching(builder, GrenadierUtils.convertArray(clazz.getEnumConstants(), e -> lowerCase ? e.name().toLowerCase() : e.name()));
    }

    @Override
    public Collection<String> getExamples() {
        return GrenadierUtils.convertArray(clazz.getEnumConstants(), e -> e.name().toLowerCase());
    }
}
