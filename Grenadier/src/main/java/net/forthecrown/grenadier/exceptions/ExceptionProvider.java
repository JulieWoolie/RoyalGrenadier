package net.forthecrown.grenadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class ExceptionProvider implements CommandExceptionType {
    private ExceptionProvider() {}
    
    private static final ExceptionProvider INSTANCE = new ExceptionProvider();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder()
            .extractUrls()
            .hexColors()
            .build();

    public RoyalCommandException _create(Component text) {
        return new RoyalCommandException(this, text);
    }

    public RoyalCommandException _createWithContext(Component text, ImmutableStringReader reader) {
        return new RoyalCommandException(this, text, reader.getString(), reader.getCursor());
    }

    public static RoyalCommandException create(Component msg) {
        return INSTANCE._create(msg);
    }

    public RoyalCommandException _createWithContext(String msg, ImmutableStringReader reader) {
        return new RoyalCommandException(this, legacySerializer.deserialize(msg), reader.getString(), reader.getCursor());
    }

    public RoyalCommandException _create(String msg) {
        return new RoyalCommandException(this, legacySerializer.deserialize(msg));
    }

    public static RoyalCommandException createWithContext(Component msg, ImmutableStringReader reader) {
        return INSTANCE._createWithContext(msg, reader);
    }
    
    public static RoyalCommandException translatable(String key, ComponentLike... args){
        return create(Component.translatable(key, args));
    }

    public static RoyalCommandException translatable(String key, TextColor color, ComponentLike... args){
        return create(Component.translatable(key, color, args));
    }

    public static RoyalCommandException translatableWithContext(String key, ImmutableStringReader context, ComponentLike... args){
        return createWithContext(Component.translatable(key, args), context);
    }

    public static RoyalCommandException translatableWithContext(String key, TextColor color, ImmutableStringReader context, ComponentLike... args){
        return createWithContext(Component.translatable(key, color, args), context);
    }

    public static RoyalCommandException create(String msg, Object... args) {
        return INSTANCE._create(String.format(msg, args));
    }

    public static RoyalCommandException createWithContext(String msg, ImmutableStringReader reader, Object... args) {
        return INSTANCE._createWithContext(String.format(msg, args), reader);
    }
}