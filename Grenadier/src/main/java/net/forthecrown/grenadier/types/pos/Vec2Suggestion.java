package net.forthecrown.grenadier.types.pos;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.util.Strings;

import static net.forthecrown.royalgrenadier.GrenadierUtils.decimal;

public class Vec2Suggestion implements CoordinateSuggestion {
    public static final Vec2Suggestion DEFAULT = new Vec2Suggestion("~", "~");

    private final String x;
    private final String z;

    private final Component tooltip;

    public Vec2Suggestion(String x, String z) {
        this.x = x;
        this.z = z;

        this.tooltip = null;
    }

    public Vec2Suggestion(String x, String z, Component tooltip) {
        this.x = x;
        this.z = z;
        this.tooltip = tooltip;
    }

    public Vec2Suggestion(double x, double z) {
        this(x, z, null);
    }

    public Vec2Suggestion(double x, double z, Component tooltip) {
        this(
                String.format("%.2f", x),
                String.format("%.2f", z),
                tooltip
        );
    }

    public String getX() {
        return x;
    }

    public String getZ() {
        return z;
    }

    @Override
    public Component tooltip() {
        return tooltip;
    }

    @Override
    public void applySuggestions(SuggestionsBuilder builder, boolean allowDecimals) {
        String remaining = builder.getRemainingLowerCase();
        Message tooltip = GrenadierUtils.componentToMessage(tooltip());

        if(Strings.isBlank(remaining)) {
            builder.suggest(decimal(getX(), allowDecimals), tooltip);
            builder.suggest(decimal(getX(), allowDecimals) + ' ' + decimal(getZ(), allowDecimals), tooltip);

            return;
        }

        String[] args = remaining.split(" ");

        if(args.length == 1) {
            GrenadierUtils.suggestMatches(builder, decimal(args[0], allowDecimals) + ' ' + decimal(getZ(), allowDecimals), tooltip);
        } else if(args.length == 2) {
            GrenadierUtils.suggestMatches(builder, decimal(args[0], allowDecimals) + ' ' + decimal(args[1], allowDecimals), tooltip);
        }
    }
}
