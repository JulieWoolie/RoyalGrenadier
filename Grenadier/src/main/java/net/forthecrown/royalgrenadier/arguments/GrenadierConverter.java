package net.forthecrown.royalgrenadier.arguments;

import com.mojang.brigadier.arguments.ArgumentType;

public interface GrenadierConverter<T extends ArgumentType<V>, V> {
    //Converts the given Grenadier argument type to vanilla NMS
    ArgumentType<?> toNms(T grenadier);
}
