package net.forthecrown.royalgrenadier.types.scoreboard;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.exceptions.TranslatableExceptionType;
import net.forthecrown.grenadier.types.scoreboard.ObjectiveArgument;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.forthecrown.royalgrenadier.VanillaMappedArgument;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ObjectiveArgumentImpl implements ObjectiveArgument, VanillaMappedArgument {
    public static final ObjectiveArgumentImpl INSTANCE = new ObjectiveArgumentImpl();
    public static final TranslatableExceptionType UNKNOWN_OBJECTIVE = new TranslatableExceptionType("arguments.objective.notFound");

    @Override
    public Objective parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        String name = reader.readUnquotedString();

        Objective objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(name);
        if (objective == null) {
            throw UNKNOWN_OBJECTIVE
                    .createWithContext(
                            GrenadierUtils.correctReader(reader, cursor),
                            Component.text(name)
                    );
        }

        return objective;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CompletionProvider.suggestObjectives(builder);
    }

    @Override
    public Collection<String> getExamples() {
        return Arrays.asList("foo", "*", "012");
    }

    public net.minecraft.commands.arguments.ObjectiveArgument getVanillaArgumentType() {
        return net.minecraft.commands.arguments.ObjectiveArgument.objective();
    }

    @Override
    public boolean useVanillaSuggestions() {
        return true;
    }
}