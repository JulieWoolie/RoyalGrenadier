package net.forthecrown.royalgrenadier.arguments;

import com.mojang.brigadier.arguments.ArgumentType;

import java.util.function.Supplier;

public interface SimpleGrenadierConverter<T extends ArgumentType<V>, V> extends GrenadierConverter<T, V>, Supplier<ArgumentType<?>> {
    @Override
    default ArgumentType<?> toNms(T grenadier) {
        return get();
    }

    ArgumentType<?> get();
}
