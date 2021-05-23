package net.forthecrown.grenadier.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.forthecrown.grenadier.CommandSource;
import org.bukkit.permissions.Permission;

public class BrigadierCommand extends LiteralArgumentBuilder<CommandSource> {

    private final AbstractCommand command;

    BrigadierCommand(String literal, AbstractCommand command) {
        super(literal);
        this.command = command;
    }

    /**
     * Sets the aliases the command will have
     * @param s The command's aliases
     * @return Self
     */
    public BrigadierCommand withAliases(String... s){
        command.setAliases(s);
        return this;
    }

    /**
     * Sets the permission the command will use
     * @param permission The command's permission
     * @return Self
     */
    public BrigadierCommand withPermission(String permission){
        command.setPermission(permission);
        return this;
    }

    /**
     * Sets the permission the command will use
     * @param permission The command's permission
     * @return Self
     */
    public BrigadierCommand withPermission(Permission permission){
        command.setPermission(permission);
        return this;
    }

    /**
     * Sets the command's permission message
     * @param permissionMessage The command's permission message
     * @return Self
     */
    public BrigadierCommand withPermissionMessage(String permissionMessage){
        command.setPermissionMessage(permissionMessage);
        return this;
    }

    /**
     * Sets the command's description
     * @param description The command's description
     * @return Self
     */
    public BrigadierCommand withDescription(String description){
        command.setDescription(description);
        return this;
    }
}
