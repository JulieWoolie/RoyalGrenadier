package net.forthecrown.grenadier;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.kyori.adventure.key.Key;
import net.minecraft.server.v1_16_R3.IRegistry;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public interface CompletionProvider {
    static CompletableFuture<Suggestions> suggestMatching(SuggestionsBuilder builder, Iterable<String> suggestions){
        String token = builder.getRemaining().toLowerCase();
        for (String s: suggestions) if(s.toLowerCase().startsWith(token)) builder.suggest(s);

        return builder.buildFuture();
    }

    static CompletableFuture<Suggestions> suggestMatching(SuggestionsBuilder builder, String... suggestions){
        return suggestMatching(builder, Arrays.asList(suggestions));
    }

    static CompletableFuture<Suggestions> suggestMatching(SuggestionsBuilder builder, Stream<String> suggestions){
        String token = builder.getRemaining().toLowerCase();

        suggestions
                .filter(s -> s.toLowerCase().startsWith(token))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }

    static CompletableFuture<Suggestions> suggestKeys(SuggestionsBuilder builder, Iterable<Key> suggestions){
        String token = builder.getRemaining().toLowerCase();
        String namespace = null;
        int index = token.indexOf(':');
        if(index == -1) index = 0;

        if(index != 0){
            namespace = token.substring(0, index-1);
            token = token.substring(index);
        }

        for (Key k: suggestions){
            if(namespace != null && namespace.length() > 0 && !k.namespace().startsWith(namespace)) continue;
            if(!k.value().startsWith(token)) continue;

            builder.suggest(k.asString());
        }

        return builder.buildFuture();
    }

    static CompletableFuture<Suggestions> suggestWorlds(SuggestionsBuilder builder){
        return suggestMatching(builder, GrenadierUtils.convertList(Bukkit.getWorlds(), World::getName));
    }

    static CompletableFuture<Suggestions> suggestSounds(SuggestionsBuilder builder){
        return GrenadierUtils.suggestResource(IRegistry.SOUND_EVENT.keySet(), builder);
    }

    static CompletableFuture<Suggestions> suggestParticles(SuggestionsBuilder builder){
        return GrenadierUtils.suggestResource(IRegistry.PARTICLE_TYPE.keySet(), builder);
    }

    static CompletableFuture<Suggestions> suggestEnchantments(SuggestionsBuilder builder){
        return GrenadierUtils.suggestResource(IRegistry.ENCHANTMENT.keySet(), builder);
    }

    static CompletableFuture<Suggestions> suggestEntities(SuggestionsBuilder builder){
        return GrenadierUtils.suggestResource(IRegistry.ENTITY_TYPE.keySet(), builder);
    }

    static CompletableFuture<Suggestions> suggestTeams(SuggestionsBuilder builder){
        return suggestMatching(builder, GrenadierUtils.convertList(Bukkit.getScoreboardManager().getMainScoreboard().getTeams(), Team::getName));
    }

    static CompletableFuture<Suggestions> suggestObjectives(SuggestionsBuilder builder){
        return suggestMatching(builder, GrenadierUtils.convertList(Bukkit.getScoreboardManager().getMainScoreboard().getObjectives(), Objective::getName));
    }

    static CompletableFuture<Suggestions> suggestBlocks(SuggestionsBuilder builder){
        return GrenadierUtils.suggestResource(IRegistry.BLOCK.keySet(), builder);
    }
}
