package net.forthecrown.grenadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.types.WorldArgumentImpl;
import org.bukkit.World;

public interface WorldArgument extends ArgumentType<World> {

    static WorldArgument world(){
        return WorldArgumentImpl.INSTANCE;
    }

    static World getWorld(CommandContext<CommandSource> c, String argument){
        return c.getArgument(argument, World.class);
    }
}
