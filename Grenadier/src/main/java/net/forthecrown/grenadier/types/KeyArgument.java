package net.forthecrown.grenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.types.KeyArgumentImpl;
import net.kyori.adventure.key.Namespaced;
import org.bukkit.NamespacedKey;

/**
 * An argument type which parse a namespaced key
 */
public interface KeyArgument extends ArgumentType<NamespacedKey> {

    /**
     * A key parser which will use "minecraft" as its default namespace
     * @return A minecraft default key parser
     */
    static KeyArgument minecraft() {
        return KeyArgumentImpl.MINECRAFT_INSTANCE;
    }

    /**
     * A key parser which will use the given namespace as default
     * @param defaultNamespace The namespace to default to in case the input doesn't include a namespace
     * @return The key argument
     */
    static KeyArgument key(String defaultNamespace) {
        return new KeyArgumentImpl(defaultNamespace);
    }

    static KeyArgument key(Namespaced namespaced) {
        return key(namespaced.namespace());
    }

    static NamespacedKey getKey(CommandContext<CommandSource> c, String argument) {
        return c.getArgument(argument, NamespacedKey.class);
    }

    /**
     * Gets the default namespace used by this instance of the argument
     * @return The argument's default namespace
     */
    String getDefaultNamespace();

    @Override
    NamespacedKey parse(StringReader reader) throws CommandSyntaxException;
}
