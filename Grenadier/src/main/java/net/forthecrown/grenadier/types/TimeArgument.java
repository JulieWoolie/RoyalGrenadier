package net.forthecrown.grenadier.types;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import lombok.Getter;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.types.TimeArgumentImpl;

import java.util.concurrent.TimeUnit;

/**
 * Parses an input into milliseconds.
 * <p>
 * An example of a valid input for this argument is:
 * "365d", which is parsed into 365 days, aka 31,5 billion
 * milliseconds.
 * <p>
 * If the given input does not have a suffix then the
 * initial value is returned.
 * @see TimeSuffix All valid time suffixes
 */
public interface TimeArgument extends ArgumentType<Long> {

    /**
     * Gets the time argument instance
     * @return The argument instance
     */
    static TimeArgument time() {
        return TimeArgumentImpl.INSTANCE;
    }

    /**
     * Gets the parsed time as ticks from the given
     * context.
     * <p>
     * This simply calls {@link #getMillis(CommandContext, String)}
     * and divides the result by 50
     * @param c The context to get the time from
     * @param argument The name of the argument
     * @return The gotten time
     */
    static long getTicks(CommandContext<CommandSource> c, String argument) {
        return getMillis(c, argument) / TimeSuffix.TICK.getMultiplier();
    }

    /**
     * Gets the parsed time from the given
     * command context.
     * @param c The context to get the time from
     * @param argument The name of the argument
     * @return The gotten time
     */
    static long getMillis(CommandContext<CommandSource> c, String argument) {
        return c.getArgument(argument, Long.class);
    }

    /**
     * A valid time suffix for parsing
     * <p>
     * This enum holds both the valid labels
     * for all suffixes and their scaling
     * values that are applied to the
     * initial result.
     */
    enum TimeSuffix {
        YEAR (TimeUnit.DAYS.toMillis(365),
                "y", "yr", "year", "years"
        ),

        MONTH (TimeUnit.DAYS.toMillis(28),
                "mo", "month", "months"
        ),

        WEEK (TimeUnit.DAYS.toMillis(7),
                "w", "week", "weeks"
        ),

        DAY (TimeUnit.DAYS.toMillis(1),
                "d", "day", "days"
        ),

        HOUR (TimeUnit.HOURS.toMillis(1),
                "h", "hour", "hours"
        ),

        MINUTE (TimeUnit.MINUTES.toMillis(1),
                "m", "min", "mins", "minute", "minutes"
        ),

        SECOND (TimeUnit.SECONDS.toMillis(1),
                "s", "sec", "secs", "second", "seconds"
        ),

        TICK (50,
                "t", "ticks", "tick"
        );

        @Getter
        private final long multiplier;

        private final String[] labels;

        /**
         * A label -> suffix lookup map for faster suffix
         * lookups during parsing
         */
        public static final ImmutableMap<String, TimeSuffix> BY_LABEL;

        static {
            ImmutableMap.Builder<String, TimeSuffix> map = ImmutableMap.builder();

            for (var v: values()) {
                for (var s: v.labels) {
                    map.put(s.toLowerCase(), v);
                }
            }

            BY_LABEL = map.build();
        }

        TimeSuffix(long multiplier, String... labels) {
            this.multiplier = multiplier;
            this.labels = labels;
        }
    }
}