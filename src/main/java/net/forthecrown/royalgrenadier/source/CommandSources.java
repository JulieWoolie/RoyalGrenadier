package net.forthecrown.royalgrenadier.source;

import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandSources {
    private static final Map<CommandListenerWrapper, CommandSource> map = new HashMap<>();

    public static CommandSource getOrCreate(CommandListenerWrapper wrapper, AbstractCommand builder){
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

    public static void remove(CommandListenerWrapper wrapper){
        map.remove(wrapper);
    }

    public static void remove(CommandSource source){
        remove(((CommandSourceImpl) source).getHandle());
    }
}
