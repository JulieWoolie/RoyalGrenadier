package net.forthecrown.grenadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.types.GameModeArgumentImpl;
import org.bukkit.GameMode;

public interface GameModeArgument extends ArgumentType<GameMode> {

    static GameModeArgument gameMode(){
        return GameModeArgumentImpl.INSTANCE;
    }

    static GameMode getGameMode(CommandContext<CommandSource> c, String argument){
        return c.getArgument(argument, GameMode.class);
    }

}
