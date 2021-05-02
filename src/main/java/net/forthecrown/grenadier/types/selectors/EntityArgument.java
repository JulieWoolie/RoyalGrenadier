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

public interface EntityArgument extends ArgumentType<EntitySelector> {

    static EntityArgument multipleEntities(){
        return EntityArgumentImpl.ENTITIES;
    }

    static EntityArgument entity(){
        return EntityArgumentImpl.ENTITY;
    }

    static EntityArgument player(){
        return EntityArgumentImpl.PLAYER;
    }

    static EntityArgument players(){
        return EntityArgumentImpl.PLAYERS;
    }

    static Player getPlayer(CommandContext<CommandSource> c, String argument) throws CommandSyntaxException {
        return c.getArgument(argument, EntitySelector.class).getPlayer(c.getSource());
    }

    static List<Player> getPlayers(CommandContext<CommandSource> c, String argument) throws CommandSyntaxException {
        return c.getArgument(argument, EntitySelector.class).getPlayers(c.getSource());
    }

    static Entity getEntity(CommandContext<CommandSource> c, String argument) throws CommandSyntaxException {
        return c.getArgument(argument, EntitySelector.class).getEntity(c.getSource());
    }

    static List<Entity> getEntities(CommandContext<CommandSource> c, String argument) throws CommandSyntaxException {
        return c.getArgument(argument, EntitySelector.class).getEntities(c.getSource());
    }

    EntitySelector parse(StringReader reader, boolean overridePerms) throws CommandSyntaxException;

    @Override
    EntitySelector parse(StringReader reader) throws CommandSyntaxException;
}
