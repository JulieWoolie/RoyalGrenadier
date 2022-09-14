package net.forthecrown.royalgrenadier.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.forthecrown.grenadier.exceptions.ImmutableCommandExceptionType;
import net.forthecrown.grenadier.exceptions.RoyalCommandException;
import net.forthecrown.grenadier.exceptions.TranslatableExceptionType;
import net.forthecrown.royalgrenadier.WrappedCommandSource;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.forthecrown.royalgrenadier.RoyalGrenadier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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
    static final TranslatableExceptionType NOT_ALLOWED_TO_USE_COMMAND = new TranslatableExceptionType("commands.help.failed");

    public CommandWrapper(AbstractCommand builder) {
        this.builder = builder;

        if (builder.permissionMessage() == null) {
            noPermission = new ImmutableCommandExceptionType(Bukkit.permissionMessage());
        } else {
            noPermission = new ImmutableCommandExceptionType(builder.permissionMessage());
        }
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        return run(context.getSource(), context.getInput());
    }

    public int run(CommandSourceStack stack, String input) {
        //Takes the vanilla command source and turns it's input into
        //Grenadier's sender and executes command with it
        CommandSource source = WrappedCommandSource.of(stack, builder, null);
        StringReader reader = GrenadierUtils.filterCommandInput(input);

        boolean parsing = true;
        CommandDispatcher<CommandSource> dispatcher = RoyalGrenadier.getDispatcher();
        ParseResults<CommandSource> parse = dispatcher.parse(reader, source);

        try {
            // Test if the sender is even allowed to use the command
            if (!test(stack)) {
                if (!builder.testPermissionSilent(stack.getBukkitSender())) {
                    throw noPermission.create();
                }

                throw NOT_ALLOWED_TO_USE_COMMAND.create();
            }

            // Copy-pasted from the execute method to allow for
            // changing that parsing boolean
            if (parse.getReader().canRead()) {
                if (parse.getExceptions().size() == 1) {
                    throw parse.getExceptions().values().iterator().next();
                } else if (parse.getContext().getRange().isEmpty()) {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS
                            .dispatcherUnknownCommand()
                            .createWithContext(parse.getReader());
                } else {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS
                            .dispatcherUnknownArgument()
                            .createWithContext(parse.getReader());
                }
            }

            parsing = false;
            return dispatcher.execute(parse);
        } catch (CommandSyntaxException syntaxException) {
            source.sendFailure(GrenadierUtils.formatCommandException(syntaxException));

            if (builder.getShowUsageOnFail()) {
                Component usage = builder.getUsage(parse.getContext().build(input),
                        syntaxException,
                        parsing ? AbstractCommand.ExecPhase.PARSING : AbstractCommand.ExecPhase.LOGIC_EXECUTION
                );

                if(usage != null) {
                    source.sendMessage(usage);
                }
            }

            return -1;
        } catch (Throwable e) {
            LOGGER.error(
                    new ParameterizedMessage(
                            "Error while executing command '{}'",
                            new Object[]{builder.getName()},
                            e
                    )
            );

            TextComponent.Builder builder = Component.text()
                    .append(Component.text(e.getClass().getName() + ": " + e.getMessage()));

            if (RoyalCommandException.ENABLE_HOVER_STACK_TRACE) {
                for (StackTraceElement element : e.getStackTrace()) {
                    String info = element.getClassName() + '.' + element.getMethodName() + " (Line: " + element.getLineNumber() + ')';
                    builder
                            .append(Component.newline())
                            .append(Component.text("  " + info));
                }
            }

            source.sendFailure(
                    Component.translatable("command.failed", RoyalCommandException.ERROR_MESSAGE_STYLE)
                            .hoverEvent(builder.build())
            );
            return -1;
        }
    }

    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder, ArgumentCommandNode<CommandSource, ?> node) throws CommandSyntaxException {
        try {
            StringReader reader = GrenadierUtils.filterCommandInput(builder.getInput());

            CommandSource source = WrappedCommandSource.of(context.getSource(), this.builder, null);
            ParseResults<CommandSource> results = RoyalGrenadier.getDispatcher().parse(reader, source);

            return node.listSuggestions(results.getContext().build(builder.getInput()), builder);
        } catch (Throwable e) {
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

    public CompletableFuture<Suggestions> suggest(CommandSource source, String input) {
        StringReader reader = GrenadierUtils.filterCommandInput(input);
        ParseResults<CommandSource> results = RoyalGrenadier.getDispatcher().parse(reader, source);
        return RoyalGrenadier.getDispatcher().getCompletionSuggestions(results);
    }

    @Override
    public boolean test(CommandSourceStack wrapper) {
        return builder.test(WrappedCommandSource.of(wrapper, this.builder, null));
    }
}