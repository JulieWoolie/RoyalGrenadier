package net.forthecrown.grenadier.types.args;

import com.mojang.brigadier.arguments.ArgumentType;
import net.forthecrown.royalgrenadier.types.args.ArgsArgumentImpl;

import java.util.Set;

/**
 * An argument type which parses a list of argument into a {@link ParsedArgs}
 * object.
 * <p>
 * An example of valid input for this argument type is:
 * "integerArg:156 floatArg:1.547 worldArg: world_the_end"
 */
public interface ArgsArgument extends ArgumentType<ParsedArgs> {
    char
        COLON_SEPARATOR  = ':',
        EQUALS_SEPARATOR = '=';

    /**
     * Gets the char this instance uses to separate
     * labels from values
     * @return The separator char
     */
    char getSeparator();

    /**
     * Gets an argument by its name
     * @param name The name of the argument
     * @return The gotten argument
     */
    Argument getArg(String name);

    /**
     * Gets all argument names
     * @return The argument names
     */
    Set<String> getKeys();

    /**
     * Creates an {@link ArgsArgument} builder
     * @return The created builder
     */
    static Builder builder() {
        return new ArgsArgumentImpl.BuilderImpl();
    }

    /**
     * {@link ArgsArgument} builder
     */
    interface Builder {
        /**
         * Adds a required argument
         * @param argument The argument to add
         * @param <T> The type of the argument
         * @return This builder
         */
        default <T> Builder addRequired(Argument<T> argument) {
            return add(argument, true);
        }

        /**
         * Adds an optional argument
         * @param argument The argument to add
         * @param <T> The type of the argument
         * @return This builder
         */
        default <T> Builder addOptional(Argument<T> argument) {
            return add(argument, false);
        }

        /**
         * Adds an argument
         * @param argument The argument to add
         * @param required True, if this argument has to be inputted, false otherwise
         * @param <T> The type of the argument
         * @return This builder
         */
        <T> Builder add(Argument<T> argument, boolean required);

        /**
         * Sets the key-value separator to use
         * @param separator The separator to use
         * @return This builder
         */
        Builder setSeparator(char separator);

        /**
         * Gets the char used to separate
         * labels from values
         * @return The separator char
         */
        char getSeparator();

        /**
         * Builds the args argument type
         * @return The built argument type
         */
        ArgsArgument build();
    }
}