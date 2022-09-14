package net.forthecrown.grenadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.forthecrown.royalgrenadier.types.RegistryArgumentImpl;
import org.bukkit.Keyed;
import org.bukkit.Registry;

/**
 * The Registry argument parses a value from a {@link Registry}
 * by parsing a key and then attempting to find a value associated
 * with that key. If no value is found, then this throws {@link #getExceptionType()}.
 * <p>
 * Because registry's don't have names, you must either provide one yourself
 * in {@link #registry(Registry, String)} or settle with the default
 * {@link #DEFAULT_NAME} name. This name is used to create the exception's
 * message. The format the name is used in is {@link #EXCEPTION_FORMAT}
 *
 * @param <T> The type this argument's underlying registry holds
 */
public interface RegistryArgument<T extends Keyed> extends ArgumentType<T> {
    /**
     * Default value given to {@link #registry(Registry, String)}
     * when calling {@link #registry(Registry)}
     */
    String DEFAULT_NAME = "value";

    /**
     * The base exception format used when calling {@link #registry(Registry, String)}.
     * <p>
     * This accepts 2 arguments, the first being the name of the registry and
     * the second being the key that had no associated value in a given
     * registry.
     */
    String EXCEPTION_FORMAT = "Unknown %s: '%s'";

    /**
     * The registry this argument type uses to
     * parse values from
     * @return The registry this argument type parses for
     */
    Registry<T> getRegistry();

    /**
     * Gets the exception type thrown when this argument
     * type finds an unknown value key.
     * @return The exception type
     */
    DynamicCommandExceptionType getExceptionType();

    /**
     * Creates a registry argument with the given registry
     * and uses the given exception type for unknown values
     * @param registry The registry
     * @param exceptionType The exception type to throw when
     *                      an unknown key is found
     * @param <T> The registry's type
     * @return The created argument
     */
    static <T extends Keyed> RegistryArgument<T> registry(Registry<T> registry, DynamicCommandExceptionType exceptionType) {
        return new RegistryArgumentImpl<>(registry, exceptionType);
    }

    /**
     * Creates a registry argument for the given
     * registry and uses the given <code>registryName</code>
     * in the exception message
     *
     * @param registry The registry
     * @param registryName The name of the registry
     * @param <T> The registry's type
     * @return The created argument
     */
    static <T extends Keyed> RegistryArgument<T> registry(Registry<T> registry, String registryName) {
        return registry(registry,
                new DynamicCommandExceptionType(o -> () -> String.format(EXCEPTION_FORMAT, registryName, o))
        );
    }

    /**
     * Creates a registry argument with the
     * given registry and uses {@link #DEFAULT_NAME}
     * as the registry's name
     * @param registry The registry
     * @param <T> The registry's type
     * @return The created argument
     */
    static <T extends Keyed> RegistryArgument<T> registry(Registry<T> registry) {
        return registry(registry, DEFAULT_NAME);
    }
}