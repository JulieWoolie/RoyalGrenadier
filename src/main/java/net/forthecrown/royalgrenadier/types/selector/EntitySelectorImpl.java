package net.forthecrown.royalgrenadier.types.selector;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.types.selectors.EntitySelector;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.minecraft.server.v1_16_R3.ArgumentParserSelector;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class EntitySelectorImpl implements EntitySelector {

    private final net.minecraft.server.v1_16_R3.EntitySelector nms;

    EntitySelectorImpl(ArgumentParserSelector parserSelector, boolean overridePerms) throws CommandSyntaxException {
        this.nms = parserSelector.parse(overridePerms);
    }

    public net.minecraft.server.v1_16_R3.EntitySelector getNms() {
        return nms;
    }

    @Override
    public Player getPlayer(CommandSource source) throws CommandSyntaxException {
        return nms.c(GrenadierUtils.sourceToNms(source)).getBukkitEntity();
    }

    @Override
    public Entity getEntity(CommandSource source) throws CommandSyntaxException {
        return nms.a(GrenadierUtils.sourceToNms(source)).getBukkitEntity();
    }

    @Override
    public List<Entity> getEntities(CommandSource source) throws CommandSyntaxException {
        List<? extends net.minecraft.server.v1_16_R3.Entity> nmsList = nms.getEntities(GrenadierUtils.sourceToNms(source));
        return GrenadierUtils.convertList(nmsList, net.minecraft.server.v1_16_R3.Entity::getBukkitEntity);
    }

    @Override
    public List<Player> getPlayers(CommandSource source) throws CommandSyntaxException {
        List<EntityPlayer> nmsList = nms.d(GrenadierUtils.sourceToNms(source));
        return GrenadierUtils.convertList(nmsList, EntityPlayer::getBukkitEntity);
    }

    @Override
    public boolean isSelfSelector(){
        return nms.c();
    }

    @Override
    public boolean isWorldLimited(){
        return nms.d();
    }

    @Override
    public boolean includesEntities(){
        return nms.b();
    }

    @Override
    public int getMaxResults(){
        return nms.a();
    }
}
