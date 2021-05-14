package net.forthecrown.grenadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.types.TimeArgumentImpl;

/**
 * Parses a given int with a suffix like "t", "s", "d" or "h" into ticks
 */
public interface TimeArgument extends ArgumentType<Long> {

    static TimeArgument time(){
        return TimeArgumentImpl.INSTANCE;
    }

    static Long getTicks(CommandContext<CommandSource> c, String argument){
        return c.getArgument(argument, Long.class);
    }

    static Long getMillis(CommandContext<CommandSource> c, String argument){
        return getTicks(c, argument) * 50;
    }
}
