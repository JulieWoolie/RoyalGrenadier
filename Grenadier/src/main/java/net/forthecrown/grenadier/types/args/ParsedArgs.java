package net.forthecrown.grenadier.types.args;

/**
 * Parsed arguments
 */
public interface ParsedArgs {
    /**
     * Checks if the given argument was parsed
     * @param argument The argument to check
     * @return True, if the given argument was parsed
     */
    default boolean has(Argument argument) {
        return getOrDefault(argument, null) != null;
    }

    /**
     * Gets the given argument
     * @param argument The argument to get
     * @param <T> The type of the argument
     * @return The parsed value of the argument, or the argument's default value
     */
    default <T> T get(Argument<T> argument) {
        return getOrDefault(argument, argument.getDefaultValue());
    }

    /**
     * Checks if this is empty
     * @return True, if there were no parsed arguments
     */
    default boolean isEmpty() {
        return size() <= 0;
    }

    /**
     * Gets the value of the argument or
     * the given default value
     *
     * @param argument The argument to get the value of
     * @param def the default value to get
     * @param <T> The type of the argument
     * @return The gotten value
     */
    <T> T getOrDefault(Argument<T> argument, T def);

    /**
     * Gets the amount of parsed argument
     * @return The parsed argument size
     */
    int size();
}