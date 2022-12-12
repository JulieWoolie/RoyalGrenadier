package net.forthecrown.royalgrenadier.types.block;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.types.block.BlockPredicateArgument;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.forthecrown.royalgrenadier.VanillaMappedArgument;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.bukkit.craftbukkit.v1_19_R2.block.CraftBlock;

import java.util.concurrent.CompletableFuture;

public class BlockPredicateArgumentImpl implements BlockPredicateArgument, VanillaMappedArgument {
    public static final BlockPredicateArgumentImpl INSTANCE = new BlockPredicateArgumentImpl();

    private final net.minecraft.commands.arguments.blocks.BlockPredicateArgument handle;

    public BlockPredicateArgumentImpl() {
        this.handle = new net.minecraft.commands.arguments.blocks.BlockPredicateArgument(
                GrenadierUtils.createBuildContext()
        );
    }

    @Override
    public Result parse(StringReader reader) throws CommandSyntaxException {
        var vanilla = handle.parse(reader);

        return block -> {
            CraftBlock craftBlock = (CraftBlock) block;

            BlockInWorld worldBlock = new BlockInWorld(
                    craftBlock.getHandle(),
                    craftBlock.getPosition(),
                    craftBlock.getChunk().isForceLoaded()
            );

            return vanilla.test(worldBlock);
        };
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return handle.listSuggestions(context, builder);
    }

    @Override
    public ArgumentType<?> getVanillaArgumentType() {
        return handle;
    }

    @Override
    public boolean useVanillaSuggestions() {
        return true;
    }
}