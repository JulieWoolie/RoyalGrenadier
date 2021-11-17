package net.forthecrown.grenadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import net.kyori.adventure.text.Component;

/**
 * Represents a component supporting version of the {@link com.mojang.brigadier.exceptions.SimpleCommandExceptionType}
 */
public class ImmutableCommandExceptionType implements CommandExceptionType {

    /**
     * The message the command executor will be sent
     */
    private final Component message;

    public ImmutableCommandExceptionType(Component message) {
        this.message = message;
    }

    /**
     * Creates a command exception with the given message
     * @return A command exception
     */
    public RoyalCommandException create(){
        return new RoyalCommandException(this, message);
    }

    /**
     * Creates a command exception with the given message and context
     * @param reader The context
     * @return The command exception
     */
    public RoyalCommandException createWithContext(ImmutableStringReader reader){
        return new RoyalCommandException(this, message, reader.getString(), reader.getCursor());
    }

    /**
     * The message used by this type
     * @return The messsage
     */
    public Component getMessage() {
        return message;
    }
}
