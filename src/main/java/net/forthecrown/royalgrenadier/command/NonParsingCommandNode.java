package net.forthecrown.royalgrenadier.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;

import java.util.function.Predicate;

//Exists so that the NMS node's don't parse unnecessary stuff
public class NonParsingCommandNode<S, T, V> extends ArgumentCommandNode<S, T> {
    private final ArgumentType<V> type;

    public NonParsingCommandNode(String name, ArgumentType<T> type, ArgumentType<V> grenadierType, Command<S> command, Predicate<S> requirement, CommandNode<S> redirect, RedirectModifier<S> modifier, boolean forks, SuggestionProvider<S> customSuggestions) {
        super(name, type, command, requirement, redirect, modifier, forks, customSuggestions);
        this.type = grenadierType;
    }

    @Override
    public void parse(StringReader reader, CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException {
        type.parse(reader); //Just so the cursor gets moved forward
    }
}
