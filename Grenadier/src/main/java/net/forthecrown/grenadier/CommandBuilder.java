package net.forthecrown.grenadier;

import net.forthecrown.grenadier.command.AbstractCommand;
import net.forthecrown.grenadier.command.BrigadierCommand;
import net.forthecrown.royalgrenadier.RoyalGrenadier;
import net.kyori.adventure.text.Component;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Don't want to extend a command class to create commands?
 * Here you go! :D
 * <p></p>
 * This allows for commands to be built without there needing to
 * be a class that extends AbstractCommand.
 * <p></p>
 * Use the {@link CommandCreator} interface and {@link CommandBuilder#setCommandLogic(CommandCreator)}
 * to create the command's logic.
 */
public class CommandBuilder {
    private CommandCreator creator;
    private final BuilderCommand command;

    /**
     * Creates a command builder with the given name and plugin
     * @param name The name of the command
     * @param plugin The plugin creating the command
     */
    public CommandBuilder(String name, Plugin plugin) {
        this.command = new BuilderCommand(name, plugin, this);

        this.creator = CommandCreator.EMPTY;
    }

    public CommandBuilder(String name) {
        this(name, RoyalGrenadier.getPlugin());
    }

    /**
     * Gets the command's name
     * @return The command's name
     */
    public String getName() {
        return command.getName();
    }

    /**
     * Gets the plugin that owns this command
     * @return The command's plugin
     */
    public Plugin getPlugin() {
        return command.getPlugin();
    }

    /**
     * Registers this command
     */
    public void register() {
        command.register();
    }

    /**
     * Checks if the command is already registered
     * @return True, if the command is registered, false otherwise
     */
    public boolean isRegistered() {
        return command.isRegistered();
    }

    /**
     * Gets the command logic creator this command uses
     * @return Command logic creator
     */
    public CommandCreator getCreator() {
        return creator;
    }

    /**
     * Sets the command logic creator
     * @param creator The new command logic creator
     * @return Itself
     */
    public CommandBuilder setCommandLogic(CommandCreator creator) {
        this.creator = creator;
        return this;
    }

    /**
     * Sets the command's aliases
     * @param aliases The aliases
     * @return Itself
     */
    public CommandBuilder setAliases(String... aliases) {
        command.setAliases(aliases);
        return this;
    }

    /**
     * Gets the permission this command needs
     * @return The command's permission
     */
    @Nullable
    public String getPerm() {
        return command.getPerm();
    }

    /**
     * Gets the permission this command needs
     * @return The command's permission
     */
    public Permission getPermission() {
        return command.getPermission();
    }

    /**
     * sets the permissions this command will require
     * @param permission The command's permission
     * @return Itself
     */
    public CommandBuilder setPermission(String permission) {
        command.setPermission(permission);
        return this;
    }

    /**
     * sets the permissions this command will require
     * @param permission The command's permission
     * @return Itself
     */
    public CommandBuilder setPermission(Permission permission) {
        command.setPermission(permission);
        return this;
    }

    /**
     * Gets the message the command will show to senders who
     * don't have permission for this command
     * @return The permission message
     */
    @Nullable
    public String getPermissionMessage() {
        return command.getPermissionMessage();
    }

    /**
     * Sets the permission message this command will use
     * @param permissionMessage The permission message
     * @return Itself
     */
    public CommandBuilder setPermissionMessage(String permissionMessage) {
        command.setPermissionMessage(permissionMessage);
        return this;
    }

    /**
     * Gets the message the command will show to senders who
     * don't have permission for this command
     * @return The permission message
     */
    public Component permissionMessage() {
        return command.permissionMessage();
    }

    /**
     * Sets the permission message this command will use
     * @param permissionMessage The permission message
     * @return Itself
     */
    public CommandBuilder permissionMessage(Component permissionMessage) {
        command.permissionMessage(permissionMessage);
        return this;
    }

    /**
     * Gets the description of the command
     * @return The command's description
     */
    @Nullable
    public String getDescription() {
        return command.getDescription();
    }

    /**
     * Sets the description for the command
     * @param description The command's new description
     * @return Itself
     */
    public CommandBuilder setDescription(String description) {
        command.setDescription(description);
        return this;
    }

    private static class BuilderCommand extends AbstractCommand {
        private final CommandBuilder builder;

        public BuilderCommand(@NotNull String name, @NotNull Plugin plugin, CommandBuilder builder) {
            super(name, plugin);
            this.builder = builder;
        }

        @Override
        protected void createCommand(BrigadierCommand command) {
            builder.creator.createCommand(command);
        }
    }
}