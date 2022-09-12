package net.forthecrown.grenadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.forthecrown.royalgrenadier.types.UUIDArgumentImpl;

import java.util.UUID;

/**
 * An argument type which parses a given input into a UUID
 */
public interface UUIDArgument extends ArgumentType<UUID> {
    /**
     * Gets the argument instance
     * @return The argument instance
     */
    static UUIDArgument uuid() {
        return UUIDArgumentImpl.INSTANCE;
    }

    /**
     * Gets a UUID from the given
     * context
     * @param c The context to get the UUID from
     * @param argument The name of the argument the UUID is under
     * @return The gotten UUID
     */
    static UUID getUUID(CommandContext<?> c, String argument) {
        return c.getArgument(argument, UUID.class);
    }
}