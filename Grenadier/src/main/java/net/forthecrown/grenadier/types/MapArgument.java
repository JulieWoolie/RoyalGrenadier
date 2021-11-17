package net.forthecrown.grenadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import net.forthecrown.royalgrenadier.types.MapArgumentImpl;

import java.util.Map;

public interface MapArgument<T> extends ArgumentType<T> {

    /**
     * Returns a MapArgument for the given map
     * @param map The map to parse
     * @param <E> The type the map has
     * @return An ArgumentType for the given map
     */
    static <E> MapArgument<E> of(Map<String, E> map){
        return new MapArgumentImpl<>(map);
    }
}
