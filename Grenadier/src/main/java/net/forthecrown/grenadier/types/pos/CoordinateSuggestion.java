package net.forthecrown.grenadier.types.pos;

import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a coordinate suggestion
 * <p></p>
 * The two built in implementations of this interface are {@link Vec2Suggestion} for 2d suggestions, and
 * {@link Vec3Suggestion} for normal suggestions.
 */
public interface CoordinateSuggestion {

    /**
     * The tooltip this suggestion may or may not have
     * @return The suggestion's tooltip
     */
    @Nullable Component tooltip();

    /**
     * Creates suggestions
     * @param builder The builder to use for context
     * @param allowDecimals Whether decimal places are allowed in the suggestion
     */
    void applySuggestions(SuggestionsBuilder builder, boolean allowDecimals);
}
