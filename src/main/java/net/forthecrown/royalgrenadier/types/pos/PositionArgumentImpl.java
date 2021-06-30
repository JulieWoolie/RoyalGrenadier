package net.forthecrown.royalgrenadier.types.pos;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.types.pos.Position;
import net.forthecrown.grenadier.types.pos.PositionArgument;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class PositionArgumentImpl implements PositionArgument {
    public static final PositionArgumentImpl VECTOR_INSTANCE = new PositionArgumentImpl(false);
    public static final PositionArgumentImpl BLOCK_INSTANCE = new PositionArgumentImpl(true);

    private final ArgumentType<Coordinates> handle;

    protected PositionArgumentImpl(boolean isBlockPos){
        handle = isBlockPos ? BlockPosArgument.blockPos() : Vec3Argument.vec3();
    }

    @Override
    public Position parse(StringReader reader) throws CommandSyntaxException {
        return new PositionImpl(handle.parse(reader));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String token = builder.getRemaining();
        Collection<SharedSuggestionProvider.TextCoordinates> suggestions;

        if (!token.isEmpty() && token.charAt(0) == '^') suggestions = Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_LOCAL);
        else suggestions = Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_GLOBAL);

        return SharedSuggestionProvider.suggestCoordinates(token, suggestions, builder, Commands.createValidator(this::parse));
    }

    @Override
    public Collection<String> getExamples() {
        return Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5");
    }
}
