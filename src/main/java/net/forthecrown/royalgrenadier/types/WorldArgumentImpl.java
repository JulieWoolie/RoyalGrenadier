package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.forthecrown.grenadier.types.WorldArgument;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class WorldArgumentImpl implements WorldArgument {
    protected WorldArgumentImpl() {}
    public static final WorldArgumentImpl INSTANCE = new WorldArgumentImpl();

    public static final DynamicCommandExceptionType UNKOWN_WORLD = new DynamicCommandExceptionType(obj -> () -> "Unknown world: " + obj.toString());

    @Override
    public World parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        String name = reader.readUnquotedString();

        World result = Bukkit.getWorld(name);
        if(result == null){
            reader.setCursor(cursor);
            throw UNKOWN_WORLD.createWithContext(reader, name);
        }

        return result;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(builder, GrenadierUtils.convertList(Bukkit.getWorlds(), World::getName));
    }

    @Override
    public Collection<String> getExamples() {
        return Arrays.asList("world", "world_event", "world_void");
    }
}