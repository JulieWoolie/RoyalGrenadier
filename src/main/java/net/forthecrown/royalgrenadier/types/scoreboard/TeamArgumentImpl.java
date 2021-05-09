package net.forthecrown.royalgrenadier.types.scoreboard;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.types.scoreboard.TeamArgument;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Team;

import java.util.concurrent.CompletableFuture;

public class TeamArgumentImpl implements TeamArgument {
    public static final DynamicCommandExceptionType UNKNOWN_TEAM = new DynamicCommandExceptionType(o -> () -> "Unknown team: " + o);
    public static final TeamArgumentImpl INSTANCE = new TeamArgumentImpl();

    @Override
    public Team parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        String name = reader.readUnquotedString();

        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(name);
        if(team == null) throw UNKNOWN_TEAM.createWithContext(GrenadierUtils.correctCursorReader(reader, cursor), name);

        return team;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(builder, GrenadierUtils.convertList(Bukkit.getScoreboardManager().getMainScoreboard().getTeams(), Team::getName));
    }
}
