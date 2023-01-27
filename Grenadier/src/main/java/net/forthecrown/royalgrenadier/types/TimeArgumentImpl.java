package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.exceptions.TranslatableExceptionType;
import net.forthecrown.grenadier.types.TimeArgument;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.forthecrown.royalgrenadier.VanillaMappedArgument;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class TimeArgumentImpl implements TimeArgument, VanillaMappedArgument {
    public static final TimeArgumentImpl INSTANCE = new TimeArgumentImpl();

    public static final TranslatableExceptionType INVALID_UNIT
        = new TranslatableExceptionType("argument.time.invalid_unit");

    @Override
    public Long parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        double initialTime = reader.readDouble();

        if (initialTime < 1) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS
                    .integerTooLow()
                    .createWithContext(
                            GrenadierUtils.correctReader(reader, cursor),
                            initialTime, 1
                    );
        }

        if (!reader.canRead() || Character.isWhitespace(reader.peek())) {
            return (long) initialTime;
        }

        cursor = reader.getCursor();
        String multiplier = reader.readUnquotedString().toLowerCase(Locale.ROOT);
        var suffix = TimeSuffix.BY_LABEL.get(multiplier);

        if (suffix == null) {
            throw INVALID_UNIT.createWithContext(
                    GrenadierUtils.correctReader(reader, cursor)
            );
        }

        return (long) (suffix.getMultiplier() * initialTime);
    }

    private static final List<String> EXAMPLES
        = Arrays.asList("10s", "10m", "10h", "10d", "10w", "10mo", "10yr");

    private static final List<String> SUGGESTIONS
        = Arrays.asList("t", "s", "m", "h", "d", "w", "mo", "yr");

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(
        CommandContext<S> context,
        SuggestionsBuilder builder
    ) {
        String before = builder.getRemaining().toLowerCase();
        String after = before.replaceAll("[^\\d.]", "");

        if (after.isBlank()) {
            return CompletionProvider.suggestMatching(builder, EXAMPLES);
        }

        return CompletionProvider.suggestMatching(builder,
                SUGGESTIONS.stream()
                        .map(s -> after + s)
        );
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public ArgumentType<?> getVanillaArgumentType() {
        return StringArgumentType.word();
    }
}