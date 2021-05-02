package net.forthecrown.royalgrenadier.types.pos;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.types.pos.Position;
import net.forthecrown.grenadier.types.pos.PositionArgument;
import net.minecraft.server.v1_16_R3.ArgumentVec3;
import net.minecraft.server.v1_16_R3.CommandDispatcher;
import net.minecraft.server.v1_16_R3.ICompletionProvider;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class PositionArgumentImpl implements PositionArgument {
    protected PositionArgumentImpl() {}
    public static final PositionArgumentImpl INSTANCE = new PositionArgumentImpl();

    @Override
    public Position parse(StringReader reader) throws CommandSyntaxException {
        return new PositionImpl(ArgumentVec3.a().parse(reader));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String token = builder.getRemaining();
        Collection<ICompletionProvider.a> suggestions;

        if (!token.isEmpty() && token.charAt(0) == '^') suggestions = Collections.singleton(net.minecraft.server.v1_16_R3.ICompletionProvider.a.a);
        else suggestions = Collections.singleton(ICompletionProvider.a.b);

        return ICompletionProvider.a(token, suggestions, builder, CommandDispatcher.a(this::parse));
    }

    @Override
    public Collection<String> getExamples() {
        return Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5");
    }
}
