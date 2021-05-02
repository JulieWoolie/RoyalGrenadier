package net.forthecrown.royalgrenadier.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.RoyalGrenadier;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.forthecrown.royalgrenadier.source.CommandSources;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class CommandWrapper implements Command<CommandListenerWrapper>, Predicate<CommandListenerWrapper> {

    private final AbstractCommand builder;

    public CommandWrapper(AbstractCommand builder){
        this.builder = builder;
    }

    @Override
    public int run(CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException {
        return RoyalGrenadier.getDispatcher().execute(context.getInput(), CommandSources.getOrCreate(context.getSource(), this.builder));
    }

    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandListenerWrapper> context, SuggestionsBuilder builder, ArgumentCommandNode<CommandSource, ?> node) throws CommandSyntaxException {
        StringReader reader = new StringReader(builder.getInput());
        if (reader.canRead() && reader.peek() == '/') {
            reader.skip();
        }

        //This shit is dumb, but I'm glad I finally got the fucker working
        ParseResults<CommandSource> parseResults = RoyalGrenadier.getDispatcher().parse(reader, CommandSources.getOrCreate(context.getSource(), this.builder));

        //Hacky af approach to getting the suggestions to work, just list the suggestions of the given node
        SuggestionsBuilder builder1 = new SuggestionsBuilder(builder.getInput(), builder.getStart());
        CommandContext<CommandSource> fuckThisShit = parseResults.getContext().build(builder.getInput());
        //... with some haphazard commandcontext and suggestionsbuilder lmao


        //end me
        return node.listSuggestions(fuckThisShit, builder1);
    }

    @Override
    public boolean test(CommandListenerWrapper wrapper) {
        return builder.test(CommandSources.getOrCreate(wrapper, builder));
    }
}
