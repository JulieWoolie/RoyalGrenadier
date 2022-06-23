package net.forthecrown.royalgrenadier.types.scoreboard;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.exceptions.TranslatableExceptionType;
import net.forthecrown.grenadier.types.scoreboard.TeamArgument;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.forthecrown.royalgrenadier.VanillaMappedArgument;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class TeamArgumentImpl implements TeamArgument, VanillaMappedArgument {
    public static final TranslatableExceptionType UNKNOWN_TEAM = new TranslatableExceptionType("team.notFound");
    public static final TeamArgumentImpl INSTANCE = new TeamArgumentImpl();

    @Override
    public Team parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        String name = reader.readUnquotedString();

        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(name);
        if(team == null) throw UNKNOWN_TEAM.createWithContext(GrenadierUtils.correctReader(reader, cursor), Component.text(name));

        return team;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CompletionProvider.suggestTeams(builder);
    }

    @Override
    public Collection<String> getExamples() {
        return GrenadierUtils.convertList(Bukkit.getScoreboardManager().getMainScoreboard().getTeams(), Team::getName);
    }

    public net.minecraft.commands.arguments.TeamArgument getVanillaArgumentType() {
        return net.minecraft.commands.arguments.TeamArgument.team();
    }

    @Override
    public boolean useVanillaSuggestions() {
        return true;
    }
}