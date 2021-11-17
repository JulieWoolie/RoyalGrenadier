package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.types.KeyArgument;
import net.forthecrown.grenadier.types.LootTableArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.loot.LootTable;

import java.util.concurrent.CompletableFuture;

public class LootTableArgumentImpl implements LootTableArgument {
    public static final LootTableArgumentImpl INSTANCE = new LootTableArgumentImpl();
    protected LootTableArgumentImpl() {}

    @Override
    public LootTable parse(StringReader reader) throws CommandSyntaxException {
        NamespacedKey key = KeyArgument.minecraft().parse(reader);

        return Bukkit.getLootTable(key);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CompletionProvider.suggestLootTables(builder);
    }

    public ArgumentType<ResourceLocation> getHandle() {
        return ResourceLocationArgument.id();
    }
}
