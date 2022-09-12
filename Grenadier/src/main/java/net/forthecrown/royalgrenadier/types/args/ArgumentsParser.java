package net.forthecrown.royalgrenadier.types.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.RequiredArgsConstructor;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.types.args.ArgsArgument;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.kyori.adventure.util.TriState;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static net.forthecrown.grenadier.CompletionProvider.startsWith;
import static net.forthecrown.royalgrenadier.types.args.ArgsArgumentImpl.*;
import static org.apache.commons.lang.StringUtils.INDEX_NOT_FOUND;

@RequiredArgsConstructor
public class ArgumentsParser implements SuggestionProvider<CommandSource> {
    private static final String
            BORDER_START = "{[",
            BORDER_END = "}]";

    public final StringReader reader;
    public final ArgsArgumentImpl argumentType;

    public final ParsedArgsImpl.Builder builder = new ParsedArgsImpl.Builder();

    public int borderType = -1;

    private ArgsSuggestions suggestions = null;

    public void parse() throws CommandSyntaxException {
        suggestStart(reader.getCursor());
        readBracketStart();

        while (true) {
            readKeyValuePair();
            reader.skipWhitespace();

            if (!reader.canRead()
                    || (borderType != -1 && isBorderEnd(reader.peek()))
                    || argumentType.entries.size() <= builder.values.size()
            ) {
                break;
            }
        }

        readBracketEnd();
    }

    private void readKeyValuePair() throws CommandSyntaxException {
        var start = reader.getCursor();
        suggestStart(start);

        var key = readKey();

        if (key.isBlank()) {
            throw EMPTY_LABEL.createWithContext(reader);
        }

        var arg = argumentType.getArg(key);

        if (arg == null) {
            throw UNKNOWN_ARG.createWithContext(
                    GrenadierUtils.correctReader(reader, start),
                    key
            );
        }

        if (builder.has(arg)) {
            throw DUPLICATE_ARG.createWithContext(
                    GrenadierUtils.correctReader(reader, start),
                    key
            );
        }

        reader.skipWhitespace();

        suggests(reader.getCursor(), this::suggestSeparator);

        expectSeparator();
        reader.skipWhitespace();

        int cursor = reader.getCursor();
        suggestions = (context, builder1) -> {
            builder1 = builder1.createOffset(cursor);
            return arg.getParser().listSuggestions(context, builder1);
        };

        if (!reader.canRead()) {
            throw INPUT_REQUIRED.createWithContext(reader);
        }

        Object result = arg.getParser().parse(reader);

        if (reader.canRead()
                && !isBorderEnd(reader.peek())
                && !Character.isWhitespace(reader.peek())
        ) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS
                    .dispatcherExpectedArgumentSeparator()
                    .createWithContext(reader);
        }

        if (reader.canRead() && Character.isWhitespace(reader.peek())) {
            suggestEnd(reader.getCursor() + 1);
        }

        builder.add(arg, result);
    }

    public String readKey() {
        int start = reader.getCursor();

        while (reader.canRead()
                && !isSeparator(reader.peek())
                && !Character.isWhitespace(reader.peek())
        ) {
            reader.skip();
        }

        return reader.getString().substring(start, reader.getCursor());
    }

    public boolean isSeparator(char c) {
        return c == ArgsArgument.COLON_SEPARATOR || c == ArgsArgument.EQUALS_SEPARATOR;
    }

    public boolean isBorderEnd(char c) {
        return BORDER_END.indexOf(c) != INDEX_NOT_FOUND;
    }

    public void readBracketStart() throws CommandSyntaxException {
        if (argumentType.bracketsForced() == TriState.FALSE) {
            return;
        }

        if (!reader.canRead()) {
            if (argumentType.bracketsForced() == TriState.TRUE) {
                throw EXPECTED_BORDER
                        .createWithContext(reader);
            } else {
                return;
            }
        }

        char peeked = reader.peek();
        borderType = BORDER_START.indexOf(peeked);

        if (borderType == -1) {
            if (argumentType.bracketsForced() == TriState.TRUE) {
                throw EXPECTED_BORDER
                        .createWithContext(reader);
            }
        } else {
            reader.skip();
            reader.skipWhitespace();
        }
    }

    public void readBracketEnd() throws CommandSyntaxException {
        if (argumentType.bracketsForced() == TriState.FALSE) {
            return;
        }

        if (argumentType.bracketsForced() == TriState.NOT_SET && borderType == -1) {
            return;
        }

        reader.expect(BORDER_END.charAt(borderType));
    }

    public void expectSeparator() throws CommandSyntaxException {
        if (!reader.canRead() || !isSeparator(reader.peek())) {
            throw EXPECTED_SEPARATOR.createWithContext(reader);
        }

        reader.skip();
    }

    // --- SUGGESTIONS ---

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSource> context,
                                                         SuggestionsBuilder builder
    ) {
        if (this.suggestions == null) {
            suggestStart(builder.getInput().lastIndexOf(' ') + 1);
        }

        return suggestions.getSuggestions(context, builder);
    }

    public void suggestKeys(CommandSource source, SuggestionsBuilder builder) {
        var token = builder.getRemainingLowerCase();

        for (var arg : argumentType.entries) {
            if (this.builder.has(arg.argument)) {
                continue;
            }

            var name = arg.argument.getName();

            if (!arg.argument.getRequires().test(source)) {
                continue;
            }

            if (!startsWith(token, name)) {
                continue;
            }

            builder.suggest(name);
        }
    }

    private void suggestStartBracket(CommandSource source, SuggestionsBuilder builder) {
        CompletionProvider.suggestMatching(builder, BORDER_START.split(""));
    }

    private void suggestEndBracket(CommandSource source, SuggestionsBuilder builder) {
        if (borderType == -1) {
            return;
        }

        var endChar = BORDER_END.charAt(borderType);
        CompletionProvider.suggestMatching(builder, endChar + "");
    }

    private void suggestSeparator(CommandSource source, SuggestionsBuilder builder) {
        CompletionProvider.suggestMatching(builder, "=", ":");
    }

    private void suggestStart(int cursor) {
        if (borderType == -1 && argumentType.bracketsForced() != TriState.FALSE) {
            suggests(cursor, this::suggestKeys, this::suggestStartBracket);
        } else {
            suggests(cursor, this::suggestKeys);
        }
    }

    private void suggestEnd(int cursor) {
        if (borderType != -1) {
            suggests(cursor, this::suggestEndBracket, this::suggestKeys);
        } else {
            suggests(cursor, this::suggestKeys);
        }
    }

    @SafeVarargs
    final void suggests(int cursor, BiConsumer<CommandSource, SuggestionsBuilder>... biConsumer) {
        suggestions = (context, builder1) -> {
            builder1 = builder1.createOffset(cursor);

            for (var c: biConsumer) {
                c.accept(context.getSource(), builder1);
            }

            return builder1.buildFuture();
        };
    }

    private interface ArgsSuggestions extends SuggestionProvider<CommandSource> {
        @Override
        CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder builder);
    }
}