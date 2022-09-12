package net.forthecrown.royalgrenadier;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.adventure.AdventureComponent;
import io.papermc.paper.brigadier.PaperBrigadier;
import net.forthecrown.grenadier.exceptions.RoyalCommandException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.translation.GlobalTranslator;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import static net.forthecrown.grenadier.exceptions.RoyalCommandException.*;

public class GrenadierUtils {
    /**
     * Creates a reader with the same input as the given
     * reader, but moves its cursor to the given cursor
     * position.
     *
     * @param reader The reader to copy
     * @param cursor The position to move the result's cursor to
     * @return The cloned reader with a shifted cursor
     */
    public static StringReader correctReader(ImmutableStringReader reader, int cursor) {
        StringReader reader1 = new StringReader(reader.getString());
        reader1.setCursor(cursor);

        return reader1;
    }

    /**
     * Turns the given adventure component into a vanilla
     * chat component.
     * <p>
     * If the givne input is null, the return value of this
     * method is also null.
     * <p>
     * If the given component is, or contains, translatable
     * components, they will be rendered into {@link Locale#ENGLISH}.
     *
     * @param component The component to convert
     * @return The vanilla equivalent of the given input
     */
    public static net.minecraft.network.chat.Component toVanilla(Component component) {
        if (component == null) {
            return null;
        }

        component = GlobalTranslator.render(component, Locale.ENGLISH);
        return new AdventureComponent(component);
    }

    /**
     * Suggests a vanilla resource location iterable.
     * <p>
     * Not in {@link net.forthecrown.grenadier.CompletionProvider}
     * because I don't want to directly put vanilla code in that.
     *
     * @param resources The resource keys to suggest
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    public static CompletableFuture<Suggestions> suggestResource(Iterable<ResourceLocation> resources, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(resources, builder);
    }

    /**
     * Formats a command exception into a single formatted text.
     * <p>
     * Uses {@link RoyalCommandException#ERROR_MESSAGE_STYLE} as
     * the style for the error message.
     * <p>
     * If the given exception has a context, it calls
     * {@link #formatExceptionContext(CommandSyntaxException)} to
     * format it and appends it onto the result on a second line.
     *
     * @param exception The exception to format
     * @return The formatted exception
     * @see #formatExceptionContext(CommandSyntaxException)
     */
    public static Component formatCommandException(CommandSyntaxException exception) {
        Component initialMessage = PaperBrigadier.componentFromMessage(exception.getRawMessage())
                .style(RoyalCommandException.ERROR_MESSAGE_STYLE);

        if (exception.getInput() == null || exception.getCursor() < 0) {
            return initialMessage;
        }

        return Component.text()
                .append(initialMessage)
                .append(Component.newline())
                .append(formatExceptionContext(exception))
                .build();
    }

    /**
     * Formats a command syntax exception's context
     * into a single text.
     * <p>
     * If the given exception's context is null or cursor
     * at -1, then null is returned.
     * <p>
     * The returned text uses text styles contained in the
     * {@link RoyalCommandException} class, these styles are
     * free for anyone to edit or remove.
     *
     * @param e The exception to format the context of
     * @return The formatted context, or null, if there was no
     *         context to format
     */
    public static @Nullable Component formatExceptionContext(CommandSyntaxException e) {
        if (e.getInput() == null || e.getCursor() < 0) {
            return null;
        }

        final TextComponent.Builder builder = Component.text();
        final int cursor = Math.min(e.getInput().length(), e.getCursor());
        final int start = Math.max(0, cursor - CommandSyntaxException.CONTEXT_AMOUNT); //Either start of input or cursor - 10

        //Context too long, add dots
        if (start != 0) {
            builder.append(Component.text("...").style(GRAY_CONTEXT_STYLE));
        }

        String grayContext = e.getInput().substring(start, cursor);
        String redContext = e.getInput().substring(cursor);

        builder.append(
                Component.text()
                        //Clicking on the exception will put the input in chat
                        .clickEvent(ClickEvent.suggestCommand("/" + e.getInput()))

                        // Show command in hover event
                        .hoverEvent(
                                Component.text()
                                        .append(
                                                Component.text("/" + e.getInput().substring(0, cursor), GRAY_CONTEXT_STYLE)
                                        )
                                        .append(
                                                Component.text(e.getInput().substring(cursor), RED_CONTEXT_STYLE)
                                        )
                                        .build()
                        )

                        .append(Component.text(grayContext).style(GRAY_CONTEXT_STYLE))
                        .append(Component.text(redContext).style(RED_CONTEXT_STYLE))

                        .append(Component.translatable("command.context.here").style(HERE_POINTER_STYLE))

                        .build()
        );

        return builder.build();
    }

    /**
     * Sometimes the input given to a command execution
     * contains the plugin's name as a namespace, for example:
     * "bukkit:reload".
     * <p>
     * There might also be the scenario that
     * we're given an input from a /execute command in which
     * case we need to make sure we're not trying to parse the
     * execute command's input, so we skip to the 'run' argument
     * and get to our own input.
     *
     * @param input The input to filter
     * @return The reader with the cursor at the start
     *         of the relevant input.
     */
    public static StringReader filterCommandInput(String input) {
        StringReader reader = new StringReader(input);

        if (reader.peek() == '/') {
            reader.skip();
        }

        if (reader.getRemaining().startsWith("execute")) {
            var runIndex = reader.getString().indexOf("run");

            // execute as @e facing ~ ~ ~ run /grenadier arg1 arg2
            // ^ starts with execute     ^ run index
            //                            +4 ^ corrected start index
            // That's a weird way to explain my thought process
            // but yeah

            if (runIndex != -1) {
                reader.setCursor(runIndex + 4);

                if (reader.canRead() && reader.peek() == '/') {
                    reader.skip();
                }
            }
        }

        // If arguments in input:
        // /plugin:command arg1 arg2
        //                ^ Isolate from here, there
        //                  might be ':' in input
        //
        // If no arguments in input:
        // /plugin:command
        // - No space index, no need to isolate
        //
        // Isolation result:
        // /plugin:command
        //        ^ ':' index, namespace found, move cursor
        //              to compensate
        //
        // The reason the above shown isolation is done is
        // that it's not guarenteed that the input has/doesn't
        // have a namespace, so we need to test if it does

        int cursor = reader.getCursor();
        int spaceIndex = reader.getRemaining().indexOf(' ');

        if (spaceIndex == -1) {
            spaceIndex = reader.getRemainingLength();
        }

        var subStr = reader.getRemaining().substring(0, spaceIndex);

        if (subStr.contains(":")) {
            while (reader.canRead() && reader.peek() != ':') {
                reader.skip();
            }

            reader.skip();
        }

        return reader;
    }
}