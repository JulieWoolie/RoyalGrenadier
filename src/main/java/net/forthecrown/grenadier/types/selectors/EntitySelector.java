package net.forthecrown.grenadier.types.selectors;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.forthecrown.grenadier.CommandSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public interface EntitySelector {
    Player getPlayer(CommandSource source) throws CommandSyntaxException;

    Entity getEntity(CommandSource source) throws CommandSyntaxException;

    List<Entity> getEntities(CommandSource source) throws CommandSyntaxException;

    List<Player> getPlayers(CommandSource source) throws CommandSyntaxException;

    boolean isSelfSelector();

    boolean isWorldLimited();

    boolean includesEntities();

    int getMaxResults();
}
