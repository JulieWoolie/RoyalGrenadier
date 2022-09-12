package net.forthecrown.grenadier.types.pos;

import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.royalgrenadier.types.pos.CoordinateSuggestionImpl;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a coordinate suggestion
 */
public interface CoordinateSuggestion {
    /**
     * The default world local 2 coordinate '~' suggestion
     */
    CoordinateSuggestion DEFAULT_WORLD_VEC2 = of("~", "~");

    /**
     * The default command sender local 2 coordinate '^' suggestion
     */
    CoordinateSuggestion DEFAULT_LOCAL_VEC2 = of("^", "^");

    /**
     * The default world local 3 coordinate '~' suggestion
     */
    CoordinateSuggestion DEFAULT_WORLD_VEC3 = of("~", "~", "~");

    /**
     * The default command sender local 3 coordinate '^' suggestion
     */
    CoordinateSuggestion DEFAULT_LOCAL_VEC3 = of("^", "^", "^");

    /**
     * Creates a suggestion with the given
     * coordinates.
     * <p>
     * <b>Note</b>: The given coordinate
     * array must have either 2 or 3 values
     * in it, no more, no less.
     * <p>
     * Delegate method for {@link #of(Component, double...)}
     * @param cords The coordinates to suggest
     * @return The created suggestion
     * @see #of(Component, String...)
     */
    static CoordinateSuggestion of(double... cords) {
        return of(null, cords);
    }

    /**
     * Creates a suggestion with the given
     * tooltip and coordinates
     * <p>
     * <b>Note</b>: The given coordinate
     * array must have either 2 or 3 values
     * in it, no more, no less.
     * <p>
     * Delegate method for {@link #of(Component, String...)}
     * @param tooltip The hover tooltip to show for the suggestion
     * @param cords The coordinates to suggest
     * @return The created suggestion
     * @see #of(Component, String...)
     */
    static CoordinateSuggestion of(Component tooltip, double... cords) {
        Validate.isTrue(cords.length == 2 || cords.length == 3,
                "Invalid length: %s", cords.length
        );

        if (cords.length == 2) {
            return of(tooltip,
                    String.format("%.2f", cords[0]),
                    String.format("%.2f", cords[1])
            );
        }

        return of(
                tooltip,
                String.format("%.2f", cords[0]),
                String.format("%.2f", cords[1]),
                String.format("%.2f", cords[2])
        );
    }

    /**
     * Creates a suggestion with the given
     * strings as coordinates.
     * <p>
     * <b>Note</b>: The given coordinate
     * array must have either 2 or 3 values
     * in it, no more, no less.
     * <p>
     * Delegate method for {@link #of(Component, String...)}
     * @param cords The coordinates to suggest
     * @return The created suggestion
     * @see #of(Component, String...)
     */
    static CoordinateSuggestion of(String... cords) {
        return of(null, cords);
    }

    /**
     * Creates a suggestion with the given
     * tooltip and coordinates
     * <p>
     * <b>Note</b>: The given coordinate
     * array must have either 2 or 3 values
     * in it, no more, no less.
     * @param tooltip The hover tooltip to show for the suggestion
     * @param cords The coordinates to suggest
     * @return The created suggestion
     */
    static CoordinateSuggestion of(Component tooltip, String... cords) {
        Validate.isTrue(cords.length == 2 || cords.length == 3,
                "Invalid length: %s", cords.length
        );

        return new CoordinateSuggestionImpl(tooltip, cords);
    }

    /**
     * The tooltip this suggestion may or may not have
     * @return The suggestion's tooltip
     */
    @Nullable Component tooltip();

    /**
     * Applies suggestions to the given builder
     * @param builder The builder to use for context
     * @param allowDecimals Whether decimal places are allowed in the suggestion
     */
    void applySuggestions(SuggestionsBuilder builder, boolean allowDecimals);

    /**
     * Returns the string representation of this
     * coordinate suggestion.
     * @return The string representation of this
     *         suggestion
     */
    default String asString() {
        return toString();
    }
}