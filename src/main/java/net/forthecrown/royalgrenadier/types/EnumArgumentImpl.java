package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.forthecrown.grenadier.types.EnumArgument;

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
            throw unknownEnum.createWithContext(GrenadierUtils.correctCursorReader(reader, cursor), name);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return listSuggestions(context, builder, true);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder, boolean lowerCase){
        return CommandSource.suggestMatching(builder, GrenadierUtils.convertArray(clazz.getEnumConstants(), e -> lowerCase ? e.name().toLowerCase() : e.name()));
    }
}
