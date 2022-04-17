package net.forthecrown.grenadier.command;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.forthecrown.grenadier.CmdUtil;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.royalgrenadier.RoyalGrenadier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;

/**
 * The abstract class used to create and register Grenadier commands
 */
public abstract class AbstractCommand extends CmdUtil implements Predicate<CommandSource> {

    private final String name;
    private final BrigadierCommand root;
    private final Plugin plugin;

    protected String[] aliases;
    protected Permission permission;
    protected Component permissionMessage;
    protected String description;
    protected boolean showUsageOnFail;

    // Will be null until command is registered
    protected LiteralCommandNode<CommandSource> built;
    private boolean registered = false;

    /**
     * Constructs the command with the given label for the given plugin
     * @param name The name and label of the command
     * @param plugin The plugin creating the command
     */
    protected AbstractCommand(@NotNull String name, @NotNull Plugin plugin){
        this.name = name.toLowerCase();
        this.root = new BrigadierCommand(this.name, this);
        this.plugin = plugin;
    }

    protected AbstractCommand(@NotNull String name) {
        this(name, RoyalGrenadier.getPlugin());
    }

    protected abstract void createCommand(BrigadierCommand command);

    /**
     * Registers the command and makes it usable ingame
     */
    public final void register(){
        if(registered) return;
        root.requires(this);
        createCommand(root);

        built = RoyalGrenadier.register(this);
        registered = true;
    }

    /**
     * Gets whether the command has been registered
     * @return If the command is registered or not
     */
    public final boolean isRegistered(){
        return registered;
    }

    /**
     * Utility method for creating custom suggestions quickly
     * @param strings The string to suggest
     * @return The suggestion provider of the inputted strings
     */
    protected SuggestionProvider<CommandSource> suggestMatching(String... strings){
        return suggestMatching(Arrays.asList(strings));
    }

    /**
     * Utility method for creating custom suggestions
     * @param strings The strings to suggest
     * @return The SuggestionProvider of the inputted string collection
     */
    protected SuggestionProvider<CommandSource> suggestMatching(Collection<String> strings){
        return (c, b) -> CompletionProvider.suggestMatching(b, strings);
    }

    /**
     * Utility method for creating suggestions with tooltips
     * @param suggestions The map of suggestions
     * @return The suggestion provider for the inputted map
     */
    protected SuggestionProvider<CommandSource> suggestMatching(Map<String, String> suggestions){
        return (c, b) -> CompletionProvider.suggestMatching(b, suggestions);
    }

    /**
     * Tests if the specified source is allowed to
     * use the command
     * @param source The source to check
     * @return Whether they are allowed to use the command
     */
    @Override
    public boolean test(CommandSource source) {
        return testPermissionSilent(source.asBukkit());
    }

    /**
     * Tests if the specified sender has the permission to use this command
     * @param source The sender to check
     * @return Whether they have permission for this command
     */
    public boolean testPermissionSilent(CommandSender source){
        if(getPermission() == null) return true;
        return source.hasPermission(getPermission());
    }

    /**
     * Returns the plugin that created this command
     * @return Plugin that created the command
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Gets all the aliases for the command
     * @return The command's aliases
     */
    public @Nullable String[] getAliases() {
        return aliases;
    }

    /**
     * Sets the command's aliases
     * @param newAliases Aliases
     */
    public void setAliases(String... newAliases) {
        if(registered) return;
        this.aliases = newAliases;

        // Ensure all given aliases are lower case
        if(aliases != null && aliases.length > 0) {
            for (int i = 0; i < aliases.length; i++) {
                aliases[i] = Validate.notNull(aliases[i]).toLowerCase();
            }
        }
    }

    /**
     * Gets the permission needed to use this command
     * @return The command's permission
     */
    public @Nullable String getPerm() {
        return permission.getName();
    }

    /**
     * Gets the permission needed to use this command
     * @return The command's permission
     */
    public Permission getPermission(){
        return permission;
    }

    /**
     * Sets the command's permission
     * @param permission Permission needed to use the command
     */
    public void setPermission(String permission) {
        if(registered) return;

        if(permission == null || permission.isBlank()){
            this.permission = null;
            return;
        }

        PluginManager pm = Bukkit.getPluginManager();
        if(pm.getPermission(permission) == null) pm.addPermission(this.permission = new Permission(permission));
        else this.permission = pm.getPermission(permission);
    }

    /**
     * Sets the command's permission
     * @param permission Permission needed to use the command
     */
    public void setPermission(Permission permission) {
        if(registered) return;
        this.permission = permission;
    }

    /**
     * Gets the message the command will show to senders who
     * don't have permission for this command
     * @return The permission message
     */
    public @Nullable String getPermissionMessage() {
        if(permissionMessage == null) return null;
        return LegacyComponentSerializer.legacySection().serialize(permissionMessage);
    }

    /**
     * Sets the permission message this command will use
     * @param permissionMessage The permission message
     */
    public void setPermissionMessage(String permissionMessage) {
        if(registered) return;
        if(permissionMessage == null || permissionMessage.isBlank()) this.permissionMessage = null;
        else this.permissionMessage = LegacyComponentSerializer.legacySection().deserialize(permissionMessage);
    }

    /**
     * Gets the message the command will show to senders who
     * don't have permission for this command
     * @return The permission message
     */
    public Component permissionMessage() {
        return permissionMessage;
    }

    /**
     * Sets the permission message this command will use
     * @param permissionMessage The permission message
     */
    public void permissionMessage(Component permissionMessage) {
        this.permissionMessage = permissionMessage;
    }

    /**
     * Gets the name of the command
     * @return The command's name and label
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the command
     * @return The command's description
     */
    public @Nullable String getDescription() {
        return description;
    }

    /**
     * Sets the description for the command
     * @param description The command's new description
     */
    public void setDescription(String description) {
        if(registered) return;
        this.description = description;
    }

    /**
     * Gets the argument builder for this command
     * @return The command's argument builder
     */
    public BrigadierCommand getCommand() {
        return root;
    }

    public Component getUsage(CommandSource source) {
        Iterator<String> iterator = RoyalGrenadier.getDispatcher().getSmartUsage(built, source).values().iterator();

        String prefix = "/" + getName() + " ";
        StringBuilder builder = new StringBuilder(prefix);

        while (iterator.hasNext()) {
            String s = iterator.next();

            builder.append(s);

            if(iterator.hasNext()) {
                builder
                        .append('\n')
                        .append(prefix);
            }
        }

        return Component.text(builder.toString());
    }

    public boolean getShowUsageOnFail() {
        return showUsageOnFail;
    }

    public void setShowUsageOnFail(boolean showUsageOnFail) {
        this.showUsageOnFail = showUsageOnFail;
    }
}