package net.forthecrown.royalgrenadier.types.pos;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.exceptions.TranslatableExceptionType;
import net.forthecrown.grenadier.types.pos.*;
import net.forthecrown.royalgrenadier.VanillaMappedArgument;
import net.minecraft.commands.arguments.coordinates.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.forthecrown.grenadier.types.pos.CoordinateSuggestion.*;

public class PositionArgumentImpl implements PositionArgument, VanillaMappedArgument {
    static final byte
        FLAG_BLOCKPOS = 0x1,
        FLAG_2D = 0x2,

        X = 0,
        Y = 1,
        Z = 2,

        LENGTH = 3;

    public static final TranslatableExceptionType
        ERROR_MIXED = new TranslatableExceptionType("argument.pos.mixed"),
        ERROR_NOT_COMPLETED = new TranslatableExceptionType("argument.pos3d.incomplete");

    public static final PositionArgumentImpl VECTOR_INSTANCE = new PositionArgumentImpl(0);
    public static final PositionArgumentImpl BLOCK_INSTANCE = new PositionArgumentImpl(FLAG_BLOCKPOS);

    public static final PositionArgumentImpl VECTOR_2D_INSTANCE = new PositionArgumentImpl(FLAG_2D);
    public static final PositionArgumentImpl BLOCK_2D_INSTANCE = new PositionArgumentImpl(FLAG_2D | FLAG_BLOCKPOS);

    private final ArgumentType<Coordinates> handle;
    private final byte flags;

    protected PositionArgumentImpl(int flags) {
        this.flags = (byte) flags;

        this.handle = switch (flags) {
            case FLAG_BLOCKPOS -> BlockPosArgument.blockPos();
            case FLAG_2D -> Vec2Argument.vec2();
            case FLAG_2D | FLAG_BLOCKPOS -> ColumnPosArgument.columnPos();
            default -> Vec3Argument.vec3();
        };
    }

    @Override
    public Position parse(StringReader reader) throws CommandSyntaxException {
        PositionParser parser = new PositionParser(flags, reader);
        parser.parse();

        return parser.get();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof CommandSource source) {
            List<CoordinateSuggestion> suggestions = new ArrayList<>();

            boolean is2D = (flags & FLAG_2D) != 0;
            boolean vector = (flags & FLAG_BLOCKPOS) == 0;

            // Add cords for the block they're looking at,
            // if there's a block to add cords for
            CoordinateSuggestion sourceSuggestion = is2D ? source.getRelevant2DCords() : source.getRelevant3DCords();

            if (sourceSuggestion != null) {
                suggestions.add(sourceSuggestion);
            }

            boolean local = builder.getRemainingLowerCase().contains("^");

            // Add default cords
            suggestions.add(getDefault(is2D, local));

            // Suggest the cords
            return CompletionProvider.suggestCords(builder, vector, suggestions);
        }

        return Suggestions.empty();
    }

    private static CoordinateSuggestion getDefault(boolean is2D, boolean local) {
        if (local) {
            return is2D ? DEFAULT_LOCAL_VEC2 : DEFAULT_LOCAL_VEC3;
        } else {
            return is2D ? DEFAULT_WORLD_VEC2 : DEFAULT_WORLD_VEC3;
        }
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