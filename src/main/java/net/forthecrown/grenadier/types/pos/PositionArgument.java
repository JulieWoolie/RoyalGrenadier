package net.forthecrown.grenadier.types.pos;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.types.pos.PositionArgumentImpl;
import org.bukkit.Location;

public interface PositionArgument extends ArgumentType<Position> {
    static PositionArgument position(){
        return PositionArgumentImpl.VECTOR_INSTANCE;
    }

    static PositionArgument blockPos(){
        return PositionArgumentImpl.BLOCK_INSTANCE;
    }

    static Location getLocation(CommandContext<CommandSource> c, String argument){
        return c.getArgument(argument, Position.class).getLocation(c.getSource());
    }
}
