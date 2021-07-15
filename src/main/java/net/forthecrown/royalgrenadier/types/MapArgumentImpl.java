package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.types.MapArgument;
import net.forthecrown.royalgrenadier.GrenadierUtils;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class MapArgumentImpl<T> implements MapArgument<T> {
    public static final DynamicCommandExceptionType UNKNOWN_KEY = new DynamicCommandExceptionType(o -> () -> "Unknown key: " + o);

    private final MapSupplier<T> mapSupplier;
    public MapArgumentImpl(MapSupplier<T> supplier){
        this.mapSupplier = supplier;
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        String name = reader.readUnquotedString();

        T result = mapSupplier.get().get(name);
        if(result == null) throw UNKNOWN_KEY.createWithContext(GrenadierUtils.correctReader(reader, cursor), name);

        return result;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CompletionProvider.suggestMatching(builder, mapSupplier.get().keySet());
    }

    @Override
    public Collection<String> getExamples() {
        return mapSupplier.get().keySet();
    }
}
