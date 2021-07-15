package net.forthecrown.grenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.types.LootTableArgumentImpl;
import org.bukkit.loot.LootTable;

import java.util.concurrent.CompletableFuture;

/**
 * Parses a loot table from the given namespaced key input.
 * <p></p>
 * Note: If the correct loottable is not not found, minecraft defaults to the empty loot table
 */
public interface LootTableArgument extends ArgumentType<LootTable> {

    static LootTableArgument lootTable() {
        return LootTableArgumentImpl.INSTANCE;
    }

    static LootTable getLootTable(CommandContext<CommandSource> c, String argument) {
        return c.getArgument(argument, LootTable.class);
    }

    @Override
    LootTable parse(StringReader reader) throws CommandSyntaxException;

    @Override
    <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder);
}
