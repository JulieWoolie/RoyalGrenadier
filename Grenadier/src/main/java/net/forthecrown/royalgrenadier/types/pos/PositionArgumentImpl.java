package net.forthecrown.royalgrenadier.types.pos;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.types.pos.*;
import net.forthecrown.royalgrenadier.VanillaMappedArgument;
import net.minecraft.commands.arguments.coordinates.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PositionArgumentImpl implements PositionArgument, VanillaMappedArgument {
    public static final PositionArgumentImpl VECTOR_INSTANCE = new PositionArgumentImpl(false, false);
    public static final PositionArgumentImpl VECTOR_2D_INSTANCE = new PositionArgumentImpl(false, true);

    public static final PositionArgumentImpl BLOCK_INSTANCE = new PositionArgumentImpl(true, false);
    public static final PositionArgumentImpl BLOCK_2D_INSTANCE = new PositionArgumentImpl(true, true);

    private final ArgumentType<Coordinates> handle;
    private final boolean blockPos;
    private final boolean is2D;

    protected PositionArgumentImpl(boolean isBlockPos, boolean is2D){
        blockPos = isBlockPos;
        this.is2D = is2D;

        if(is2D) this.handle = blockPos ? ColumnPosArgument.columnPos() : Vec2Argument.vec2();
        else this.handle = blockPos ? BlockPosArgument.blockPos() : Vec3Argument.vec3();
    }

    @Override
    public Position parse(StringReader reader) throws CommandSyntaxException {
        CoordinateParser parser = new CoordinateParser(reader, !blockPos);

        return is2D ? parser.parse2D() : parser.parse3D();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if(context.getSource() instanceof CommandSource) {
            CommandSource source = (CommandSource) context.getSource();
            List<CoordinateSuggestion> suggestions = new ArrayList<>();

            //Add cords for the block they're looking at, if there's a block to add cords for
            CoordinateSuggestion sourceSuggestion = is2D ? source.getRelevant2DCords() : source.getRelevant3DCords();
            if(sourceSuggestion != null) suggestions.add(sourceSuggestion);

            //Add default cords.
            suggestions.add(is2D ? Vec2Suggestion.DEFAULT : Vec3Suggestion.DEFAULT);

            //Suggest the cords
            return CompletionProvider.suggestCords(builder, !blockPos, suggestions);
        }

        return Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return handle.getExamples();
    }

    public ArgumentType<Coordinates> getVanillaArgumentType() {
        return handle;
    }

    @Override
    public boolean useVanillaSuggestions() {
        return true;
    }
}