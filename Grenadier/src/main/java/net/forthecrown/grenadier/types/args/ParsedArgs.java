package net.forthecrown.grenadier.types.args;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.forthecrown.grenadier.CommandSource;

/**
 * Parsed arguments
 */
public interface ParsedArgs {
    /**
     * The Exception type used if the a {@link CommandSource} object fails a
     * {@link Argument#getRequires()} test
     */
    DynamicCommandExceptionType CANNOT_USE_ARG = new DynamicCommandExceptionType(o ->
            () -> String.format("Cannot use argument: '%s'", o)
    );

    /**
     * Gets the value of the given argument.
     * <p>
     * This is the same as calling {@link #getOrDefault(Argument, Object)}
     * with the {@link Argument#getDefaultValue()}.
     * @param argument The argument to get the value of
     * @param <T> The argument's type
     * @return The gotten argument's value.
     */
    default <T> T get(Argument<T> argument) {
        return getOrDefault(argument, argument.getDefaultValue());
    }

    /**
     * Gets the value of the given argument.
     * <p>
     * This is the same as calling {@link #getOrDefault(Argument, Object, CommandSource)}
     * with the {@link Argument#getDefaultValue()}.
     * @param argument The argument to get the value of
     * @param source The source accessing the value
     * @param <T> The argument's type
     *
     * @return The gotten argument's value
     *
     * @throws CommandSyntaxException If the argument was present, but the
     *                                source failed the {@link #testArgument(Argument, CommandSource)}
     *                                validation test.
     */
    default <T> T get(Argument<T> argument, CommandSource source) throws CommandSyntaxException {
        return getOrDefault(argument, argument.getDefaultValue(), source);
    }

    /**
     * Gets the argument by the given name
     * and type.
     * <p>
     * This is the same as calling {@link #getOrDefault(String, Class, Object)}
     * with a null default value.
     * @param name The name of the argument
     * @param type The argument type's class
     * @param <T> The argument's type
     * @return The gotten value.
     */
    default <T> T get(String name, Class<T> type) {
        return getOrDefault(name, type, null);
    }

    /**
     * Gets the argument by the given name
     * and type.
     * <p>
     * This is the same as calling {@link #getOrDefault(String, Class, Object, CommandSource)}
     * with a null default value.
     * @param name The name of the argument
     * @param type The argument type's class
     * @param source The source accessing the value
     * @param <T> The argument's type
     *
     * @return The gotten value.
     *
     * @throws CommandSyntaxException If the argument was present, but the
     *                                source failed the {@link #testArgument(Argument, CommandSource)}
     *                                validation test.
     */
    default <T> T get(String name, Class<T> type, CommandSource source) throws CommandSyntaxException {
        return getOrDefault(name, type, null, source);
    }

    /**
     * Gets the value, or default fallback value,
     * of the given argument
     * @param argument The argument to get the value of
     * @param def The default value to fall back to
     * @param <T> The argument's type
     * @return The argument's value, or the default value, if
     *         the argument was not present.
     */
    <T> T getOrDefault(Argument<T> argument, T def);

    /**
     * Gets the value of a given argument, or the default value
     * @param argument The argument to get the value of
     * @param def The default value to fall back to
     * @param source The source accessing the argument
     * @param <T> The argument's type
     *
     * @return The argument's value, or the default value, if
     *         the argument was not present.
     *
     * @throws CommandSyntaxException If the argument was present, but the
     *                                source failed the {@link #testArgument(Argument, CommandSource)}
     *                                validation test.
     */
    default  <T> T getOrDefault(Argument<T> argument, T def, CommandSource source) throws CommandSyntaxException {
        testArgument(argument, source);
        return getOrDefault(argument, def);
    }

    /**
     * Gets or defaults the argument by the given name
     * and type.
     * @param name The name of the argument
     * @param type The argument type's class
     * @param <T> The argument's type
     * @return The gotten argument, or the default value
     */
    <T> T getOrDefault(String name, Class<T> type, T def);

    /**
     * Gets or defaults the argument by the given name
     * and type.
     * @param name The name of the argument
     * @param type The argument type's class
     * @param def The default value to return
     * @param source The source accessing the argument
     * @param <T> The argument's type
     * @return The gotten argument, or the default value
     * @throws CommandSyntaxException If the argument was present, but the
     *                                source failed the {@link #testArgument(Argument, CommandSource)}
     *                                validation test.
     */
    <T> T getOrDefault(String name, Class<T> type, T def, CommandSource source) throws CommandSyntaxException;

    /**
     * Tests a source against the {@link Argument#getRequires()} predicate.
     * <p>
     * If the given argument is not present in this parse result, then
     * nothing happens, if it is and the given source fails the predicate,
     * then {@link #CANNOT_USE_ARG} is thrown.
     * @param argument The argument to test against
     * @param source The source to test
     * @throws CommandSyntaxException If the source failed the argument's predicate.
     */
    default void testArgument(Argument argument, CommandSource source) throws CommandSyntaxException {
        if (!has(argument)) {
            return;
        }

        if (!argument.getRequires().test(source)) {
            throw CANNOT_USE_ARG.create(argument.getName());
        }
    }

    /**
     * Tests if the given argument was parsed and
     * is contained in this result.
     *
     * @param argument The argument to check
     * @return True, if the given argument was parsed
     */
    default boolean has(Argument argument) {
        return getOrDefault(argument, null) != null;
    }

    /**
     * Checks if this is empty
     * @return True, if there were no parsed arguments
     */
    default boolean isEmpty() {
        return size() <= 0;
    }

    /**
     * Gets the amount of parsed argument
     * @return The parsed argument size
     */
    int size();
}