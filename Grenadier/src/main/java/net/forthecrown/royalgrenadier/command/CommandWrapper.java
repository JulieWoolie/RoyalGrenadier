package net.forthecrown.royalgrenadier.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
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
import net.forthecrown.grenadier.exceptions.ImmutableCommandExceptionType;
import net.forthecrown.grenadier.exceptions.RoyalCommandException;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.forthecrown.royalgrenadier.RoyalGrenadier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.commands.CommandSourceStack;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

//Class for wrapping Grenadier's commands into NMS ones
public class CommandWrapper implements Command<CommandSourceStack>, Predicate<CommandSourceStack> {
    private static final Logger LOGGER = RoyalGrenadier.getLogger();

    final AbstractCommand builder;
    final ImmutableCommandExceptionType noPermission;
    static final SimpleCommandExceptionType NOT_ALLOWED_TO_USE_COMMAND = new SimpleCommandExceptionType(() -> "You aren't allowed to use this command at the moment");

    public CommandWrapper(AbstractCommand builder){
        this.builder = builder;

        if(builder.permissionMessage() == null) {
            noPermission = new ImmutableCommandExceptionType(
                    LegacyComponentSerializer.legacySection().deserialize(Bukkit.getPermissionMessage())
            );
        } else {
            noPermission = new ImmutableCommandExceptionType(builder.permissionMessage());
        }
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        //Takes the vanilla command source and turns it's input into
        //Grenadier's sender and executes command with it
        CommandSource source = GrenadierUtils.wrap(context.getSource(), builder);

        try {
            //Test if the sender is even allowed to use the command
            //VanillaCommandWrapper should test this for us, but we should still test, just in case
            if(!test(context.getSource())){
                if(!builder.testPermissionSilent(context.getSource().getBukkitSender())) throw noPermission.create();
                throw NOT_ALLOWED_TO_USE_COMMAND.create();
            }

            StringReader reader = GrenadierUtils.filterCommandInput(context.getInput());

            CommandDispatcher<CommandSource> dispatcher = RoyalGrenadier.getDispatcher();
            ParseResults<CommandSource> parseResults = dispatcher.parse(reader, source);

            return dispatcher.execute(parseResults);
        } catch (CommandSyntaxException syntaxException) {
            source.sendMessage(GrenadierUtils.formatCommandException(syntaxException));

            if(builder.getShowUsageOnFail()) {
                Component usage = builder.getUsage(source);
                if(usage != null) source.sendMessage(usage);
            }

            return -1;
        } catch (Exception e){
            LOGGER.error(
                    new ParameterizedMessage(
                            "Error while executing command '{}'",
                            new Object[]{builder.getName()},
                            e
                    )
            );

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
            StringReader reader = GrenadierUtils.filterCommandInput(builder.getInput());

            CommandSource source = GrenadierUtils.wrap(context.getSource(), this.builder);
            ParseResults<CommandSource> results = RoyalGrenadier.getDispatcher().parse(reader, source);

            return node.listSuggestions(results.getContext().build(builder.getInput()), builder);
        } catch (RuntimeException e) {
            LOGGER.error(
                    new ParameterizedMessage(
                            "Error while attempting to get suggestions for command '{}'",
                            new Object[] { this.builder.getName()},
                            e
                    )
            );

            return Suggestions.empty();
        }
    }

    @Override
    public boolean test(CommandSourceStack wrapper) {
        return builder.test(GrenadierUtils.wrap(wrapper, builder));
    }
}
