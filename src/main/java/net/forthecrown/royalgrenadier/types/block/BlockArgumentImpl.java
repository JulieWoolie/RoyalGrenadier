package net.forthecrown.royalgrenadier.types.block;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.types.block.BlockArgument;
import net.forthecrown.grenadier.types.block.ParsedBlock;
import net.minecraft.server.v1_16_R3.ArgumentTile;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class BlockArgumentImpl implements BlockArgument {
    protected BlockArgumentImpl() {}
    public static final BlockArgumentImpl INSTANCE = new BlockArgumentImpl();

    @Override
    public ParsedBlock parse(StringReader reader) throws CommandSyntaxException {
        return new ParsedBlockImpl(ArgumentTile.a().parse(reader));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CompletionProvider.suggestBlocks(builder);
    }

    @Override
    public Collection<String> getExamples() {
        return ArgumentTile.a().getExamples();
    }
}
