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

public class TimeArgumentImpl implements TimeArgument {
    public static final TimeArgumentImpl INSTANCE = new TimeArgumentImpl();

    public static final TranslatableExceptionType INVALID_UNIT = new TranslatableExceptionType("argument.time.invalid_unit");

    public static final long
            SECOND_IN_MILLIS    = 1000,
            MINUTE_IN_MILLIS    = SECOND_IN_MILLIS * 60,
            HOUR_IN_MILLIS      = MINUTE_IN_MILLIS * 60,
            DAY_IN_MILLIS       = HOUR_IN_MILLIS * 24,
            WEEK_IN_MILLIS      = DAY_IN_MILLIS * 7,
            MONTH_IN_MILLIS     = DAY_IN_MILLIS * 31,
            YEAR_IN_MILLIS      = DAY_IN_MILLIS * 365;

    @Override
    public Long parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        long initialTime = reader.readInt();
        if(initialTime < 1) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow().createWithContext(GrenadierUtils.correctReader(reader, cursor), initialTime, 1);
        if(!reader.canRead()) return initialTime;

        cursor = reader.getCursor();
        String multiplier = reader.readUnquotedString();

        long multiplierActual = switch (multiplier) {
            case "year", "years", "yr" -> YEAR_IN_MILLIS;
            case "month", "months", "mo" -> MONTH_IN_MILLIS;
            case "week", "weeks", "w" -> WEEK_IN_MILLIS;
            case "day", "days", "d" -> DAY_IN_MILLIS;
            case "hour", "hours", "h" -> HOUR_IN_MILLIS;
            case "minute", "minutes", "m" -> MINUTE_IN_MILLIS;
            case "second", "seconds", "s" -> SECOND_IN_MILLIS;

            default -> throw INVALID_UNIT.createWithContext(GrenadierUtils.correctReader(reader, cursor));
        };

        return initialTime * multiplierActual;
    }

    private static final List<String> EXAMPLES = Arrays.asList("10s", "10m", "10h", "10d", "10w", "10mo", "10yr");
    private static final List<String> SUGGESTIONS = Arrays.asList("s", "m", "h", "d", "w", "mo", "yr");

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
