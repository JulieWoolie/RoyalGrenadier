package net.forthecrown.grenadier.types.selectors;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.forthecrown.grenadier.CommandSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public interface EntitySelector {

    /**
     * Gets the player in the selector
     * @param source The source executing the command
     * @return The player in the selector
     * @throws CommandSyntaxException If the sender doesn't have permission to use a target selector
     */
    Player getPlayer(CommandSource source) throws CommandSyntaxException;

    /**
     * Gets the entity in the selector
     * @param source The source executing the command
     * @return The entity in the selector
     * @throws CommandSyntaxException If the sender doesn't have permission to use a target selector
     */
    Entity getEntity(CommandSource source) throws CommandSyntaxException;

    /**
     * Gets the entities in the selector
     * @param source The source executing the command
     * @return The entities in the selector
     * @throws CommandSyntaxException If the sender doesn't have permission to use a target selector
     */
    List<Entity> getEntities(CommandSource source) throws CommandSyntaxException;

    /**
     * Gets the players in the selector
     * @param source The source executing the command
     * @return The players in the selector
     * @throws CommandSyntaxException If the sender doesn't have permission to use a target selector
     */
    List<Player> getPlayers(CommandSource source) throws CommandSyntaxException;

    /**
     * Returns if the selector is pointing to the sender
     * @return Whether the selector is '@a'
     */
    boolean isSelfSelector();

    /**
     * Returns whether the selector is world limit or not
     * @return Whether the selector is limited to a world
     */
    boolean isWorldLimited();

    /**
     * Returns if the selector includes entities as well, not just players
     * @return
     */
    boolean includesEntities();

    /**
     * Gets the max amount of results
     * @return
     */
    int getMaxResults();
}
