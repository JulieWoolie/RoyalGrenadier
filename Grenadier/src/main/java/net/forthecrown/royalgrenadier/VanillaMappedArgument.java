package net.forthecrown.royalgrenadier;

import com.mojang.brigadier.arguments.ArgumentType;

public interface VanillaMappedArgument {
    ArgumentType<?> getVanillaArgumentType();

    default boolean useVanillaSuggestions() {
        return false;
    }
}