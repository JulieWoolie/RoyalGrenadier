package net.forthecrown.grenadier.types.pos;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.Nullable;

import static net.forthecrown.royalgrenadier.GrenadierUtils.decimal;

/**
 * A class which holds 3 string suggestions for suggesting coordinates.
 */
public class Vec3Suggestion implements CoordinateSuggestion {
    /**
     * The default ~ ~ ~ coordinate suggestion
     */
    public static final Vec3Suggestion DEFAULT = new Vec3Suggestion("~", "~", "~");

    private final String x;
    private final String y;
    private final String z;

    private final Component tooltip;

    public Vec3Suggestion(String x, String y, String z, Component tooltip) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.tooltip = tooltip;
    }

    public Vec3Suggestion(String x, String y, String z) {
        this(x, y, z, null);
    }

    public Vec3Suggestion(double x, double y, double z){
        this(x + "", y + "", z + "", null);
    }

    public Vec3Suggestion(double x, double y, double z, Component tooltip){
        this(x + "", y + "", z + "", tooltip);
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getZ() {
        return z;
    }

    @Override
    public String toString() {
        return x + ' ' + y + ' ' + z;
    }

    @Override
    public @Nullable Component tooltip() {
        return tooltip;
    }

    @Override
    public void applySuggestions(SuggestionsBuilder builder, boolean allowDecimals) {
        String remaining = builder.getRemainingLowerCase();
        Message tooltip = GrenadierUtils.componentToMessage(tooltip());

        if(Strings.isBlank(remaining)) {
            builder.suggest(decimal(getX(), allowDecimals), tooltip);
            builder.suggest(decimal(getX(), allowDecimals) + ' ' + decimal(getY(), allowDecimals), tooltip);
            builder.suggest(decimal(getX(), allowDecimals) + ' ' + decimal(getY(), allowDecimals) + ' ' + decimal(getZ(), allowDecimals), tooltip);

            return;
        }

        String[] args = remaining.split(" ");

        if(args.length == 1) {
            GrenadierUtils.suggestMatches(builder, decimal(args[0], allowDecimals) + ' ' + decimal(getY(), allowDecimals) + ' ' + decimal(getZ(), allowDecimals), tooltip);
        } else if(args.length == 2) {
            GrenadierUtils.suggestMatches(builder, decimal(args[0], allowDecimals) + ' ' + decimal(args[1], allowDecimals) + ' ' + decimal(getZ(), allowDecimals), tooltip);
        } else if(args.length == 3) {
            GrenadierUtils.suggestMatches(builder, decimal(args[0], allowDecimals) + ' ' + decimal(args[1], allowDecimals) + ' ' + decimal(args[2], allowDecimals), tooltip);
        }
    }
}
