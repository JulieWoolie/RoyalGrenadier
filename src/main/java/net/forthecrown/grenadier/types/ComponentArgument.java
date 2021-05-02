package net.forthecrown.grenadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.types.ComponentArgumentImpl;
import net.kyori.adventure.text.Component;

/**
 * Represents an argument type which will be parsed into a {@link Component}
 */
public interface ComponentArgument extends ArgumentType<Component> {
    static ComponentArgument component(){
        return ComponentArgumentImpl.INSTANCE;
    }

    static Component getComponent(CommandContext<CommandSource> c, String argument){
        return c.getArgument(argument, Component.class);
    }
}
