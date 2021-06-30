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
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface CommandSource extends ResultConsumer<CommandSource>, ServerOperator {

    /**
     * Checks if the sender is of the type
     * @param clazz The class of the type to check
     * @param <T>
     * @return Whether the sender is of the type
     */
    <T extends CommandSender> boolean is(Class<T> clazz);

    /**
     * Gets the sender as the specified type
     * @param clazz The class of the type, must extend {@link CommandSender}
     * @param <T> The type
     * @return The sender as the type
     * @throws CommandSyntaxException If the sender is not of the specified type
     */
    <T extends CommandSender> T as(Class<T> clazz) throws CommandSyntaxException;

    /**
     * Gets the normal Bukkit CommandSender
     * @return the CommandSender of this source
     */
    CommandSender asBukkit();

    /**
     * Checks if the source is a player
     * @return Whether the sender is a player or not
     */
    default boolean isPlayer(){ return is(Player.class); }

    /**
     * Gets the source as a player
     * @return The player for this source
     * @throws CommandSyntaxException If the source is not a player
     */
    default Player asPlayer() throws CommandSyntaxException { return as(Player.class); }

    /**
     * Gets the display name of this source
     * @return The source's display name
     */
    Component displayName();

    /**
     * Gets the string representation of the display name
     * @return The string display name
     */
    String textName();

    /**
     * Gets the sender as the specified type, or null if sender isn't of the given type
     * @param clazz The class of the type, must extend {@link CommandSender}
     * @param <T> The type
     * @return The sender as the given type, or null if sender isn't of the given type
     */
    @Nullable <T extends CommandSender> T asOrNull(Class<T> clazz);

    /**
     * Gets the sender's location
     * @return The sender's location
     */
    Location getLocation();

    /**
     * Gets the world the sender is in
     * @return The sender's world
     */
    World getWorld();

    /**
     * Gets the server
     * @return The server lol
     */
    Server getServer();

    /**
     * Checks if the sender has the given permission
     * @param s The permission to check for
     * @return Whether they have the permission or not
     */
    boolean hasPermission(String s);

    /**
     * Checks if the sender has the given permission
     * @param permission The permission to check for
     * @return Whether they have the permission or not
     */
    boolean hasPermission(Permission permission);

    /**
     * Checks if the sender has the vanilla OP level
     * @param level The level to check for
     * @return Whether they have the given level of permissions
     */
    boolean hasPermission(int level);

    /**
     * Checks if the sender has both the bukkit permission and the OP permission level
     * @param perm The level to check
     * @param level The permission to check
     * @return Whether the sender has both
     */
    default boolean hasPermission(String perm, int level){ return hasPermission(perm) && hasPermission(level); }

    /**
     * Checks if the sender is opped
     * @return Whether the sender is opped or not
     */
    boolean isOp();

    /**
     * Sends a message to the sender
     * @param s The message to send
     */
    void sendMessage(String s);

    /**
     * Sends several messages to the sender lol
     * @param s The messages to send
     */
    default void sendMessage(String... s){
        for (String ss: s){
            sendMessage(ss);
        }
    }

    /**
     * Sends a message to the sender
     * @param component The message
     */
    void sendMessage(Component component);

    /**
     * Sends an admin message to this sender and everyone else with permission for the current command
     * @param component The message to send
     */
    default void sendAdmin(Component component){ sendAdmin(component, true); }

    /**
     * Sends an admin message to this sender and everyone else with permission for the current command
     * @param component The message
     * @param sendToSelf Whether the sender should also receive the message
     */
    void sendAdmin(Component component, boolean sendToSelf);

    /**
     * Sends an admin message to this sender and everyone else with permission for the current command
     * @param s The message
     */
    default void sendAdmin(String s) { sendAdmin(s, true); }

    /**
     * Sends an admin message to this sender and everyone else with permission for the current command
     * @param s The message
     * @param sendToSelf Whether the sender should also receive the message
     */
    void sendAdmin(String s, boolean sendToSelf);

    /**
     * Gets the current command the sender is using, null if no command is currently in use
     * @return The command the sender is currently using
     */
    AbstractCommand getCurrentCommand();

    /**
     * Sets the current command the sender is using
     * @param command The command the sender will be using
     */
    void setCurrentCommand(AbstractCommand command);

    /**
     * Gets if the sender should broadcast admin messages
     * @return Whether this sender should be broadcasting admin messages
     */
    boolean shouldInformAdmins();

    /**
     * Suggest matching strings for the specified SuggestionsBuilder
     * @deprecated Use {@link CompletionProvider#suggestMatching(SuggestionsBuilder, Iterable)}
     * @param b The builder
     * @param suggestions The suggestions to suggest
     * @return The suggestions of the given strings
     */
    @Deprecated
    static CompletableFuture<Suggestions> suggestMatching(SuggestionsBuilder b, Iterable<String> suggestions){
        return CompletionProvider.suggestMatching(b, suggestions);
    }
}
