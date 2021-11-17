package net.forthecrown.grenadier.exceptions;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * Represents an exception that is compatible with Adventure Components.
 */
public class RoyalCommandException extends CommandSyntaxException {
    private Component message;

    //Not final so it can be changed if someone wants

    /**
     * The default style of the error messages, red by default
     */
    public static Style ERROR_MESSAGE_STYLE = Style.style(NamedTextColor.RED);

    /**
     * The style of the text in the context, gray by default
     */
    public static Style GRAY_CONTEXT_STYLE = Style.style(NamedTextColor.GRAY);

    /**
     * The style of the part of the context where you went wrong, underlined red by default
     */
    public static Style RED_CONTEXT_STYLE = Style.style(NamedTextColor.RED, TextDecoration.UNDERLINED);

    /**
     * The style of the <--[HERE] pointer, italic red by default
     */
    public static Style HERE_POINTER_STYLE = Style.style(NamedTextColor.RED, TextDecoration.ITALIC);

    /**
     * Determines whether the stack trace of exceptions is printed in the hover event of the command failure message
     */
    public static boolean ENABLE_HOVER_STACK_TRACE = true;

    public RoyalCommandException(CommandExceptionType type, Message message) {
        super(type, message);
    }

    public RoyalCommandException(CommandExceptionType type, Message message, String input, int cursor) {
        super(type, message, input, cursor);
    }

    public RoyalCommandException(CommandExceptionType type, Component message1) {
        super(type, GrenadierUtils.toVanilla(message1));
        this.message = message1;
    }

    public RoyalCommandException(CommandExceptionType type, Component message1, String input, int cursor) {
        super(type, GrenadierUtils.toVanilla(message1), input, cursor);
        this.message = message1;
    }

    /**
     * Gets the message for this exception
     * @return The exception's message
     */
    public Component getComponentMessage() {
        return message;
    }

    /**
     * Creates a context component with the given input, or null, if no input was given
     * @return The context, or null, if no input was given
     */
    public Component getComponentContext(){
        return GrenadierUtils.formatExceptionContext(this);
    }

    /**
     * Gets the formatted text with the message and context
     * @return The formatted message
     */
    public Component formattedText(){
        Component errorMessageFormatted = message.hasStyling() ? message : message.style(ERROR_MESSAGE_STYLE);
        if(getInput() == null || getCursor() == 0) return errorMessageFormatted;

        TextComponent.Builder builder = Component.text()
                .append(errorMessageFormatted)
                .append(Component.newline())
                .append(getComponentContext());

        return builder.build();
    }
}
