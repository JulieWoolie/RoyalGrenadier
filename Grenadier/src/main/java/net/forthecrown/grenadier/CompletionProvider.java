package net.forthecrown.grenadier;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.types.pos.CoordinateSuggestion;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.kyori.adventure.key.Key;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.loot.LootTables;
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
     * Checks if the given string starts with the given token
     * @param token The token the string should start with
     * @param s The string to check
     * @return True, if <code>s</code> starts with <code>token</code>
     */
    static boolean startsWith(String token, String s) {
        if (token.length() > s.length()) {
            return false;
        }

        return s.regionMatches(true, 0, token, 0, token.length());
    }

    /**
     * Suggest all matching strings into the given SuggestionsBuilder
     * @param builder The builder to give suggestions to
     * @param suggestions The suggestions to pick from
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestMatching(SuggestionsBuilder builder, Iterable<String> suggestions) {
        String token = builder.getRemainingLowerCase();

        for (String s: suggestions) {
            if (startsWith(token, s)) builder.suggest(s);
        }

        return builder.buildFuture();
    }

    /**
     * Suggest all matching strings into the given SuggestionsBuilder
     * @param builder The builder to give suggestions to
     * @param suggestions The suggestions to pick from
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestMatching(SuggestionsBuilder builder, String... suggestions) {
        return suggestMatching(builder, Arrays.asList(suggestions));
    }

    /**
     * Suggest all matching strings into the given SuggestionsBuilder
     * @param builder The builder to give suggestions to
     * @param suggestions The suggestions to pick from
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestMatching(SuggestionsBuilder builder, Stream<String> suggestions) {
        String token = builder.getRemainingLowerCase();

        suggestions
                .filter(s -> startsWith(token, s))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }

    /**
     * Suggest all matching strings into the given SuggestionsBuilder and add a Message tooltip
     * @param b The builder to give suggestions to
     * @param suggestions The suggestions to pick from
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestMatching(SuggestionsBuilder b, Map<String, String> suggestions) {
        String token = b.getRemainingLowerCase();

        for (Map.Entry<String, String> entry: suggestions.entrySet()) {
            if(startsWith(token, entry.getKey())) {
                b.suggest(entry.getKey(), new LiteralMessage(entry.getValue()));
            }
        }

        return b.buildFuture();
    }

    /**
     * Suggest all matching Keys, or NamespacedKeys, into the given SuggestionsBuilder
     * @param builder The builder to give suggestions to
     * @param suggestions The suggestions to pick from
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestKeys(SuggestionsBuilder builder, Iterable<? extends Key> suggestions) {
        String token = builder.getRemainingLowerCase();

        for (Key k: suggestions) {
            if (startsWith(token, k.asString())
                    || startsWith(token, k.value())
                    || startsWith(token, k.namespace())
            ) {
                builder.suggest(k.asString());
            }
        }

        return builder.buildFuture();
    }

    /**
     * Suggests 3d cords to given builder
     * @param builder The builder to suggest to
     * @param allowDecimals Whether to allow decimal places in the suggestions
     * @param suggestions Any optional extra sugestions.
     * @return
     */
    static CompletableFuture<Suggestions> suggestCords(SuggestionsBuilder builder, boolean allowDecimals, Iterable<CoordinateSuggestion> suggestions) {
        for (CoordinateSuggestion s: suggestions) {
            s.applySuggestions(builder, allowDecimals);
        }

        return builder.buildFuture();
    }

    /**
     * Suggest world names
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestWorlds(SuggestionsBuilder builder) {
        return suggestMatching(builder, GrenadierUtils.convertList(Bukkit.getWorlds(), World::getName));
    }

    /**
     * Suggest sound event keys
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestSounds(SuggestionsBuilder builder) {
        return GrenadierUtils.suggestResource(Registry.SOUND_EVENT.keySet(), builder);
    }

    /**
     * Suggest particle keys
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestParticles(SuggestionsBuilder builder) {
        return GrenadierUtils.suggestResource(Registry.PARTICLE_TYPE.keySet(), builder);
    }

    /**
     * Suggest enchant keys
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestEnchantments(SuggestionsBuilder builder) {
        return GrenadierUtils.suggestResource(Registry.ENCHANTMENT.keySet(), builder);
    }

    /**
     * Suggest entity type keys
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestEntities(SuggestionsBuilder builder) {
        return GrenadierUtils.suggestResource(Registry.ENTITY_TYPE.keySet(), builder);
    }

    /**
     * Suggest Scoreboard team names
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestTeams(SuggestionsBuilder builder) {
        return suggestMatching(builder, GrenadierUtils.convertList(Bukkit.getScoreboardManager().getMainScoreboard().getTeams(), Team::getName));
    }

    /**
     * Suggest Scoreboard objectives
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestObjectives(SuggestionsBuilder builder) {
        return suggestMatching(builder, GrenadierUtils.convertList(Bukkit.getScoreboardManager().getMainScoreboard().getObjectives(), Objective::getName));
    }

    /**
     * Suggest block type keys
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestBlocks(SuggestionsBuilder builder) {
        return GrenadierUtils.suggestResource(Registry.BLOCK.keySet(), builder);
    }

    /**
     * Suggest potion effects
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestPotionEffects(SuggestionsBuilder builder) {
        return GrenadierUtils.suggestResource(Registry.POTION.keySet(), builder);
    }

    /**
     * Suggest potion effects
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestEffects(SuggestionsBuilder builder) {
        return GrenadierUtils.suggestResource(Registry.MOB_EFFECT.keySet(), builder);
    }

    /**
     * Suggest player names
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestPlayerNames(SuggestionsBuilder builder) {
        return suggestMatching(builder, GrenadierUtils.convertList(Bukkit.getOnlinePlayers(), Player::getName));
    }

    /**
     * Suggests entity types, eg: minecraft:cow
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestEntityTypes(SuggestionsBuilder builder) {
        return GrenadierUtils.suggestResource(Registry.ENTITY_TYPE.keySet(), builder);
    }

    static CompletableFuture<Suggestions> suggestLootTables(SuggestionsBuilder builder) {
        MinecraftServer server = MinecraftServer.getServer();
        MinecraftServer.ReloadableResources manager = server.resources;
        LootTables lootTables = manager.managers().getLootTables();

        return GrenadierUtils.suggestResource(lootTables.getIds(), builder);
    }
}