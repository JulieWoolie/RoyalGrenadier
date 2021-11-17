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
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.forthecrown.royalgrenadier.RoyalGrenadier;
import net.forthecrown.royalgrenadier.source.CommandSources;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

//Class for wrapping Grenadier's commands into NMS ones
public class CommandWrapper implements Command<CommandSourceStack>, Predicate<CommandSourceStack> {

    final AbstractCommand builder;
    final SimpleCommandExceptionType noPermission;
    static final SimpleCommandExceptionType NOT_ALLOWED_TO_USE_COMMAND = new SimpleCommandExceptionType(() -> "You aren't allowed to use this command at the moment");

    public CommandWrapper(AbstractCommand builder){
        this.builder = builder;

        this.noPermission = new SimpleCommandExceptionType(
                builder.getPermissionMessage() == null ?
                        Bukkit::getPermissionMessage :
                        builder::getPermissionMessage
        );
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        //Takes the vanilla command source and turns it's input into
        //Grenadier's sender and executes command with it
        CommandSource source = CommandSources.getOrCreate(context.getSource(), builder);

        //Test if the sender is even allowed to use the command
        //VanillaCommandWrapper should test this for us, but we should still test, just in case
        if(!test(context.getSource())){
            if(!builder.testPermissionSilent(context.getSource().getBukkitSender())) throw noPermission.create();
            throw NOT_ALLOWED_TO_USE_COMMAND.create();
        }

        try {
            return RoyalGrenadier.getDispatcher().execute(context.getInput(), source);
        } catch (RoyalCommandException exception) { //Adventure component exceptions
            source.sendMessage(exception.formattedText());

            return -1;
        } catch (CommandSyntaxException syntaxException) {
            source.sendMessage(GrenadierUtils.formatCommandException(syntaxException));

            return -1;
        } catch (Exception e){
            e.printStackTrace();

            TextComponent.Builder builder = Component.text()
                    .append(Component.text(e.getClass().getName() + ": " + e.getMessage()));

            if(RoyalCommandException.ENABLE_HOVER_STACK_TRACE) {
                for (StackTraceElement element : e.getStackTrace()) {
                    String info = element.getClassName() + '.' + element.getMethodName() + " (Line: " + element.getLineNumber() + ')';

                    builder
                            .append(Component.newline())
                            .append(Component.text("  " + info));
                }
            }

            source.sendMessage(Component.translatable("command.failed", RoyalCommandException.ERROR_MESSAGE_STYLE).hoverEvent(builder.build()));
            return -1;
        }
    }

    //FIXME This shit broken
    //Doesn't work in places with mutpliple required arguments, just returns an empty suggestions thing.
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder, ArgumentCommandNode<CommandSource, ?> node) throws CommandSyntaxException {
        try {
            StringReader reader = new StringReader(builder.getInput());

            //Skip / or no work
            if(reader.canRead() && reader.peek() == '/') {
                reader.skip();
            }

            CommandSource source = CommandSources.getOrCreate(context.getSource(), this.builder);
            ParseResults<CommandSource> results = RoyalGrenadier.getDispatcher().parse(reader, source);

            return node.listSuggestions(results.getContext().build(builder.getInput()), builder);
        } catch (RuntimeException e) {
            throw SuggestionException.create(e);
        }
    }

    @Override
    public boolean test(CommandSourceStack wrapper) {
        return builder.test(CommandSources.getOrCreate(wrapper, builder));
    }
}