package net.forthecrown.grenadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;

/**
 * An extension of {@link SuggestionProvider} which cannot
 * throw {@link com.mojang.brigadier.exceptions.CommandSyntaxException}s
 * @param <S> Command source
 */
public interface Suggester<S> extends SuggestionProvider<S> {
    @Override
    CompletableFuture<Suggestions> getSuggestions(CommandContext<S> context, SuggestionsBuilder builder);
}