package net.forthecrown.grenadier.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.forthecrown.grenadier.CommandSource;

public class BrigadierCommand extends LiteralArgumentBuilder<CommandSource> {
    private final AbstractCommand command;
    public BrigadierCommand(String literal, AbstractCommand command) {
        super(literal);
        this.command = command;
    }

    public BrigadierCommand withAliases(String... s){
        command.setAliases(s);
        return this;
    }

    public BrigadierCommand withPermission(String permission){
        command.setPermission(permission);
        return this;
    }

    public BrigadierCommand withDescription(String description){
        command.setDescription(description);
        return this;
    }
}
