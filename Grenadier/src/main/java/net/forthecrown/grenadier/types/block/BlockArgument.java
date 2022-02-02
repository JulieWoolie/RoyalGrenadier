package net.forthecrown.grenadier.types.block;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.types.block.BlockArgumentImpl;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.concurrent.CompletableFuture;

/**
 * Represents an argument which returns a {@link ParsedBlock}
 * @see ParsedBlock
 */
public interface BlockArgument extends ArgumentType<ParsedBlock> {

    static BlockArgument block(){
        return BlockArgumentImpl.INSTANCE;
    }

    static BlockData getData(CommandContext<CommandSource> c, String argument){
        return getBlock(c, argument).getData();
    }

    static Material getMaterial(CommandContext<CommandSource> c, String argument){
        return getBlock(c, argument).getMaterial();
    }

    static ParsedBlock getBlock(CommandContext<CommandSource> c, String argument){
        return c.getArgument(argument, ParsedBlock.class);
    }

    @Override
    ParsedBlock parse(StringReader reader) throws CommandSyntaxException;

    @Override
    <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder);

    <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder, boolean allowTags);
}
