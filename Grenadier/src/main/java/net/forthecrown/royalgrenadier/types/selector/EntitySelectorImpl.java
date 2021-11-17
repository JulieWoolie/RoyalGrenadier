package net.forthecrown.royalgrenadier.types.selector;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.types.selectors.EntitySelector;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class EntitySelectorImpl implements EntitySelector {

    private final net.minecraft.commands.arguments.selector.EntitySelector nms;

    EntitySelectorImpl(EntitySelectorParser parserSelector, boolean overridePerms) throws CommandSyntaxException {
        this.nms = parserSelector.parse(overridePerms);
    }

    public net.minecraft.commands.arguments.selector.EntitySelector getNms() {
        return nms;
    }

    @Override
    public Player getPlayer(CommandSource source) throws CommandSyntaxException {
        return nms.findSinglePlayer(GrenadierUtils.sourceToNms(source)).getBukkitEntity();
    }

    @Override
    public Entity getEntity(CommandSource source) throws CommandSyntaxException {
        return nms.findSingleEntity(GrenadierUtils.sourceToNms(source)).getBukkitEntity();
    }

    @Override
    public List<Entity> getEntities(CommandSource source) throws CommandSyntaxException {
        List<? extends net.minecraft.world.entity.Entity> nmsList = nms.findEntities(GrenadierUtils.sourceToNms(source));
        return GrenadierUtils.convertList(nmsList, net.minecraft.world.entity.Entity::getBukkitEntity);
    }

    @Override
    public List<Player> getPlayers(CommandSource source) throws CommandSyntaxException {
        List<ServerPlayer> nmsList = nms.findPlayers(GrenadierUtils.sourceToNms(source));
        return GrenadierUtils.convertList(nmsList, ServerPlayer::getBukkitEntity);
    }

    @Override
    public boolean isSelfSelector(){
        return nms.isSelfSelector();
    }

    @Override
    public boolean isWorldLimited(){
        return nms.isWorldLimited();
    }

    @Override
    public boolean includesEntities(){
        return nms.includesEntities();
    }

    @Override
    public int getMaxResults(){
        return nms.getMaxResults();
    }
}
