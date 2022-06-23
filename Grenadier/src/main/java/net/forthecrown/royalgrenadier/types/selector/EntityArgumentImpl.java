package net.forthecrown.royalgrenadier.types.selector;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.exceptions.TranslatableExceptionType;
import net.forthecrown.grenadier.types.selectors.EntityArgument;
import net.forthecrown.grenadier.types.selectors.EntitySelector;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.forthecrown.royalgrenadier.VanillaMappedArgument;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class EntityArgumentImpl implements EntityArgument, VanillaMappedArgument {
    private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");

    public static final TranslatableExceptionType TOO_MANY_ENTITIES = new TranslatableExceptionType("argument.entity.toomany");
    public static final TranslatableExceptionType TOO_MANY_PLAYERS = new TranslatableExceptionType("argument.player.toomany");
    public static final TranslatableExceptionType ENTITIES_WHEN_NOT_ALLOWED = new TranslatableExceptionType("argument.player.entities");
    public static final TranslatableExceptionType NO_ENTITIES_FOUND = new TranslatableExceptionType("argument.entity.notfound.entity");
    public static final TranslatableExceptionType PLAYER_NOT_FOUND = new TranslatableExceptionType("argument.entity.notfound.player");
    public static final TranslatableExceptionType SELECTOR_NOT_ALLOWED = new TranslatableExceptionType("argument.entity.selector.not_allowed");

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
    public boolean allowsMultiple() {
        return multiple;
    }

    @Override
    public boolean allowsEntities() {
        return allowEntities;
    }

    @Override
    public EntitySelector parse(StringReader reader, boolean overridePerms) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        EntitySelectorParser parser = new EntitySelectorParser(reader); //NMS parser
        EntitySelector selector = new EntitySelectorImpl(parser, overridePerms);

        ImmutableStringReader correctCursor = GrenadierUtils.correctReader(reader, cursor);

        if(!allowEntities && selector.includesEntities() && !selector.isSelfSelector()) throw ENTITIES_WHEN_NOT_ALLOWED.createWithContext(correctCursor);
        if(!multiple && selector.getMaxResults() > 1){
            if(allowEntities) throw TOO_MANY_ENTITIES.createWithContext(correctCursor);
            else throw TOO_MANY_PLAYERS.createWithContext(correctCursor);
        }

        if(selector.getMaxResults() < 1) {
            if(allowEntities) throw NO_ENTITIES_FOUND.createWithContext(correctCursor);
            else throw PLAYER_NOT_FOUND.createWithContext(correctCursor);
        }

        return selector;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if(context.getSource() instanceof CommandSource) {
            StringReader reader = new StringReader(builder.getInput());
            reader.setCursor(builder.getStart());

            CommandSource source = (CommandSource) context.getSource();
            EntitySelectorParser parser = new EntitySelectorParser(reader, source.hasPermission("minecraft.command.selector"));

            try {
                parser.parse();
            } catch (CommandSyntaxException ignored) {}

            return parser.fillSuggestions(builder, b -> {
                Collection<String> collection = GrenadierUtils.convertList(Bukkit.getOnlinePlayers(), Player::getName);
                SharedSuggestionProvider.suggest(collection, b);
            });

        } else return Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public net.minecraft.commands.arguments.EntityArgument getVanillaArgumentType() {
        if(allowEntities){
            if(multiple) return net.minecraft.commands.arguments.EntityArgument.entities();
            else return net.minecraft.commands.arguments.EntityArgument.entity();
        } else {
            if(multiple) return net.minecraft.commands.arguments.EntityArgument.players();
            else return net.minecraft.commands.arguments.EntityArgument.player();
        }
    }

    @Override
    public boolean useVanillaSuggestions() {
        return true;
    }
}