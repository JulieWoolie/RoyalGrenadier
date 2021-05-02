package net.forthecrown.grenadier.types.scoreboard;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.types.scoreboard.TeamArgumentImpl;
import org.bukkit.scoreboard.Team;

public interface TeamArgument extends ArgumentType<Team> {
    static TeamArgument team(){
        return TeamArgumentImpl.INSTANCE;
    }

    static Team getTeam(CommandContext<CommandSource> c, String argument){
        return c.getArgument(argument, Team.class);
    }
}
