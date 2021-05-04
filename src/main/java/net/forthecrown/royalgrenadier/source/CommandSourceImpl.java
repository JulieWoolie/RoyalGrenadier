package net.forthecrown.royalgrenadier.source;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import io.papermc.paper.adventure.PaperAdventure;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class CommandSourceImpl implements CommandSource {

    public static final LegacyComponentSerializer lSerializer = LegacyComponentSerializer.builder().extractUrls().hexColors().character('&').hexCharacter('#').build();
    public static final DynamicCommandExceptionType INVALID_SENDER_TYPE =
            new DynamicCommandExceptionType(obj -> new LiteralMessage("Only " + obj.toString() + "s may execute this command"));

    private final CommandListenerWrapper source;
    private AbstractCommand current;
    public CommandSourceImpl(CommandListenerWrapper source, AbstractCommand currentCommand){
        this.source = source;
        this.current = currentCommand;
    }

    public CommandListenerWrapper getHandle(){
        return source;
    }

    @Override
    public <T extends CommandSender> boolean is(Class<T> clazz) {
        return clazz.isAssignableFrom(asBukkit().getClass());
    }

    @Override
    public CommandSender asBukkit() {
        return source.getBukkitSender();
    }

    @Override
    public <T extends CommandSender> T as(Class<T> clazz) throws CommandSyntaxException {
        CommandSender sender = asBukkit();

        if(is(clazz)) return (T) sender;
        throw INVALID_SENDER_TYPE.create(clazz.getSimpleName());
    }

    @Override
    public Location getLocation() {
        return source.getBukkitLocation();
    }

    @Override
    public Component displayName() {
        return PaperAdventure.asAdventure(source.getScoreboardDisplayName());
    }

    @Override
    public String textName() {
        return source.getName();
    }

    @Override
    public World getWorld() {
        return source.getBukkitWorld();
    }

    @Override
    public Server getServer() {
        return source.getServer().server;
    }

    @Override
    public boolean hasPermission(String s) {
        return asBukkit().hasPermission(s);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return asBukkit().hasPermission(permission);
    }

    @Override
    public boolean hasPermission(int level) {
        return source.hasPermission(level);
    }

    @Override
    public boolean isOp() {
        return source.getBukkitSender().isOp();
    }

    @Override
    public void setOp(boolean b) {
        source.getBukkitSender().setOp(b);
    }

    @Override
    public void sendMessage(String s) {
        source.getBukkitSender().sendMessage(s);
    }

    @Override
    public void sendMessage(Component component) {
        source.base.sendMessage(PaperAdventure.asVanilla(component), null);
    }

    @Override
    public void sendAdmin(Component component, boolean sendToSelf) {
        if(sendToSelf) sendMessage(component);
        if(!shouldBroadcastCommand()) return;

        String name = textName();
        Component message = Component.text()
                .append(Component.text("["))
                .append(displayName())
                .append(Component.text(": "))
                .append(component)
                .append(Component.text("]"))
                .style(Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC))
                .build();

        for (Player p: Bukkit.getOnlinePlayers()){
            if(!current.testPermissionSilent(p)) continue;

            //Don't send to self
            if(p.getName().equalsIgnoreCase(name)) continue;

            p.sendMessage(message);
        }

        //Let console know as well
        Bukkit.getConsoleSender().sendMessage(message);
    }

    @Override
    public void sendAdmin(String s) {
        sendAdmin(lSerializer.deserialize(s));
    }

    @Override
    public AbstractCommand getCurrentCommand() {
        return current;
    }

    @Override
    public void setCurrentCommand(AbstractCommand command) {
        this.current = command;
    }

    @Override
    public boolean shouldBroadcastCommand() {
        return source.base.shouldBroadcastCommands();
    }

    @Override
    public void onCommandComplete(CommandContext<CommandSource> context, boolean success, int result) {
        //Remove this source from the registry thing and set the current command to null
        CommandSources.remove(source);
        this.current = null;
    }
}
