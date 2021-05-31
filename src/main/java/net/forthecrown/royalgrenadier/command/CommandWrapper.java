package net.forthecrown.royalgrenadier.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.forthecrown.grenadier.exceptions.RoyalCommandException;
import net.forthecrown.royalgrenadier.Main;
import net.forthecrown.royalgrenadier.source.CommandSources;
import net.minecraft.server.v1_16_R3.ChatMessage;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

//Class for wrapping Grenadier's commands into NMS ones
public class CommandWrapper implements Command<CommandListenerWrapper>, Predicate<CommandListenerWrapper> {

    private final AbstractCommand builder;
    private final SimpleCommandExceptionType noPermission;
    private static final SimpleCommandExceptionType NOT_ALLOWED_TO_USE_COMMAND = new SimpleCommandExceptionType(() -> "You aren't allowed to use this command at the moment");
    private static final SimpleCommandExceptionType EXCEPTION_OCCURRED = new SimpleCommandExceptionType(new ChatMessage("commands.generic.exception"));

    public CommandWrapper(AbstractCommand builder){
        this.builder = builder;

        String permissionMessage = builder.getPermissionMessage() == null ? Bukkit.getPermissionMessage() : builder.getPermissionMessage();
        this.noPermission = new SimpleCommandExceptionType(() -> permissionMessage);
    }

    @Override
    public int run(CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException {
        //Takes the vanilla command source and turns it's input into
        //Grenadier's sender and executes command with it

        //Test if the sender is even allowed to use the command
        //VanillaCommandWrapper should test this for us, but we should still test, just in case
        if(!test(context.getSource())){
            if(!builder.testPermissionSilent(context.getSource().getBukkitSender())) throw noPermission.create();
            throw NOT_ALLOWED_TO_USE_COMMAND.create();
        }

        try {
            return Main.getDispatcher().execute(context.getInput(), CommandSources.getOrCreate(context.getSource(), builder));
        } catch (RuntimeException e){ //Catch runtime exceptions but have NMS to deal with CommandSyntaxExceptions
            e.printStackTrace(); //If we didn't catch this and print it, stack traces would never appear because NMS suppresses them
            throw EXCEPTION_OCCURRED.create();
        } catch (RoyalCommandException exception){ //Adventure component exceptions
            context.getSource().getBukkitSender().sendMessage(exception.formattedText());

            //Paper's Adventure to Vanilla component conversion sucks, so we don't use it
            //Instead we throw our own exceptions that we format manually
            //Plus, this supports TranslatableComponents with custom translations

            return 1;
        }
    }

    //TODO This doesn't work with multiple argument types that rely on Grenadier, and not vanilla, for suggestions
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandListenerWrapper> context, SuggestionsBuilder builder, ArgumentCommandNode<CommandSource, ?> node) throws CommandSyntaxException {
        StringReader reader = new StringReader(builder.getInput());
        if (reader.canRead() && reader.peek() == '/') {
            reader.skip();
        }

        ParseResults<CommandSource> parseResults = Main.getDispatcher().parse(reader, CommandSources.getOrCreate(context.getSource(), this.builder));

        SuggestionsBuilder builder1 = new SuggestionsBuilder(builder.getInput(), builder.getStart());
        CommandContext<CommandSource> fuckThisShit = parseResults.getContext().build(builder.getInput());

        return node.listSuggestions(fuckThisShit, builder1);
    }

    @Override
    public boolean test(CommandListenerWrapper wrapper) {
        return builder.test(CommandSources.getOrCreate(wrapper, builder));
    }
}
