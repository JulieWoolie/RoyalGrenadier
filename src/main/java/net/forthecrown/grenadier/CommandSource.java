package net.forthecrown.grenadier;

import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.concurrent.CompletableFuture;

public interface CommandSource extends ResultConsumer<CommandSource> {

    /**
     * Checks if the sender is of the type
     * @param clazz The class of the type to check
     * @param <T>
     * @return Whether the sender is of the type
     */
    <T extends CommandSender> boolean is(Class<T> clazz);


    <T extends CommandSender> T as(Class<T> clazz) throws CommandSyntaxException;

    CommandSender asBukkit();

    default boolean isPlayer(){ return is(Player.class); }
    default Player asPlayer() throws CommandSyntaxException{ return as(Player.class); }

    Component displayName();

    String textName();

    Location getLocation();
    World getWorld();
    Server getServer();

    boolean hasPermission(String s);
    boolean hasPermission(Permission permission);
    boolean hasPermission(int level);
    default boolean hasPermission(String perm, int level){ return hasPermission(perm) && hasPermission(level); }

    void sendMessage(String s);

    default void sendMessage(String... s){
        for (String ss: s){
            sendMessage(s);
        }
    }

    void sendMessage(Component component);

    default void sendAdmin(Component component){ sendAdmin(component, true); }
    void sendAdmin(Component component, boolean sendToSelf);
    void sendAdmin(String s);

    AbstractCommand getCurrentCommand();
    void setCurrentCommand(AbstractCommand command);

    boolean shouldBroadcastCommand();

    static CompletableFuture<Suggestions> suggestMatching(SuggestionsBuilder b, Iterable<String> string){
        String token = b.getRemaining().toLowerCase();
        for (String s: string) if(s.toLowerCase().startsWith(token)) b.suggest(s);

        return b.buildFuture();
    }
}
