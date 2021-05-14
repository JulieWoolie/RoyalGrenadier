package net.forthecrown.grenadier;

import com.mojang.brigadier.arguments.ArgumentType;
import net.forthecrown.royalgrenadier.RoyalArgumentsImpl;

public interface RoyalArguments {

    /**
     * Registers an argument type
     * <p>Since registering completely custom ArgumentTypes is impossible, we do it by
     * mapping custom ones to already existing vanilla ones
     * </p>
     * @param type The class of the type
     * @param vanillaType The vanilla type to represent the argument
     * @param <T>
     * @param <V>
     */
    static <T extends ArgumentType<V>, V> void register(Class<T> type, VanillaArgumentType vanillaType){
        RoyalArgumentsImpl.register(type, vanillaType.getSupplier());
    }

    /**
     * Checks if the given ArgumentType class is registered into the registry
     * @param clazz The class to check
     * @param <T>
     * @param <V>
     * @return
     */
    static <T extends ArgumentType<V>, V> boolean isRegistered(Class<T> clazz){
        return RoyalArgumentsImpl.isRegistered(clazz);
    }
}
