package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.types.ArrayArgument;
import net.forthecrown.royalgrenadier.GrenadierUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ArrayArgumentImpl<V> implements ArrayArgument<V> {

    private final ArgumentType<V> type;
    public static final DynamicCommandExceptionType PARSING_ERROR = new DynamicCommandExceptionType(o -> () -> "Error parsing array: " + o);
    public static final DynamicCommandExceptionType ELEMENT_ALREADY_USED = new DynamicCommandExceptionType(o -> () -> "Value already used: '" + o + "'");

    public ArrayArgumentImpl(ArgumentType<V> type){
        this.type = type;
    }

    @Override
    public ArgumentType<V> getType() {
        return type;
    }

    @Override
    public Collection<V> parse(StringReader reader) throws CommandSyntaxException {
        List<V> result = new ArrayList<>();

        while (reader.canRead()) {
            reader.skipWhitespace();

            int cursor = reader.getCursor();
            V parsed = getType().parse(reader);

            if (result.contains(parsed)) {
                ELEMENT_ALREADY_USED.createWithContext(
                        GrenadierUtils.correctReader(reader, cursor),
                        parsed
                );
            }

            result.add(parsed);

            if (reader.canRead() && reader.peek() == ',') {
                reader.skip();
            } else {
                break;
            }
        }

        return result;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        builder = builder.createOffset(builder.getStart() + builder.getRemainingLowerCase().indexOf(',') + 1);
        return type.listSuggestions(context, builder);
    }
}