package net.forthecrown.royalgrenadier.types.scoreboard;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.types.scoreboard.ObjectiveArgument;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;

import java.util.concurrent.CompletableFuture;

public class ObjectiveArgumentImpl implements ObjectiveArgument {
    public static final ObjectiveArgumentImpl INSTANCE = new ObjectiveArgumentImpl();
    public static final DynamicCommandExceptionType UNKNOWN_OBJECTIVE = new DynamicCommandExceptionType(o -> () -> "Unkown objective: " + o.toString());

    @Override
    public Objective parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        String name = reader.readUnquotedString();

        Objective objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(name);
        if(objective == null){
            reader.setCursor(cursor);
            throw UNKNOWN_OBJECTIVE.createWithContext(reader, name);
        }

        return objective;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(builder, GrenadierUtils.convertList(Bukkit.getScoreboardManager().getMainScoreboard().getObjectives(), Objective::getName));
    }
}
