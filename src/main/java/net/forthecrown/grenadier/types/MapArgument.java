package net.forthecrown.grenadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import net.forthecrown.royalgrenadier.types.MapArgumentImpl;

import java.util.Map;
import java.util.function.Supplier;

public interface MapArgument<T> extends ArgumentType<T> {

    /**
     * Returns a MapArgument for the given map
     * @param map The map to parse
     * @param <E> The type the map has
     * @return An ArgumentType for the given map
     */
    static <E> MapArgument<E> of(Map<String, E> map){
        return of(() -> map);
    }

    /**
     * Returns a MapArgument for the given map supplier
     * <p>Suppliers can be used to keep the map dynamic, instead of it being static</p>
     * @param supplier The supplier of the map
     * @param <E> The map's type
     * @return An ArgumentType for the given map and it's supplier
     */
    static <E> MapArgument<E> of(MapSupplier<E> supplier){
        return new MapArgumentImpl<>(supplier);
    }

    /**
     * A simple interface that supplies maps
     * @see java.util.function.Supplier
     * @param <T> The map's type
     */
    interface MapSupplier<T> extends Supplier<Map<String, T>>{}
}
