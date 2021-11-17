package net.forthecrown.grenadier.types.pos;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.types.pos.PositionArgumentImpl;
import org.bukkit.Location;

public interface PositionArgument extends ArgumentType<Position> {

    /**
     * A vector position argument which allows decimal places in the given input.
     * @return A vector position argument
     */
    static PositionArgument position(){
        return PositionArgumentImpl.VECTOR_INSTANCE;
    }

    /**
     * Returns a 2D vector position argument.
     * <p></p>
     * Takes in two coordinates instead of 3 and will always parse a position with a Y level of 0
     * @return A 2D vector argument
     */
    static PositionArgument position2D() {
        return PositionArgumentImpl.VECTOR_2D_INSTANCE;
    }

    /**
     * A block position argument which only accepts integers in the given input
     * @return A block position argument
     */
    static PositionArgument blockPos(){
        return PositionArgumentImpl.BLOCK_INSTANCE;
    }

    /**
     * Returns a 2d block pos argument, aka, a column pos argument
     * <p></p>
     * Accepts only 2 cords, both must be integers or relative coordinates, will always return with a Y level of 0
     * @return A 2d block pos argument
     */
    static PositionArgument blockPos2D() {
        return PositionArgumentImpl.BLOCK_2D_INSTANCE;
    }

    static Location getLocation(CommandContext<CommandSource> c, String argument){
        return c.getArgument(argument, Position.class).getLocation(c.getSource());
    }
}
