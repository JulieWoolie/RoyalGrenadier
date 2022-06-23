package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.exceptions.TranslatableExceptionType;
import net.forthecrown.grenadier.types.TimeArgument;
import net.forthecrown.royalgrenadier.GrenadierUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class TimeArgumentImpl implements TimeArgument {
    public static final TimeArgumentImpl INSTANCE = new TimeArgumentImpl();

    public static final TranslatableExceptionType INVALID_UNIT = new TranslatableExceptionType("argument.time.invalid_unit");

    @Override
    public Long parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        long initialTime = reader.readLong();
        if(initialTime < 1) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow().createWithContext(GrenadierUtils.correctReader(reader, cursor), initialTime, 1);
        if(!reader.canRead()) return initialTime;

        cursor = reader.getCursor();
        String multiplier = reader.readUnquotedString();

        return switch (multiplier) {
            case "year",   "years",   "yr" -> TimeUnit.DAYS.toMillis(365) * initialTime;
            case "month",  "months",  "mo" -> TimeUnit.DAYS.toMillis(28) * initialTime;
            case "week",   "weeks",   "w"  -> TimeUnit.DAYS.toMillis(7) * initialTime;
            case "day",    "days",    "d"  -> TimeUnit.DAYS.toMillis(initialTime);
            case "hour",   "hours",   "h"  -> TimeUnit.HOURS.toMillis(initialTime);
            case "minute", "minutes", "m"  -> TimeUnit.MINUTES.toMillis(initialTime);
            case "second", "seconds", "s"  -> TimeUnit.SECONDS.toMillis(initialTime);
            case "tick",   "ticks",   "t"  -> initialTime * 50;

            default -> throw INVALID_UNIT.createWithContext(GrenadierUtils.correctReader(reader, cursor));
        };
    }

    private static final List<String> EXAMPLES = Arrays.asList("10s", "10m", "10h", "10d", "10w", "10mo", "10yr");
    private static final List<String> SUGGESTIONS = Arrays.asList("t", "s", "m", "h", "d", "w", "mo", "yr");

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String before = builder.getRemaining().toLowerCase();
        String after = before.replaceAll("[^\\d.]", "");
        if(after.isBlank()) return CompletionProvider.suggestMatching(builder, EXAMPLES);

        return CompletionProvider.suggestMatching(builder, GrenadierUtils.convertList(SUGGESTIONS, s -> after + s));
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}