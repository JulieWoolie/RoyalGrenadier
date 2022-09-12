package net.forthecrown.grenadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.forthecrown.royalgrenadier.types.WorldArgumentImpl;
import org.bukkit.World;

/**
 * Argument type that parses and returns a world by its name
 */
public interface WorldArgument extends ArgumentType<World> {

    /**
     * Gets the instance of the argument
     * @return The argument instance
     */
    static WorldArgument world() {
        return WorldArgumentImpl.INSTANCE;
    }

    static World getWorld(CommandContext<?> c, String argument) {
        return c.getArgument(argument, World.class);
    }
}