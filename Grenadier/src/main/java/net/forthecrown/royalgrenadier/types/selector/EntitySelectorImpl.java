package net.forthecrown.royalgrenadier.types.selector;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.Getter;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.types.selectors.EntitySelector;
import net.forthecrown.royalgrenadier.WrappedCommandSource;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class EntitySelectorImpl implements EntitySelector {

    @Getter
    private final net.minecraft.commands.arguments.selector.EntitySelector nms;

    EntitySelectorImpl(net.minecraft.commands.arguments.selector.EntitySelector selector) {
        this.nms = selector;
    }

    @Override
    public Player getPlayer(CommandSource source) throws CommandSyntaxException {
        return nms.findSinglePlayer(WrappedCommandSource.getStack(source)).getBukkitEntity();
    }

    @Override
    public Entity getEntity(CommandSource source) throws CommandSyntaxException {
        return nms.findSingleEntity(WrappedCommandSource.getStack(source)).getBukkitEntity();
    }

    @Override
    public List<Entity> getEntities(CommandSource source) throws CommandSyntaxException {
        List<? extends net.minecraft.world.entity.Entity> nmsList = nms.findEntities(WrappedCommandSource.getStack(source));

        return nmsList
                .stream()
                .map(net.minecraft.world.entity.Entity::getBukkitEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Player> getPlayers(CommandSource source) throws CommandSyntaxException {
        List<ServerPlayer> nmsList = nms.findPlayers(WrappedCommandSource.getStack(source));

        return nmsList
                .stream()
                .map(ServerPlayer::getBukkitEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isSelfSelector() {
        return nms.isSelfSelector();
    }

    @Override
    public boolean isWorldLimited() {
        return nms.isWorldLimited();
    }

    @Override
    public boolean includesEntities() {
        return nms.includesEntities();
    }

    @Override
    public int getMaxResults() {
        return nms.getMaxResults();
    }
}