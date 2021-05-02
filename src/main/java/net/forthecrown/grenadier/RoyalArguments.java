package net.forthecrown.grenadier;

import com.mojang.brigadier.arguments.ArgumentType;
import net.forthecrown.royalgrenadier.RoyalArgumentRegistry;

public interface RoyalArguments {
    static <T extends ArgumentType<V>, V> void register(Class<T> type, VanillaArgumentType vanillaType){
        RoyalArgumentRegistry.register(type, vanillaType.getSupplier());
    }

    static <T extends ArgumentType<V>, V> boolean isRegistered(Class<T> clazz){
        return RoyalArgumentRegistry.isRegistered(clazz);
    }
}
