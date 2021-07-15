package net.forthecrown.royalgrenadier.types.pos;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.forthecrown.grenadier.exceptions.TranslatableExceptionType;
import net.minecraft.world.level.Level;

public class CoordinateParser {
    public static final TranslatableExceptionType NOT_FINISHED = new TranslatableExceptionType("argument.pos3d.incomplete");
    public static final TranslatableExceptionType OUT_OF_BOUNDS = new TranslatableExceptionType("argument.pos.outofbounds");

    private final StringReader reader;

    public CoordinateParser(StringReader reader) {
        this.reader = reader;
    }

    private Coordinate readCord() throws CommandSyntaxException {
        if(!reader.canRead()) throw NOT_FINISHED.createWithContext(reader);
        reader.skipWhitespace();

        boolean relative = false;
        double cord = 0D;

        if(reader.peek() == '^' || reader.peek() == '~'){
            relative = true;
            reader.skip();
        }

        if(reader.canRead() && reader.peek() != ' '){
            cord = reader.readDouble();
        } else if(!relative) throw NOT_FINISHED.createWithContext(reader);

        return new Coordinate(relative, cord);
    }

    public PositionImpl parse() throws CommandSyntaxException {
        Coordinate xCord = readCord().validateInWorld();
        Coordinate yCord = readCord();
        Coordinate zCord = readCord().validateInWorld();

        return new PositionImpl(xCord.relative, yCord.relative, zCord.relative, xCord.cord, yCord.cord, zCord.cord);
    }

    public PositionImpl parse2D() throws CommandSyntaxException {
        Coordinate xCord = readCord().validateInWorld();
        Coordinate zCord = readCord().validateInWorld();

        return new PositionImpl(xCord.relative, false, zCord.relative, xCord.cord, 0D, zCord.cord);
    }

    private class Coordinate {
        private final boolean relative;
        private final double cord;

        private Coordinate(boolean relative, double cord) {
            this.relative = relative;
            this.cord = cord;
        }

        private Coordinate validateInWorld() throws CommandSyntaxException {
            if(cord > Level.MAX_LEVEL_SIZE || cord < -Level.MAX_LEVEL_SIZE) throw OUT_OF_BOUNDS.createWithContext(reader);
            return this;
        }
    }
}
