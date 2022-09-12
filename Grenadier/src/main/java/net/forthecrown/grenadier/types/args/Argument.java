package net.forthecrown.grenadier.types.args;

import com.mojang.brigadier.arguments.ArgumentType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.forthecrown.grenadier.CommandSource;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.permissions.Permission;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * A single argument that can be parsed by {@link ArgsArgument}
 * into a {@link ParsedArgs} instance.
 * @param <T> The argument's type
 */
@Getter
@EqualsAndHashCode
public class Argument<T> {
    /**
     * Valid name pattern.
     * If the name contained either '[', ']', '{', '}', ':' or '='
     * it could break the parser
     */
    public static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_./-]+");

    /**
     * Default predicate each argument uses to test sources
     */
    private static final Predicate<CommandSource> DEFAULT_REQUIRES = source -> true;

    /**
     * The argument's name
     */
    @EqualsAndHashCode.Include
    private final String name;

    /**
     * The argument type, used for parsing and suggestions
     */
    private final ArgumentType<T> parser;

    /**
     * The argument's default value
     */
    private final T defaultValue;

    /**
     * The argument's aliases, empty if no aliases
     * are given
     */
    private final String[] aliases;

    /**
     * The test command sources must pass to be able to access
     * this argument
     */
    private final Predicate<CommandSource> requires;

    public Argument(String name,
                    ArgumentType<T> parser,
                    T defaultValue,
                    Predicate<CommandSource> requires,
                    String... aliases
    ) {
        // Validate that the name and aliases
        // all match the name pattern
        validateLabel(name);

        for (var s: aliases) {
            validateLabel(s);
        }

        // Set fields
        this.name = name;
        this.parser = parser;
        this.defaultValue = defaultValue;
        this.aliases = aliases;
        this.requires = Objects.requireNonNullElse(requires, DEFAULT_REQUIRES);
    }

    /**
     * Validates that the given label matches the {@link #NAME_PATTERN}
     * pattern.
     * @param label The label to test
     * @throws IllegalArgumentException If the label failed the validation
     */
    private static void validateLabel(String label) throws IllegalArgumentException {
        Validate.isTrue(NAME_PATTERN.matcher(label).matches(),
                "Invalid label: '%s'", label
        );
    }

    /**
     * Creates an argument
     * @param name The name of the argument
     * @param type The argument's parser and suggestion provider
     * @param defaultValue The default value of the argument, may be null
     * @param requires The test command sources must pass to get the value
     *                of this argument
     * @param aliases Aliases by which this argument can be referred to
     * @param <T> The argument's type
     * @return The created argument
     */
    public static <T> Argument<T> of(String name, ArgumentType<T> type,
                                     T defaultValue,
                                     Predicate<CommandSource> requires,
                                     String... aliases
    ) {
        return builder(name, type)
                .setDefaultValue(defaultValue)
                .setRequires(requires)
                .setAliases(aliases)
                .build();
    }

    /**
     * Creates an argument
     * @param name The name of the argument
     * @param type The argument's type
     * @param defaultValue The default value of the argument
     * @param aliases Any optional aliases the argument can be
     *                parsed with
     * @param <T> The type the argument parses
     * @return The created argument
     */
    public static <T> Argument<T> of(String name, ArgumentType<T> type, T defaultValue, String... aliases) {
        return of(name, type, defaultValue, null, aliases);
    }

    /**
     * Creates an argument with a null default value
     * @param name The name of the argument
     * @param type The argument's type
     * @param aliases Any optional aliases the argument can be
     *        parsed with
     * @param <T> The type the argument parses
     * @return The created argument
     */
    public static <T> Argument<T> of(String name, ArgumentType<T> type, String... aliases) {
        return of(name, type, null, null, aliases);
    }

    /**
     * Creates an argument builder
     * @param name The name of the argument
     * @param type The argument's parser and suggestion provider
     * @param <T> The argument's type
     * @return The created builder
     */
    public static <T> Builder<T> builder(String name, ArgumentType<T> type) {
        return new Builder<>(name, type);
    }

    /**
     * Argument builder
     * @param <T> The builder's type
     */
    @RequiredArgsConstructor
    public static class Builder<T> {
        private final String name;
        private final ArgumentType<T> type;
        private T defaultValue;
        private Predicate<CommandSource> requires = null;
        private String[] aliases = ArrayUtils.EMPTY_STRING_ARRAY;

        /**
         * Sets the default value the resulting argument
         * will use
         * @param defaultValue The new default value
         * @return This
         */
        public Builder<T> setDefaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        /**
         * Sets the predicate that's used by the resulting
         * argument
         * @param requires The command source test
         * @return This
         */
        public Builder<T> setRequires(Predicate<CommandSource> requires) {
            this.requires = requires;
            return this;
        }

        /**
         * Sets the permissions needed to use the resulting
         * argument.
         * <p>
         * Delegate method for {@link #setRequires(Predicate)}
         * @param permission The permissions to use
         * @return This
         * @see #setRequires(Predicate)
         */
        public Builder<T> setPermission(String permission) {
            return setRequires(source -> source.hasPermission(permission));
        }

        /**
         * Sets the permissions needed to use the resulting
         * argument.
         * <p>
         * Delegate method for {@link #setRequires(Predicate)}
         * @param permission The permissions to use
         * @return This
         * @see #setRequires(Predicate)
         */
        public Builder<T> setPermission(Permission permission) {
            return setPermission(permission.getName());
        }

        /**
         * Sets all the aliases of this argument
         * @param aliases The new aliases
         * @return This
         */
        public Builder<T> setAliases(String... aliases) {
            this.aliases = Validate.noNullElements(aliases);
            return this;
        }

        /**
         * Creates the argument
         * @return The created argument
         */
        public Argument<T> build() {
            return new Argument<>(name, type, defaultValue, requires, aliases);
        }
    }
}