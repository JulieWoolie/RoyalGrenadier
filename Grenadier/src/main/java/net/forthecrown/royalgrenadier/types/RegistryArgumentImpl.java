package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.types.KeyArgument;
import net.forthecrown.grenadier.types.RegistryArgument;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.forthecrown.royalgrenadier.VanillaMappedArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import org.bukkit.Keyed;
import org.bukkit.Registry;

import java.util.concurrent.CompletableFuture;

@Getter
@RequiredArgsConstructor
public class RegistryArgumentImpl<T extends Keyed> implements RegistryArgument<T>, VanillaMappedArgument {
    private final Registry<T> registry;
    private final DynamicCommandExceptionType exceptionType;

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();

        var key = KeyArgument.minecraft().parse(reader);
        var value = registry.get(key);

        if (value == null) {
            throw exceptionType.createWithContext(
                    GrenadierUtils.correctReader(reader, cursor),
                    key
            );
        }

        return value;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CompletionProvider.suggestRegistry(builder, registry);
    }

    @Override
    public ArgumentType<?> getVanillaArgumentType() {
        return ResourceLocationArgument.id();
    }
}