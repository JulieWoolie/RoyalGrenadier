package net.forthecrown.grenadier;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.types.pos.CoordinateSuggestion;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.kyori.adventure.key.Key;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.loot.LootTables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * A utility class to help in creating command suggestions.
 * <p>
 * This class also provides 2 utility methods for checking if
 * a potential suggestion is valid, those 2 being {@link #startsWith(String, Key)}
 * and {@link #startsWith(String, String)}.
 * Most of the time, however, you'll only be using {@link #suggestMatching(SuggestionsBuilder, String...)}
 * or {@link #suggestMatching(SuggestionsBuilder, Iterable)}. These
 * suggest only strings that match the input in the given suggestions builder.
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
     * Tests if the given key is a valid suggestion
     * for the given token
     * @param token The token to test against
     * @param key The key to test
     * @return True, if the key is a valid suggestion, false otherwise
     */
    static boolean startsWith(String token, Key key) {
        return startsWith(token, key.namespace())
                || startsWith(token, key.value())
                || startsWith(token, key.asString());
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
            if (startsWith(token, s)) {
                builder.suggest(s);
            }
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
                b.suggest(entry.getKey(), CmdUtil.toTooltip(entry.getValue()));
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
            if (startsWith(token, k)) {
                builder.suggest(k.asString());
            }
        }

        return builder.buildFuture();
    }

    /**
     * Suggests all keys within a given registry
     * @param builder The builder to suggest to
     * @param registry The registry to suggest the keys of
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestRegistry(SuggestionsBuilder builder, org.bukkit.Registry<?> registry) {
        var token = builder.getRemainingLowerCase();

        for (var v: registry) {
            var key = v.key();

            if (startsWith(token, key)) {
                builder.suggest(key.asString());
            }
        }

        return builder.buildFuture();
    }

    /**
     * Suggests 3d cords to given builder
     * @param builder The builder to suggest to
     * @param allowDecimals Whether to allow decimal places in the suggestions
     * @param suggestions The coordinates to suggest
     * @return
     */
    static CompletableFuture<Suggestions> suggestCords(SuggestionsBuilder builder,
                                                       boolean allowDecimals,
                                                       Iterable<CoordinateSuggestion> suggestions
    ) {
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
        return suggestMatching(builder,
                Bukkit.getWorlds()
                        .stream()
                        .map(WorldInfo::getName)
        );
    }

    /**
     * Suggest sound event keys
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestSounds(SuggestionsBuilder builder) {
        return suggestRegistry(builder, org.bukkit.Registry.SOUNDS);
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
        return suggestRegistry(builder, org.bukkit.Registry.ENCHANTMENT);
    }

    /**
     * Suggest entity type keys
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestEntities(SuggestionsBuilder builder) {
        return suggestRegistry(builder, org.bukkit.Registry.ENTITY_TYPE);
    }

    /**
     * Suggest Scoreboard team names
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestTeams(SuggestionsBuilder builder) {
        return suggestMatching(builder,
                Bukkit.getScoreboardManager()
                        .getMainScoreboard()
                        .getTeams()
                        .stream()
                        .map(Team::getName)
        );
    }

    /**
     * Suggest Scoreboard objectives
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestObjectives(SuggestionsBuilder builder) {
        return suggestMatching(builder,
                Bukkit.getScoreboardManager()
                        .getMainScoreboard()
                        .getObjectives()
                        .stream()
                        .map(Objective::getName)
        );
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
        return suggestMatching(builder,
                Bukkit.getOnlinePlayers()
                        .stream()
                        .map(Player::getName)
        );
    }

    static CompletableFuture<Suggestions> suggestPlayerNames(SuggestionsBuilder builder, CommandSource source) {
        return suggestMatching(builder,
                Bukkit.getOnlinePlayers()
                        .stream()
                        .filter(player -> {
                            if (source.isPlayer()) {
                                var p = source.asPlayerOrNull();
                                return p.canSee(player);
                            }

                            return false;
                        })
                        .map(Player::getName)
        );
    }

    /**
     * Suggests loottables
     * @param builder The builder to suggest to
     * @return The built suggestions
     */
    static CompletableFuture<Suggestions> suggestLootTables(SuggestionsBuilder builder) {
        MinecraftServer server = MinecraftServer.getServer();
        MinecraftServer.ReloadableResources manager = server.resources;
        LootTables lootTables = manager.managers().getLootTables();

        return GrenadierUtils.suggestResource(lootTables.getIds(), builder);
    }
}