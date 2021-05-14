package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.types.TimeArgument;
import net.forthecrown.royalgrenadier.GrenadierUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TimeArgumentImpl implements TimeArgument {
    public static final TimeArgumentImpl INSTANCE = new TimeArgumentImpl();

    public static final DynamicCommandExceptionType UNKNOWN_SUFFIX = new DynamicCommandExceptionType(o -> () -> "Unknown time suffix: " + o);

    @Override
    public Long parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        long initialTime = reader.readInt();
        if(initialTime < 1) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow().createWithContext(GrenadierUtils.correctCursorReader(reader, cursor), initialTime, 1);
        if(!reader.canRead()) return initialTime;

        cursor = reader.getCursor();
        String multiplier = reader.readUnquotedString();
        int multiplierActual = 1;

        switch (multiplier.toLowerCase()){
            case "years":
            case "yr":
                multiplierActual = multiplierActual * 12;

            case "months":
            case "mo":
                multiplierActual = multiplierActual * 4;

            case "weeks":
            case "w":
                multiplierActual = multiplierActual * 7;

            case "days":
            case "d":
                multiplierActual = multiplierActual * 24;

            case "hours":
            case "h":
                multiplierActual = multiplierActual * 60;

            case "mins":
            case "m":
                multiplierActual = multiplierActual * 60;

            case "seconds":
            case "s":
                multiplierActual = multiplierActual * 20;
                break;

            default: throw UNKNOWN_SUFFIX.createWithContext(GrenadierUtils.correctCursorReader(reader, cursor), multiplier);
        }

        return initialTime * multiplierActual;
    }

    private static final List<String> EXAMPLES = Arrays.asList("10s", "10m", "10h", "10d", "10w", "10mo", "10yr");
    private static final List<String> SUGGESTIONS = Arrays.asList("s", "m", "h", "d", "w", "mo", "yr");

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String before = builder.getRemaining().toLowerCase();
        String after = before.replaceAll("[^\\d.]", "");
        if(after.isBlank()) return CommandSource.suggestMatching(builder, EXAMPLES);

        return CommandSource.suggestMatching(builder, GrenadierUtils.convertList(SUGGESTIONS, s -> after + s));
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
