package net.forthecrown.grenadier.exceptions;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.brigadier.PaperBrigadier;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.kyori.adventure.text.Component;
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
        this.message = PaperBrigadier.componentFromMessage(message);
    }

    public RoyalCommandException(CommandExceptionType type, Message message, String input, int cursor) {
        super(type, message, input, cursor);
        this.message = PaperBrigadier.componentFromMessage(message);
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
    public Component componentMessage() {
        return message;
    }
}