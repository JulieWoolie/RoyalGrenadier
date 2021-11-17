package net.forthecrown.grenadier.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.royalgrenadier.RoyalGrenadier;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

/**
 * The abstract class used to create and register Grenadier commands
 */
public abstract class AbstractCommand implements Predicate<CommandSource> {

    private final String name;
    private final BrigadierCommand root;
    private final Plugin plugin;

    protected String[] aliases;
    protected Permission permission;
    protected String permissionMessage;
    protected String description;

    private boolean registered = false;

    /**
     * Constructs the command with the given label for the given plugin
     * @param name The name and label of the command
     * @param plugin The plugin creating the command
     */
    protected AbstractCommand(@NotNull String name, @NotNull Plugin plugin){
        this.name = name;
        this.root = new BrigadierCommand(name, this);
        this.plugin = plugin;
    }

    protected abstract void createCommand(BrigadierCommand command);

    /**
     * Registers the command and makes it usable ingame
     */
    public final void register(){
        if(registered) return;
        root.requires(this);
        createCommand(root);

        RoyalGrenadier.register(this);
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
     * Utility method for creating a literal argument
     * @param name The literal string
     * @return A Literal argument
     */
    protected LiteralArgumentBuilder<CommandSource> literal(String name){
        return LiteralArgumentBuilder.literal(name);
    }

    /**
     * Utility method for creating a required argument
     * @param name The name of the argument
     * @param type The ArgumentType for the type
     * @param <T> The type
     * @return A required argument for the type
     */
    protected <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, ArgumentType<T> type){
        return RequiredArgumentBuilder.argument(name, type);
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
     * @param aliases Aliases
     */
    public void setAliases(String... aliases) {
        if(registered) return;
        this.aliases = aliases;
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
        return permissionMessage;
    }

    public void setPermissionMessage(String permissionMessage) {
        if(registered) return;
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
}
