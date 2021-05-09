package net.forthecrown.royalgrenadier;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.source.CommandSourceImpl;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.command.CraftBlockCommandSender;
import org.bukkit.craftbukkit.v1_16_R3.command.ProxiedNativeCommandSender;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class GrenadierUtils {

    public static CommandListenerWrapper sourceToNms(CommandSource source){
        return ((CommandSourceImpl) source).getHandle();
    }

    //Bukkit's getListener in VanillaCommandWrapper didn't have enough functionality to be as applicable
    public static CommandListenerWrapper senderToWrapper(CommandSender sender){
        if(sender instanceof Entity) return ((CraftEntity) sender).getHandle().getCommandListener();
        else if(sender instanceof BlockCommandSender) return ((CraftBlockCommandSender) sender).getWrapper();
        else if(sender instanceof RemoteConsoleCommandSender) return ((DedicatedServer) MinecraftServer.getServer()).remoteControlCommandListener.getWrapper();
        else if(sender instanceof ConsoleCommandSender) return ((CraftServer)sender.getServer()).getServer().getServerCommandListener();
        else if(sender instanceof ProxiedCommandSender) return ((ProxiedNativeCommandSender)sender).getHandle();
        else return null;
    }

    //Converts a list from one type to another using the given function
    public static <T, F> List<T> convertList(Iterable<F> from, Function<F, T> function){
        List<T> res = new ArrayList<>();
        for (F f: from) res.add(function.apply(f));

        return res;
    }

    public static ImmutableStringReader correctCursorReader(StringReader reader, int cursor){
        StringReader reader1 = new StringReader(reader.getString());
        reader1.setCursor(cursor);
        return reader1;
    }

    //Does the same thing as the above method but with an array
    public static <T, F> List<T> convertArray(F[] from, Function<F, T> function){
        return convertList(Arrays.asList(from), function);
    }

    //Suggests a specific MinecraftKey collection
    public static CompletableFuture<Suggestions> suggestResource(Iterable<MinecraftKey> resources, SuggestionsBuilder builder){
        return ICompletionProvider.a(resources, builder);
    }
}
