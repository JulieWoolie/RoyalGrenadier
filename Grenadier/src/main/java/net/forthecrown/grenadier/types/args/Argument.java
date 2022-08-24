package net.forthecrown.grenadier.types.args;

import com.mojang.brigadier.arguments.ArgumentType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.Validate;

import java.util.regex.Pattern;

/**
 * A single argument that can be parsed by {@link ArgsArgument}
 * into a {@link ParsedArgs} instance.
 * @param <T> The argument's type
 */
@Getter
@EqualsAndHashCode
public class Argument<T> {
    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_-]+");

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

    public Argument(String name, ArgumentType<T> parser, T defaultValue) {
        Validate.isTrue(NAME_PATTERN.matcher(name).matches(), "Invalid name: '%s'", name);

        this.name = name;
        this.parser = parser;
        this.defaultValue = defaultValue;
    }

    /**
     * Creates an argument
     * @param name The name of the argument
     * @param type The argument's type
     * @param defaultValue The default value of the argument
     * @param <T> The type the argument parses
     * @return The created argument
     */
    public static <T> Argument<T> of(String name, ArgumentType<T> type, T defaultValue) {
        return new Argument<>(name, type, defaultValue);
    }

    /**
     * Creates an argument with a null default value
     * @param name The name of the argument
     * @param type The argument's type
     * @param <T> The type the argument parses
     * @return The created argument
     */
    public static <T> Argument<T> of(String name, ArgumentType<T> type) {
        return of(name, type, null);
    }
}