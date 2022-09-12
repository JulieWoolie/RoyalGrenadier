package net.forthecrown.grenadier.types.args;

import com.mojang.brigadier.arguments.ArgumentType;
import net.forthecrown.royalgrenadier.types.args.ArgsArgumentImpl;
import net.kyori.adventure.util.TriState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
     * Determines whether this argument requires/allows
     * brackets like "[" or "{".
     * <p>
     * If this returns {@link TriState#TRUE} then the
     * arguments parser will require any given input
     * to have brackets and will throw an exception if
     * they are not present.
     * <p>
     * If this returns {@link TriState#FALSE} then
     * the arguments parser will require any given
     * input to NOT have brackets.
     * <p>
     * Else, the parser won't care if there's brackets
     * or not, it'll attempt to parse all given input
     * until it reaches the end of said input.
     * <p>
     * That's what brackets allow, to possibly have
     * multiple {@link ArgsArgument} inside one another,
     * or to simply have extra arguments after the
     * args argument
     *
     * @return The bracket state of this argument
     */
    @Nonnull
    TriState bracketsForced();

    /**
     * Gets an argument by its name
     * @param name The name of the argument
     * @return The gotten argument
     */
    Argument getArg(String name);

    /**
     * Gets all argument labels.
     * <p>
     * This method will return a set that also
     * contains every alias of every argument
     * in this args instance.
     *
     * @return The argument labels
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
         * Sets if this args argument allows brackets before and after
         * the arguments.
         *
         * @param state True if required, false for forbidden, null if it
         *              doesn't matter
         * @return This builder
         * @see ArgsArgument#bracketsForced()
         */
        Builder bracketsForced(@Nullable TriState state);

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
         * Builds the args argument type
         * @return The built argument type
         */
        ArgsArgument build();
    }
}