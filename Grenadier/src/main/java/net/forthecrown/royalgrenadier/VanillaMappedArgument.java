package net.forthecrown.royalgrenadier;

import com.mojang.brigadier.arguments.ArgumentType;
import net.forthecrown.royalgrenadier.command.WrapperTranslator;

/**
 * An interface for argument types to implement if they wish
 * to be mapped to an already existing argument type.
 * <p>
 * If a custom argument type does not implement this class,
 * then the vanilla score holder argument will be used instead.
 * This is because, I, Julie, believe that the ScoreHolder
 * argument covers the general basis of parsing needed and
 * it keeps the vanilla chat color thing correct for the
 * most part as well.
 * <p>
 * Please see {@link WrapperTranslator} for
 * more info on how grenadier commands are mapped to vanilla ones
 *
 * @see WrapperTranslator
 */
public interface VanillaMappedArgument {
    /**
     * Gets the vanilla argument type this type is mapped to.
     * @return The vanilla-registered argument type.
     */
    ArgumentType<?> getVanillaArgumentType();

    /**
     * Determines if the argument node this argument is attached
     * to should use the {@link #getVanillaArgumentType()} for
     * suggestions or this for suggestions.
     * <p>
     * This is, by default, false
     * @return True, to use vanilla for suggestions, false otherwise.
     */
    default boolean useVanillaSuggestions() {
        return false;
    }
}