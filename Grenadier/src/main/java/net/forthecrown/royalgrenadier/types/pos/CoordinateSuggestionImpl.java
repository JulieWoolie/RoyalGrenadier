package net.forthecrown.royalgrenadier.types.pos;

import com.google.common.base.Joiner;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CmdUtil;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.types.pos.CoordinateSuggestion;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import java.util.StringJoiner;

public class CoordinateSuggestionImpl implements CoordinateSuggestion {
    private final String[] suggestions;
    private final Component tooltip;

    public CoordinateSuggestionImpl(Component tooltip, String[] suggestions) {
        Validate.isTrue(suggestions.length > 1 && suggestions.length < 4);

        this.suggestions = suggestions;
        this.tooltip = tooltip;
    }

    @Override
    public @Nullable Component tooltip() {
        return tooltip;
    }

    @Override
    public void applySuggestions(SuggestionsBuilder builder, boolean allowDecimals) {
        // I have absolutely no idea what I'm doing with these coordinate
        // suggestions, I'm so absolutely sorry if they function 20% of 10%
        // of the time, I give up ;-;
        //   - Jules

        String token = builder.getRemainingLowerCase();

        // Blank input -> suggest all coordinates
        // incrementally
        if (token.isBlank()) {
            var joiner = new StringJoiner(" ");

            for (var s: suggestions) {
                joiner.add(s);
                builder.suggest(joiner.toString(), CmdUtil.toTooltip(tooltip));
            }

            return;
        }

        // Not blank, run through each coordinate
        // and see if it matches, if it does,
        // suggest it
        String[] split = token.split(" ");
        StringJoiner last = new StringJoiner(" ");

        for (int i = 0; i < suggestions.length; i++) {
            String suggestion = suggestions[i];

            if (split.length <= i) {
                break;
            }

            String compare = split[i];

            // Check if it matches, if it does, add it to the
            // suggestions, else, add the input to the suggestion
            // total
            if (CompletionProvider.startsWith(compare, suggestion)) {
                builder.suggest(
                        last.add(suggestion).toString(),
                        CmdUtil.toTooltip(tooltip)
                );
            } else {
                last.add(compare);
            }
        }
    }

    @Override
    public String toString() {
        return Joiner.on(' ').join(suggestions);
    }
}