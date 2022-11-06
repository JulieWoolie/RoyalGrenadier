package net.forthecrown.grenadier.types.block;

import com.mojang.brigadier.arguments.ArgumentType;
import net.forthecrown.royalgrenadier.types.block.BlockPredicateArgumentImpl;
import org.bukkit.block.Block;

import java.util.function.Predicate;

/**
 * A block predicate argument is an argument type
 * that parses a tag or block for the purpose of
 * using that parse result as a predicate against
 * existing blocks
 */
public interface BlockPredicateArgument extends ArgumentType<BlockPredicateArgument.Result> {

    /**
     * Gets the argument instance
     * @return The argument's instance
     */
    static BlockPredicateArgument blockPredicate() {
        return BlockPredicateArgumentImpl.INSTANCE;
    }

    /** The result parsed by this argument */
    interface Result extends Predicate<Block> {}
}