package net.forthecrown.grenadier.types.selectors;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.types.selector.EntityArgumentImpl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * An entity argument is a type which takes in either a UUID, playername
 * or entity selector, like '@a' and returns all the entities it found.
 */
public interface EntityArgument extends ArgumentType<EntitySelector> {

    /**
     * An instance which allows multiple entities
     * to be given as input
     * @return Multi entity argument
     */
    static EntityArgument multipleEntities() {
        return EntityArgumentImpl.ENTITIES;
    }

    /**
     * An instance which only allows 1 entity
     * to be given as input
     * @return A single entity argument
     */
    static EntityArgument entity() {
        return EntityArgumentImpl.ENTITY;
    }

    /**
     * An instance which only allows a single
     * player to be given as input
     * @return A single player argument
     */
    static EntityArgument player() {
        return EntityArgumentImpl.PLAYER;
    }

    /**
     * An instance which takes 1 or more players
     * as input.
     * @return A multi player argument
     */
    static EntityArgument players() {
        return EntityArgumentImpl.PLAYERS;
    }

    /**
     * Gets a selected player
     * @param c The context to use
     * @param argument The name of the argument
     * @return The found player
     * @throws CommandSyntaxException If no player was found
     */
    static Player getPlayer(CommandContext<CommandSource> c, String argument) throws CommandSyntaxException {
        return c.getArgument(argument, EntitySelector.class).getPlayer(c.getSource());
    }

    /**
     * Gets a list of players
     * @param c The context to use
     * @param argument The name of the argument
     * @return The found players
     * @throws CommandSyntaxException If no players were found
     */
    static List<Player> getPlayers(CommandContext<CommandSource> c, String argument) throws CommandSyntaxException {
        return c.getArgument(argument, EntitySelector.class).getPlayers(c.getSource());
    }

    /**
     * Gets a selected entity
     * @param c The context to use
     * @param argument The name of the argument
     * @return The found entity
     * @throws CommandSyntaxException If no entity was found
     */
    static Entity getEntity(CommandContext<CommandSource> c, String argument) throws CommandSyntaxException {
        return c.getArgument(argument, EntitySelector.class).getEntity(c.getSource());
    }

    /**
     * Gets a list of entities
     * @param c The context to use
     * @param argument The name of the argument
     * @return The found entities
     * @throws CommandSyntaxException If no entities were found
     */
    static List<Entity> getEntities(CommandContext<CommandSource> c, String argument) throws CommandSyntaxException {
        return c.getArgument(argument, EntitySelector.class).getEntities(c.getSource());
    }

    /**
     * Gets whether this instance allows more than
     * 1 entity/player to be given as input
     * @return True, if this instance can parse more than 1 entity/player
     */
    boolean allowsMultiple();

    /**
     * Gets whether this instance allows both entities and players
     * to be given as input
     * @return True, if entities are a valid input beside players, false otherwise
     */
    boolean allowsEntities();

    /**
     * Parses the selector from the given reader
     * @param reader The reader to parse
     * @param overridePerms Whether the reader should override permissions.
     *                      If true, the given selector will not check if the source
     *                      is allowed to use a selector when calling the {@link EntitySelector#getEntity(CommandSource)}
     *                      or similar methods.
     * @return The parsed selector
     * @throws CommandSyntaxException If the parsing fails
     */
    EntitySelector parse(StringReader reader, boolean overridePerms) throws CommandSyntaxException;

    /**
     * Parses the entity selector without overriding permissions
     * @param reader The reader to parse
     * @return The parsed selector
     * @throws CommandSyntaxException If the parsing fails
     */
    @Override
    default EntitySelector parse(StringReader reader) throws CommandSyntaxException {
        return parse(reader, false);
    }
}