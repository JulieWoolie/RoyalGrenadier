package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.forthecrown.grenadier.exceptions.TranslatableExceptionType;
import net.forthecrown.grenadier.types.KeyArgument;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.kyori.adventure.key.Key;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang.Validate;
import org.bukkit.NamespacedKey;

import java.util.regex.Pattern;

public class KeyArgumentImpl implements KeyArgument {
    //Make this field public in NamespacedKey ;-;
    public static final Pattern VALID_NAMESPACE = Pattern.compile("[a-z0-9._-]+");

    public static final KeyArgumentImpl MINECRAFT_INSTANCE = new KeyArgumentImpl(Key.MINECRAFT_NAMESPACE);
    public static final TranslatableExceptionType INVALID_KEY = new TranslatableExceptionType("argument.id.invalid");

    private final String defaultNamespace;

    public KeyArgumentImpl(String defaultNamespace) {
        Validate.isTrue(VALID_NAMESPACE.matcher(defaultNamespace).matches(), "Invalid namespace");
        this.defaultNamespace = defaultNamespace;
    }

    @Override
    public String getDefaultNamespace() {
        return defaultNamespace;
    }

    @Override
    public NamespacedKey parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();

        while (reader.canRead() && ResourceLocation.isAllowedInResourceLocation(reader.peek())) {
            reader.skip();
        }

        int end = reader.getCursor();
        String str = reader.getString().substring(cursor, end);

        if(str.contains(":")) { //If has namespace
            String[] split = str.split(":");
            if(split.length != 2 || split[0].isBlank() || split[1].isBlank()) throw INVALID_KEY.createWithContext(GrenadierUtils.correctReader(reader, cursor));

            return tryReturn(split[0], split[1], reader, cursor);
        }

        return tryReturn(getDefaultNamespace(), str, reader, cursor);
    }

    public ArgumentType<ResourceLocation> getHandle() {
        return ResourceLocationArgument.id();
    }

    private static NamespacedKey tryReturn(String namespace, String value, ImmutableStringReader reader, int cursor) throws CommandSyntaxException {
        try {
            return new NamespacedKey(namespace, value);
        } catch (IllegalArgumentException e) {
            throw INVALID_KEY.createWithContext(GrenadierUtils.correctReader(reader, cursor));
        }
    }
}