package net.forthecrown.grenadier.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.forthecrown.grenadier.CommandSource;
import org.bukkit.permissions.Permission;

import java.util.function.Predicate;

public class BrigadierCommand extends LiteralArgumentBuilder<CommandSource> {

    private final AbstractCommand abstractCommand;

    BrigadierCommand(String literal, AbstractCommand abstractCommand) {
        super(literal);
        this.abstractCommand = abstractCommand;
    }

    @Override
    protected BrigadierCommand getThis() {
        return this;
    }

    @Override
    public BrigadierCommand then(CommandNode<CommandSource> argument) {
        super.then(argument);
        return this;
    }

    @Override
    public BrigadierCommand then(ArgumentBuilder<CommandSource, ?> argument) {
        super.then(argument);
        return this;
    }

    @Override
    public BrigadierCommand executes(Command<CommandSource> command) {
        super.executes(command);
        return this;
    }

    @Override
    public BrigadierCommand requires(Predicate<CommandSource> requirement) {
        super.requires(requirement);
        return this;
    }

    /**
     * Sets the aliases the command will have
     * @param s The command's aliases
     * @return Self
     */
    public BrigadierCommand withAliases(String... s){
        abstractCommand.setAliases(s);
        return this;
    }

    /**
     * Sets the permission the command will use
     * @param permission The command's permission
     * @return Self
     */
    public BrigadierCommand withPermission(String permission){
        abstractCommand.setPermission(permission);
        return this;
    }

    /**
     * Sets the permission the command will use
     * @param permission The command's permission
     * @return Self
     */
    public BrigadierCommand withPermission(Permission permission){
        abstractCommand.setPermission(permission);
        return this;
    }

    /**
     * Sets the command's permission message
     * @param permissionMessage The command's permission message
     * @return Self
     */
    public BrigadierCommand withPermissionMessage(String permissionMessage){
        abstractCommand.setPermissionMessage(permissionMessage);
        return this;
    }

    /**
     * Sets the command's description
     * @param description The command's description
     * @return Self
     */
    public BrigadierCommand withDescription(String description){
        abstractCommand.setDescription(description);
        return this;
    }

    public AbstractCommand getAbstractCommand() {
        return abstractCommand;
    }
}