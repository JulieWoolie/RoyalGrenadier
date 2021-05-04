package net.forthecrown.royalgrenadier.types.selector;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.types.selectors.EntityArgument;
import net.forthecrown.grenadier.types.selectors.EntitySelector;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.minecraft.server.v1_16_R3.ArgumentParserSelector;
import net.minecraft.server.v1_16_R3.ChatMessage;
import net.minecraft.server.v1_16_R3.ICompletionProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class EntityArgumentImpl implements EntityArgument {

    public static final SimpleCommandExceptionType TOO_MANY_ENTITIES = new SimpleCommandExceptionType(new ChatMessage("argument.entity.toomany"));
    public static final SimpleCommandExceptionType TOO_MANY_PLAYERS = new SimpleCommandExceptionType(new ChatMessage("argument.player.toomany"));
    public static final SimpleCommandExceptionType ENTITIES_WHEN_NOT_ALLOWED = new SimpleCommandExceptionType(new ChatMessage("argument.player.entities"));
    public static final SimpleCommandExceptionType NO_ENTITIES_FOUND = new SimpleCommandExceptionType(new ChatMessage("argument.entity.notfound.entity"));
    public static final SimpleCommandExceptionType PLAYER_NOT_FOUND = new SimpleCommandExceptionType(new ChatMessage("argument.entity.notfound.player"));
    public static final SimpleCommandExceptionType SELECTOR_NOT_ALLOWED = new SimpleCommandExceptionType(new ChatMessage("argument.entity.selector.not_allowed"));

    private final boolean multiple;
    private final boolean allowEntities;

    public static final EntityArgumentImpl PLAYER = new EntityArgumentImpl(false, false);
    public static final EntityArgumentImpl PLAYERS = new EntityArgumentImpl(true, false);
    public static final EntityArgumentImpl ENTITY = new EntityArgumentImpl(false, true);
    public static final EntityArgumentImpl ENTITIES = new EntityArgumentImpl(true, true);

    public EntityArgumentImpl(boolean multiple, boolean allowEntities){
        this.multiple = multiple;
        this.allowEntities = allowEntities;
    }

    @Override
    public EntitySelector parse(StringReader reader) throws CommandSyntaxException {
        return parse(reader, false);
    }

    @Override
    public EntitySelector parse(StringReader reader, boolean overridePerms) throws CommandSyntaxException {
        ArgumentParserSelector parser = new ArgumentParserSelector(reader);
        EntitySelector selector = new EntitySelectorImpl(parser, overridePerms);

        if(!allowEntities && selector.includesEntities()) throw ENTITIES_WHEN_NOT_ALLOWED.createWithContext(reader);
        if(!multiple && selector.getMaxResults() > 1){
            if(allowEntities) throw TOO_MANY_ENTITIES.createWithContext(reader);
            else throw TOO_MANY_PLAYERS.createWithContext(reader);
        }

        if(selector.getMaxResults() < 1) {
            if(allowEntities) throw NO_ENTITIES_FOUND.createWithContext(reader);
            else throw PLAYER_NOT_FOUND.createWithContext(reader);
        }

        return selector;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if(context.getSource() instanceof CommandSource){
            StringReader reader = new StringReader(builder.getInput());
            reader.setCursor(builder.getStart());

            CommandSource source = (CommandSource) context.getSource();
            ArgumentParserSelector parser = new ArgumentParserSelector(reader, source.hasPermission("minecraft.command.selector"));

            try {
                parser.parse();
            } catch (CommandSyntaxException ignored) {}

            return parser.a(builder, b -> {
                Collection<String> collection = GrenadierUtils.convertList(Bukkit.getOnlinePlayers(), Player::getName);
                ICompletionProvider.b(collection, b);
            });

        } else return Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return null;
    }
}
