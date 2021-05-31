package net.forthecrown.grenadier;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.kyori.adventure.key.Key;
import net.minecraft.server.v1_16_R3.IRegistry;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * A utility class to help in creating command suggestions.
 * <p>If you want to add suggestions to a SuggestionsBuilder but don't want a built result, simply use one of these methods and ignore the return value</p>
 */
public interface CompletionProvider {

    /**
     * Suggest all matching strings into the given SuggestionsBuilder
     * @param builder The builder to give suggestions to
     * @param suggestions The suggestions to pick from
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestMatching(SuggestionsBuilder builder, Iterable<String> suggestions){
        String token = builder.getRemaining().toLowerCase();
        for (String s: suggestions) if(s.toLowerCase().startsWith(token)) builder.suggest(s);

        return builder.buildFuture();
    }

    /**
     * Suggest all matching strings into the given SuggestionsBuilder
     * @param builder The builder to give suggestions to
     * @param suggestions The suggestions to pick from
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestMatching(SuggestionsBuilder builder, String... suggestions){
        return suggestMatching(builder, Arrays.asList(suggestions));
    }

    /**
     * Suggest all matching strings into the given SuggestionsBuilder
     * @param builder The builder to give suggestions to
     * @param suggestions The suggestions to pick from
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestMatching(SuggestionsBuilder builder, Stream<String> suggestions){
        String token = builder.getRemaining().toLowerCase();

        suggestions
                .filter(s -> s.toLowerCase().startsWith(token))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }

    /**
     * Suggest all matching strings into the given SuggestionsBuilder and add a Message tooltip
     * @param b The builder to give suggestions to
     * @param suggestions The suggestions to pick from
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestMatching(SuggestionsBuilder b, Map<String, Message> suggestions){
        String token = b.getRemaining().toLowerCase();

        for (Map.Entry<String, Message> entry: suggestions.entrySet()){
            if(entry.getKey().toLowerCase().startsWith(token)) b.suggest(entry.getKey(), entry.getValue());
        }

        return b.buildFuture();
    }

    /**
     * Suggest all matching Keys, or NamespacedKeys, into the given SuggestionsBuilder
     * @param builder The builder to give suggestions to
     * @param suggestions The suggestions to pick from
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestKeys(SuggestionsBuilder builder, Iterable<Key> suggestions){
        String token = builder.getRemaining().toLowerCase();
        int index = token.indexOf(':');
        if(index == -1) index = 0;

        if(index != 0) token = token.substring(index);

        for (Key k: suggestions){
            if(!k.value().startsWith(token)) continue;

            builder.suggest(k.asString());
        }

        return builder.buildFuture();
    }

    /**
     * Suggest world names
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestWorlds(SuggestionsBuilder builder){
        return suggestMatching(builder, GrenadierUtils.convertList(Bukkit.getWorlds(), World::getName));
    }

    /**
     * Suggest sound event keys
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestSounds(SuggestionsBuilder builder){
        return GrenadierUtils.suggestResource(IRegistry.SOUND_EVENT.keySet(), builder);
    }

    /**
     * Suggest particle keys
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestParticles(SuggestionsBuilder builder){
        return GrenadierUtils.suggestResource(IRegistry.PARTICLE_TYPE.keySet(), builder);
    }

    /**
     * Suggest enchant keys
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestEnchantments(SuggestionsBuilder builder){
        return GrenadierUtils.suggestResource(IRegistry.ENCHANTMENT.keySet(), builder);
    }

    /**
     * Suggest entity type keys
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestEntities(SuggestionsBuilder builder){
        return GrenadierUtils.suggestResource(IRegistry.ENTITY_TYPE.keySet(), builder);
    }

    /**
     * Suggest Scoreboard team names
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestTeams(SuggestionsBuilder builder){
        return suggestMatching(builder, GrenadierUtils.convertList(Bukkit.getScoreboardManager().getMainScoreboard().getTeams(), Team::getName));
    }

    /**
     * Suggest Scoreboard objectives
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestObjectives(SuggestionsBuilder builder){
        return suggestMatching(builder, GrenadierUtils.convertList(Bukkit.getScoreboardManager().getMainScoreboard().getObjectives(), Objective::getName));
    }

    /**
     * Suggest block type keys
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestBlocks(SuggestionsBuilder builder){
        return GrenadierUtils.suggestResource(IRegistry.BLOCK.keySet(), builder);
    }

    /**
     * Suggest potion effects
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestPotionEffects(SuggestionsBuilder builder){
        return GrenadierUtils.suggestResource(IRegistry.POTION.keySet(), builder);
    }

    /**
     * Suggest potion effects
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestEffects(SuggestionsBuilder builder){
        return GrenadierUtils.suggestResource(IRegistry.MOB_EFFECT.keySet(), builder);
    }

    /**
     * Suggest player names
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestPlayerNames(SuggestionsBuilder builder){
        return suggestMatching(builder, GrenadierUtils.convertList(Bukkit.getOnlinePlayers(), Player::getName));
    }
}
