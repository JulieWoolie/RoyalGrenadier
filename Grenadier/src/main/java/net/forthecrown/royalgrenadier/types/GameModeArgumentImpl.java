package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.types.GameModeArgument;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import org.bukkit.GameMode;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameModeArgumentImpl implements GameModeArgument {
    public static final GameModeArgumentImpl INSTANCE = new GameModeArgumentImpl();
    protected GameModeArgumentImpl() {}

    public static final DynamicCommandExceptionType
            UNKNOWN_GAMEMODE = new DynamicCommandExceptionType(o -> () -> "Invalid gamemode: '" + o + "'");

    @Override
    public GameMode parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        String label = reader.readUnquotedString();

        return switch (label.toLowerCase()) {
            case "survival",   "s", "0" -> GameMode.SURVIVAL;
            case "creative",   "c", "1" -> GameMode.CREATIVE;
            case "adventure",  "a", "2" -> GameMode.ADVENTURE;
            case "spectator", "sp", "3" -> GameMode.SPECTATOR;

            default -> {
                throw UNKNOWN_GAMEMODE
                        .createWithContext(
                                GrenadierUtils.correctReader(reader, cursor),
                                label
                        );
            }
        };
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CompletionProvider.suggestMatching(builder, getExamples());
    }

    @Override
    public Collection<String> getExamples() {
        return Stream.of(GameMode.values())
                .map(mode -> mode.name().toLowerCase())
                .collect(Collectors.toList());
    }
}