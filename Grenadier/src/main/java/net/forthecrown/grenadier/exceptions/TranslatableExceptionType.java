package net.forthecrown.grenadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * Represents an exception that can be translated with {@link net.kyori.adventure.text.TranslatableComponent}
 */
public class TranslatableExceptionType implements CommandExceptionType {

    /**
     * The key used to translate the exception
     */
    private final String translationKey;

    public TranslatableExceptionType(String translationKey) {
        this.translationKey = translationKey;
    }

    /**
     * Creates the exception
     * @param args Any optional args
     * @return The created exception
     */
    public RoyalCommandException create(ComponentLike... args){
        return new RoyalCommandException(this, Component.translatable(translationKey, args));
    }

    public RoyalCommandException create(TextColor color, ComponentLike... args){
        return new RoyalCommandException(this, Component.translatable(translationKey, color, args));
    }

    public RoyalCommandException create(TextColor color, TextDecoration deco, ComponentLike... args){
        return new RoyalCommandException(this, Component.translatable(translationKey, Style.style(color, deco), args));
    }

    public RoyalCommandException create(Style style, ComponentLike... args){
        return new RoyalCommandException(this, Component.translatable(translationKey, style, args));
    }

    /**
     * Creates the exception with context
     * @param args Any optional args
     * @return The created exception
     */
    public RoyalCommandException createWithContext(ImmutableStringReader reader, ComponentLike... args){
        return new RoyalCommandException(this, Component.translatable(translationKey, args), reader.getString(), reader.getCursor());
    }

    public RoyalCommandException createWithContext(ImmutableStringReader reader, TextColor color, ComponentLike... args){
        return new RoyalCommandException(this, Component.translatable(translationKey, color, args), reader.getString(), reader.getCursor());
    }

    public RoyalCommandException createWithContext(ImmutableStringReader reader, TextColor color, TextDecoration deco, ComponentLike... args){
        return new RoyalCommandException(this, Component.translatable(translationKey, Style.style(color, deco), args), reader.getString(), reader.getCursor());
    }

    public RoyalCommandException createWithContext(ImmutableStringReader reader, Style style, ComponentLike... args){
        return new RoyalCommandException(this, Component.translatable(translationKey, style, args), reader.getString(), reader.getCursor());
    }

    /**
     * Gets the translation key used by this type
     * @return The translation key
     */
    public String getTranslationKey() {
        return translationKey;
    }
}
