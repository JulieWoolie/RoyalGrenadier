package net.forthecrown.grenadier.types.block;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.types.block.BlockArgumentImpl;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

/**
 * Represents an argument which returns a {@link ParsedBlock}
 * @see ParsedBlock
 */
public interface BlockArgument extends ArgumentType<ParsedBlock> {

    static BlockArgument block() {
        return BlockArgumentImpl.INSTANCE;
    }

    static BlockData getData(CommandContext<CommandSource> c, String argument) {
        return getBlock(c, argument).getData();
    }

    static Material getMaterial(CommandContext<CommandSource> c, String argument) {
        return getBlock(c, argument).getMaterial();
    }

    static ParsedBlock getBlock(CommandContext<CommandSource> c, String argument) {
        return c.getArgument(argument, ParsedBlock.class);
    }
}