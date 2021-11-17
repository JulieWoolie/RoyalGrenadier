package net.forthecrown.royalgrenadier.arguments;

import com.mojang.brigadier.arguments.ArgumentType;

public class RegisteredArgument<T extends ArgumentType<V>, V> {
    private final GrenadierConverter<T, V> converter;
    private final boolean nmsSuggests;

    public RegisteredArgument(GrenadierConverter<T, V> converter, boolean nmsSuggests) {
        this.converter = converter;
        this.nmsSuggests = nmsSuggests;
    }

    public ArgumentType<?> convert(ArgumentType<V> type) {
        return converter.toNms((T) type);
    }

    public GrenadierConverter<? extends ArgumentType<?>, ?> getConverter() {
        return converter;
    }

    public boolean nmsSuggests() {
        return nmsSuggests;
    }
}
