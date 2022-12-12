package net.forthecrown.royalgrenadier.types.block;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.types.block.BlockArgument;
import net.forthecrown.grenadier.types.block.ParsedBlock;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.forthecrown.royalgrenadier.VanillaMappedArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class BlockArgumentImpl implements BlockArgument, VanillaMappedArgument {
    protected BlockArgumentImpl() {}
    public static final BlockArgumentImpl INSTANCE = new BlockArgumentImpl();
    private final BlockStateArgument handle = BlockStateArgument.block(
            GrenadierUtils.createBuildContext()
    );

    @Override
    public ParsedBlock parse(StringReader reader) throws CommandSyntaxException {
        return parse(reader, true);
    }

    public ParsedBlock parse(StringReader reader, boolean allowNBT) throws CommandSyntaxException {
        BlockStateParser.BlockResult parser = BlockStateParser.parseForBlock(
                GrenadierUtils.createLookup(Registries.BLOCK), reader, allowNBT
        );

        return new ParsedBlockImpl(
                parser.blockState(),
                parser.properties(),
                parser.nbt(),
                BuiltInRegistries.BLOCK.getKey(parser.blockState().getBlock())
        );
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return listSuggestions(context, builder, true);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder, boolean allowTag) {
        return handle.listSuggestions(context, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return handle.getExamples();
    }

    public BlockStateArgument getVanillaArgumentType() {
        return handle;
    }

    @Override
    public boolean useVanillaSuggestions() {
        return true;
    }
}