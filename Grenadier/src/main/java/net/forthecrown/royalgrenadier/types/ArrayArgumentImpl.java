package net.forthecrown.royalgrenadier.types;

import com.google.common.base.Joiner;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.RequiredArgsConstructor;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.Suggester;
import net.forthecrown.grenadier.types.ArrayArgument;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.forthecrown.royalgrenadier.VanillaMappedArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ArrayArgumentImpl<V> implements ArrayArgument<V>, VanillaMappedArgument {

    private final ArgumentType<V> type;
    public static final DynamicCommandExceptionType PARSING_ERROR = new DynamicCommandExceptionType(o -> () -> "Error parsing array: " + o);
    public static final DynamicCommandExceptionType ELEMENT_ALREADY_USED = new DynamicCommandExceptionType(o -> () -> "Duplicate value: '" + o + "'");

    public ArrayArgumentImpl(ArgumentType<V> type){
        this.type = type;
    }

    @Override
    public ArgumentType<V> getType() {
        return type;
    }

    @Override
    public List<V> parse(StringReader reader) throws CommandSyntaxException {
        ArrayParser<?> parser = new ArrayParser(reader);
        parser.parse();

        return parser.list;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        StringReader reader = new StringReader(builder.getInput());
        reader.setCursor(builder.getStart());

        ArrayParser<S> parser = new ArrayParser<>(reader);

        try {
            parser.parse();
        } catch (CommandSyntaxException ignored) {}

        return parser.getSuggestions(context, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return Collections.singletonList(Joiner.on(',').join(getType().getExamples()));
    }

    @Override
    public ArgumentType<?> getVanillaArgumentType() {
        return StringArgumentType.greedyString();
    }

    @RequiredArgsConstructor
    class ArrayParser<S> implements Suggester<S> {
        private final StringReader reader;
        private Suggester<S> suggester;
        private final List<V> list = new ArrayList<>();

        public void parse() throws CommandSyntaxException {
            int startCursor = reader.getCursor();
            suggestType(startCursor);

            while (true) {
                int cursor = reader.getCursor();
                suggestType(cursor);

                var parsed = getType().parse(reader);

                suggestSeparator(reader.getCursor());

                if (list.contains(parsed)) {
                    ELEMENT_ALREADY_USED.createWithContext(
                            GrenadierUtils.correctReader(reader, cursor),
                            parsed
                    );
                }

                list.add(parsed);

                if (!reader.canRead() || Character.isWhitespace(reader.peek())) {
                    break;
                }

                reader.expect(',');
                reader.skipWhitespace();
            }
        }

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            if (suggester == null) {
                return getType().listSuggestions(context, builder);
            }

            return suggester.getSuggestions(context, builder);
        }

        private void suggestType(int cursor) {
            suggest(cursor, getType()::listSuggestions);
        }

        private void suggestSeparator(int cursor) {
            suggest(cursor,
                    (context, builder) -> CompletionProvider.suggestMatching(builder, ",")
            );
        }

        private void suggest(int cursor, Suggester<S> suggester) {
            this.suggester = (context, builder) -> {
                if (builder.getStart() != cursor) {
                    builder = builder.createOffset(cursor);
                }

                return suggester.getSuggestions(context, builder);
            };
        }
    }
}