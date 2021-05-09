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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ArrayArgumentImpl<V> implements ArrayArgument<V> {

    private final ArgumentType<V> type;
    public static final DynamicCommandExceptionType PARSING_ERROR = new DynamicCommandExceptionType(o -> () -> "Error parsing array: " + o);

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
        String splittable = getSplittableString(reader);
        String[] array = splittable.split(",");
        System.out.println(Arrays.toString(array));

        for (String s: array){
            try {
                result.add(type.parse(new StringReader(s)));
            } catch (CommandSyntaxException e){
                throw PARSING_ERROR.createWithContext(GrenadierUtils.correctCursorReader(reader, reader.getString().indexOf(s)), e.getRawMessage().getString());
            }
        }

        reader.setCursor(reader.getCursor() + splittable.length());
        return result;
    }

    private String getSplittableString(StringReader reader){
        String remaining = reader.getRemaining();
        if(remaining.indexOf(' ') == -1) return remaining;

        return remaining.substring(0, remaining.indexOf(' '));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String input = builder.getInput();
        int index = input.lastIndexOf(',');
        if(index != -1) builder = builder.createOffset(index + 1);

        return type.listSuggestions(context, builder);
    }
}
