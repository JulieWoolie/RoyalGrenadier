package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.types.WorldArgument;
import net.forthecrown.royalgrenadier.GrenadierUtils;
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
        if(result == null) throw UNKOWN_WORLD.createWithContext(GrenadierUtils.correctCursorReader(reader, cursor), name);

        return result;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CompletionProvider.suggestWorlds(builder);
    }

    @Override
    public Collection<String> getExamples() {
        return Arrays.asList("world", "world_event", "world_void");
    }
}
