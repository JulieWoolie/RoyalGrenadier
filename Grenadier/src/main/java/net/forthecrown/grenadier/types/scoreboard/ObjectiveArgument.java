package net.forthecrown.grenadier.types.scoreboard;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.types.scoreboard.ObjectiveArgumentImpl;
import org.bukkit.scoreboard.Objective;

public interface ObjectiveArgument extends ArgumentType<Objective> {
    static ObjectiveArgument objective(){
        return ObjectiveArgumentImpl.INSTANCE;
    }

    static Objective getObjective(CommandContext<CommandSource> c, String argument){
        return c.getArgument(argument, Objective.class);
    }
}
