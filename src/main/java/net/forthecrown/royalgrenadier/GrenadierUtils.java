package net.forthecrown.royalgrenadier;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.source.CommandSourceImpl;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.command.CraftBlockCommandSender;
import org.bukkit.craftbukkit.v1_17_R1.command.ProxiedNativeCommandSender;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class GrenadierUtils {

    public static CommandSourceStack sourceToNms(CommandSource source){
        return ((CommandSourceImpl) source).getHandle();
    }

    //Bukkit's getListener in VanillaCommandWrapper didn't have enough functionality to be as applicable
    public static CommandSourceStack senderToWrapper(CommandSender sender){
        if(sender instanceof Entity) return ((CraftEntity) sender).getHandle().createCommandSourceStack();
        else if(sender instanceof BlockCommandSender) return ((CraftBlockCommandSender) sender).getWrapper();
        else if(sender instanceof RemoteConsoleCommandSender || sender instanceof ConsoleCommandSender) return ((CraftServer) Bukkit.getServer()).getServer().createCommandSourceStack();
        else if(sender instanceof ProxiedCommandSender) return ((ProxiedNativeCommandSender)sender).getHandle();
        else return null;
    }

    //Converts a list from one type to another using the given function
    public static <T, F> List<T> convertList(Iterable<F> from, Function<F, T> function){
        List<T> res = new ArrayList<>();
        for (F f: from) res.add(function.apply(f));

        return res;
    }

    //Does the same thing as the above method but with an array
    public static <T, F> List<T> convertArray(F[] from, Function<F, T> function){
        return convertList(Arrays.asList(from), function);
    }

    //Returns a reader with the cursor at the correct position
    //Normally reading and then throwing exceptions causes the cursor to be placed at the wrong position
    //So we must correct it, but also do so with a new reader so the reader in the parse method moves forward
    public static ImmutableStringReader correctCursorReader(StringReader reader, int cursor){
        StringReader reader1 = new StringReader(reader.getString());
        reader1.setCursor(cursor);
        return reader1;
    }

    //Suggests a specific MinecraftKey collection
    public static CompletableFuture<Suggestions> suggestResource(Iterable<ResourceLocation> resources, SuggestionsBuilder builder){
        return SharedSuggestionProvider.suggestResource(resources, builder);
    }

    public static NamespacedKey readKey(StringReader reader, String defaultNamespace) {
        String initial = reader.readUnquotedString();
        if(reader.canRead() && reader.peek() == ':'){
            reader.skip();
            String key = reader.readUnquotedString();

            return new NamespacedKey(initial, key);
        }

        return new NamespacedKey(defaultNamespace, initial);
    }
}
