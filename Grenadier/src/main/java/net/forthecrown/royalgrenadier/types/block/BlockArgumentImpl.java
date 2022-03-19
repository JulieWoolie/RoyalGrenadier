package net.forthecrown.royalgrenadier.types.block;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.types.block.BlockArgument;
import net.forthecrown.grenadier.types.block.ParsedBlock;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Registry;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class BlockArgumentImpl implements BlockArgument {
    protected BlockArgumentImpl() {}
    public static final BlockArgumentImpl INSTANCE = new BlockArgumentImpl();
    private final BlockStateArgument handle = BlockStateArgument.block();

    @Override
    public ParsedBlock parse(StringReader reader) throws CommandSyntaxException {
        return parse(reader, true, true);
    }

    public ParsedBlock parse(StringReader reader, boolean allowTag, boolean allowNBT) throws CommandSyntaxException {
        BlockStateParser parser = new BlockStateParser(reader, allowTag).parse(allowNBT);

        return new ParsedBlockImpl(parser.getState(), parser.getProperties().keySet(), parser.getNbt(), parser.getTag().location());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return listSuggestions(context, builder, true);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder, boolean allowTag) {
        StringReader reader = new StringReader(builder.getInput());
        reader.setCursor(builder.getStart());

        BlockStateParser parser = new BlockStateParser(reader, allowTag);

        try {
            parser.parse(true);
        } catch (CommandSyntaxException ignored) {}

        return parser.fillSuggestions(builder, Registry.BLOCK);
    }

    @Override
    public Collection<String> getExamples() {
        return handle.getExamples();
    }

    public BlockStateArgument getHandle() {
        return handle;
    }
}
