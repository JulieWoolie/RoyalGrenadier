package net.forthecrown.grenadier;

import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.forthecrown.grenadier.types.pos.Vec2Suggestion;
import net.forthecrown.grenadier.types.pos.Vec3Suggestion;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface CommandSource extends ResultConsumer<CommandSource>, ServerOperator, Audience {

    /**
     * Checks if the sender is of the type
     * <p></p>
     * For example, to use this to check if the sender is a player you'd use
     * {@code boolean isPlayer = is(Player.class); }
     *
     * @param clazz The class of the type to check
     * @param <T>
     * @return Whether the sender is of the type
     */
    <T extends CommandSender> boolean is(Class<T> clazz);

    /**
     * Gets the sender as the specified type
     * <p></p>
     * To use this to get the sender as, for example, a slime, you'd do:
     * {@code Slime slime = as(Slime.class); }
     *
     * @param clazz The class of the type, must extend {@link CommandSender}
     * @param <T> The type
     * @return The sender as the type
     * @throws CommandSyntaxException If the sender is not of the specified type
     */
    <T extends CommandSender> T as(Class<T> clazz) throws CommandSyntaxException;

    /**
     * Similar to {@link CommandSource#as(Class)} however it won't throw an exception if the sender is not of the
     * given type, rather it just returns null
     *
     * @param clazz The class of the type, must extend {@link CommandSender}
     * @param <T> The type
     * @return The sender as the given type, or null if sender isn't of the given type
     */
    @Nullable <T extends CommandSender> T asOrNull(Class<T> clazz);

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
     * <p></p>
     * Constants for levels are in {@link PermissionLevels}
     * @see PermissionLevels
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
    default void sendMessage(@NotNull Component component) { sendMessage(component, (UUID) null); }

    /**
     * Sends a message to the sender
     * @param message The message
     * @param id The ID of the sender
     */
    void sendMessage(Component message, @Nullable UUID id);

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
     * Broadcasts the message to other admins
     * @param message The message to broadcast
     */
    void broadcastAdmin(Component message);

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
     * Gets a coordinate suggestion relevant to this source, or null, if no relevant cords were found in a 5 block distance
     * @return Gets the cords of the spot the source is looking at
     */
    @Nullable Vec3Suggestion getRelevant3DCords();

    /**
     * Gets a coordinate suggestion relevant to this source, or null, if no relevant cords were found in a 5 block distance
     * @return Gets the cords of the spot the source is looking at
     */
    @Nullable Vec2Suggestion getRelevant2DCords();

    /**
     * Checks if this source is 'silent', meaning it doesn't accept any
     * messages
     * @return True, if silent, false otherwise
     */
    boolean isSilent();

    /**
     * Checks whether this source accepts command success messages
     * @return Whether this source accepts success messages
     */
    boolean acceptsSuccessMessage();

    /**
     * Checks whether this source accepts command failure messages
     * @return Whether this source accepts failure messages
     */
    boolean acceptsFailureMessage();

    /**
     * Sends a success message
     * @param msg The message to send
     */
    default void sendSuccess(Component msg) {
        sendSuccess(msg, true);
    }

    /**
     * Send a command success message
     * @param msg The message to send
     * @param broadcast Whether to broadcast that message to other OPs
     */
    default void sendSuccess(Component msg, boolean broadcast) {
        if(!isSilent() && acceptsSuccessMessage()) {
            sendMessage(msg);
        }

        if(broadcast && !isSilent() && shouldInformAdmins()) {
            broadcastAdmin(msg);
        }
    }

    /**
     * Send a command failure message
     * @param msg The message to send
     */
    default void sendFailure(Component msg) {
        sendFailure(msg, false);
    }

    /**
     * Send a command failure message
     * @param msg The message to send
     * @param broadcast Whether to broadcast that message to other OPs
     */
    default void sendFailure(Component msg, boolean broadcast) {
        if(!isSilent() && acceptsFailureMessage()) {
            sendMessage(msg);
        }

        if(broadcast && !isSilent() && shouldInformAdmins()) {
            broadcastAdmin(msg);
        }
    }

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

    @Override
    default void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        if(isSilent()) return;
        asBukkit().sendMessage(source, message, type);
    }

    @Override
    default void sendActionBar(@NotNull Component message) {
        if(isSilent()) return;
        asBukkit().sendActionBar(message);
    }

    @Override
    default void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
        if(isSilent()) return;
        asBukkit().sendPlayerListHeaderAndFooter(header, footer);
    }

    @Override
    default <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
        if(isSilent()) return;
        asBukkit().sendTitlePart(part, value);
    }

    @Override
    default void clearTitle() {
        if(isSilent()) return;
        asBukkit().clearTitle();
    }

    @Override
    default void resetTitle() {
        if(isSilent()) return;
        asBukkit().resetTitle();
    }

    @Override
    default void showBossBar(@NotNull BossBar bar) {
        if(isSilent()) return;
        asBukkit().showBossBar(bar);
    }

    @Override
    default void hideBossBar(@NotNull BossBar bar) {
        if(isSilent()) return;
        asBukkit().hideBossBar(bar);
    }

    @Override
    default void playSound(@NotNull Sound sound) {
        if(isSilent()) return;
        asBukkit().playSound(sound);
    }

    @Override
    default void playSound(@NotNull Sound sound, Sound.@NotNull Emitter emitter) {
        if(isSilent()) return;
        asBukkit().playSound(sound, emitter);
    }

    @Override
    default void playSound(@NotNull Sound sound, double x, double y, double z) {
        if(isSilent()) return;
        asBukkit().playSound(sound, x, y, z);
    }

    @Override
    default void stopSound(@NotNull SoundStop stop) {
        if(isSilent()) return;
        asBukkit().stopSound(stop);
    }

    @Override
    default void openBook(@NotNull Book book) {
        if(isSilent()) return;
        asBukkit().openBook(book);
    }
}