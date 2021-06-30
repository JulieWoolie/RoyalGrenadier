package net.forthecrown.royalgrenadier.source;

import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandSources {
    //I believe this is better than creating a new instance of the command sender everytime you execute a command
    //Or get suggestions
    private static final Map<CommandSourceStack, CommandSource> map = new HashMap<>();

    public static CommandSource getOrCreate(CommandSourceStack wrapper, AbstractCommand builder){
        if(map.containsKey(wrapper)){
            CommandSource source = map.get(wrapper);
            source.setCurrentCommand(builder);
            return source;
        }

        CommandSourceImpl source = new CommandSourceImpl(wrapper, builder);
        map.put(wrapper, source);
        return source;
    }

    public static CommandSource getOrCreate(CommandSender sender, AbstractCommand builder){
        return getOrCreate(GrenadierUtils.senderToWrapper(sender), builder);
    }

    public static void remove(CommandSourceStack wrapper){
        map.remove(wrapper);
    }

    public static void remove(CommandSource source){
        remove(((CommandSourceImpl) source).getHandle());
    }
}
