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
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.forthecrown.royalgrenadier.RoyalGrenadier;
import net.forthecrown.royalgrenadier.WrappedCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.commands.CommandSourceStack;
import org.apache.logging.log4j.Logger;
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
            LOGGER.error("Error executing command '{}'", builder.getName(), e);

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

    public CompletableFuture<Suggestions> getSuggestions(
        CommandContext<CommandSourceStack> context,
        SuggestionsBuilder builder,
        ArgumentCommandNode<CommandSource, ?> node
    ) throws CommandSyntaxException {
        try {
            StringReader reader = GrenadierUtils.filterCommandInput(builder.getInput());

            CommandSource source = WrappedCommandSource.of(context.getSource(), this.builder, null);
            ParseResults<CommandSource> results = RoyalGrenadier.getDispatcher()
                    .parse(reader, source);

            var builtContext = results.getContext()
                    .build(builder.getInput())
                    .getLastChild();

            return node.listSuggestions(builtContext, builder);
        } catch (CommandSyntaxException syntaxException) {
            throw syntaxException;
        } catch (Throwable e) {
            LOGGER.error("Error attempting to get suggestions for command '{}'",
                    this.builder.getName(), e
            );

            return Suggestions.empty();
        }
    }

    // Debug thing I commented out because it shouldn't be in non-debug builds:
    //
    /*private static String resultsToString(ParseResults<CommandSource> results) {
        StringBuffer buffer = new StringBuffer()
                .append("--- PARSE RESULTS ---");

        buffer.append("Reader:{")
                .append("cursor:")
                .append(results.getReader().getCursor())
                .append(",read:'")
                .append(results.getReader().getRead())
                .append("',remaining='")
                .append(results.getReader().getRemaining())
                .append("'")
                .append('}');

        if (!results.getExceptions().isEmpty()) {
            buffer.append("\nExceptions:{");

            for (var e: results.getExceptions().entrySet()) {
                var node = e.getKey();
                var path = RoyalGrenadier.getDispatcher().getPath(node);

                buffer
                        .append("\n ")
                        .append(Joiner.on('.').join(path))
                        .append(":'")
                        .append(e.getValue().getMessage())
                        .append("'")
                        .append(",");
            }

            buffer.delete(buffer.length() - 1, buffer.length())
                    .append("\n}");
        }

        buffer.append("\nContexts:[\n");
        printContext(results.getContext(), buffer, results.getReader());
        buffer.append("]");

        buffer.append("\n--- RESULTS END ---");

        return buffer.toString();
    }

    private static void printContext(CommandContextBuilder<CommandSource> context, StringBuffer buffer, ImmutableStringReader input) {
        buffer.append(" Context:{\n")
                .append("  Source:'")
                .append(context.getSource().textName())
                .append("'\n")
                .append("  Range:")
                .append(context.getRange())
                .append(",input:'")
                .append(context.getRange().get(input));

        if (!context.getArguments().isEmpty()) {
            buffer.append("\n  Arguments:[");

            for (var e: context.getArguments().entrySet()) {
                buffer
                        .append("\n   ")
                        .append(e.getKey())
                        .append(",");

                buffer.append("range:")
                        .append(e.getValue().getRange());

                buffer.append("input:'")
                        .append(e.getValue().getRange().get(input))
                        .append("'")
                        .append(",");
            }

            buffer.delete(buffer.length() - 1, buffer.length());
            buffer.append("\n  ]");
        }

        if (!context.getNodes().isEmpty()) {
            buffer.append("\n  Nodes:[");

            for (var node: context.getNodes()) {
                buffer.append("\n   node:'")
                        .append(node.getNode().getUsageText())
                        .append("',range:")
                        .append(node.getRange())
                        .append(",input:'")
                        .append(node.getRange().get(input))
                        .append("'")
                        .append(",");
            }

            buffer.delete(buffer.length() - 1, buffer.length())
                    .append("  \n]\n");
        }

        buffer.append(" }");

        if (context.getChild() != null) {
            buffer.append(",\n");
            printContext(context.getChild(), buffer, input);
        }
    }*/

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