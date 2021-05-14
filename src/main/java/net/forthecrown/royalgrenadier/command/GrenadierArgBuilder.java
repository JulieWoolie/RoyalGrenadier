package net.forthecrown.royalgrenadier.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;

public class GrenadierArgBuilder<S, T, V> extends ArgumentBuilder<S, GrenadierArgBuilder<S, T, V>> {
    private final String name;
    private final ArgumentType<T> type;
    private final ArgumentType<V> grenadierType;
    private SuggestionProvider<S> suggestionsProvider = null;

    private GrenadierArgBuilder(final String name, final ArgumentType<T> type, ArgumentType<V> grenadierType) {
        this.name = name;
        this.type = type;
        this.grenadierType = grenadierType;
    }

    public static <S, T, V> GrenadierArgBuilder<S, T, V> argument(final String name, final ArgumentType<T> type, ArgumentType<V> grenadierType) {
        return new GrenadierArgBuilder<>(name, type, grenadierType);
    }

    public GrenadierArgBuilder<S, T, V> suggests(final SuggestionProvider<S> provider) {
        this.suggestionsProvider = provider;
        return getThis();
    }

    public SuggestionProvider<S> getSuggestionsProvider() {
        return suggestionsProvider;
    }

    @Override
    protected GrenadierArgBuilder<S, T, V> getThis() {
        return this;
    }

    public ArgumentType<T> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public NonParsingCommandNode<S, T, V> build() {
        final NonParsingCommandNode<S, T, V> result = new NonParsingCommandNode<>(getName(), getType(), grenadierType, getCommand(), getRequirement(), getRedirect(), getRedirectModifier(), isFork(), getSuggestionsProvider());

        for (final CommandNode<S> argument : getArguments()) {
            result.addChild(argument);
        }

        return result;
    }
}
