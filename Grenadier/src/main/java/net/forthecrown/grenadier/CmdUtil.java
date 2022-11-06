package net.forthecrown.grenadier;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class that {@link net.forthecrown.grenadier.command.AbstractCommand} extends for
 * ease of use of the utility methods here.
 */
public class CmdUtil {
    /**
     * Utility method for creating a literal argument
     * @param name The literal string
     * @return A Literal argument
     */
    public static LiteralArgumentBuilder<CommandSource> literal(String name){
        return LiteralArgumentBuilder.literal(name);
    }

    /**
     * Utility method for creating a required argument
     * @param name The name of the argument
     * @param type The ArgumentType for the type
     * @param <T> The type
     * @return A required argument for the type
     */
    public static <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, ArgumentType<T> type){
        return RequiredArgumentBuilder.argument(name, type);
    }

    /**
     * Turns a component into a brigadier tooltip message
     * @param component The tooltip component
     * @return The tooltip message
     * @see GrenadierUtils#toVanilla(Component) 
     */
    public static @Nullable Message toTooltip(@Nullable Component component) {
        return GrenadierUtils.toVanilla(component);
    }

    /**
     * Creates a tooltip message from the given
     * string message
     * @param message The string message to use
     * @return The created string message
     */
    public static @NotNull Message toTooltip(@NotNull String message) {
        return new LiteralMessage(message);
    }
}