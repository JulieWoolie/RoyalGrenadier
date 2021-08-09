package net.forthecrown.royalgrenadier.types.pos;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.forthecrown.grenadier.exceptions.TranslatableExceptionType;
import net.minecraft.world.level.Level;

//Parses coordinates from a given reader
public class CoordinateParser {
    public static final TranslatableExceptionType NOT_FINISHED = new TranslatableExceptionType("argument.pos3d.incomplete");
    public static final TranslatableExceptionType OUT_OF_BOUNDS = new TranslatableExceptionType("argument.pos.outofbounds");

    private final StringReader reader;
    private final boolean allowDecimals;

    public CoordinateParser(StringReader reader, boolean allowDecimals) {
        this.reader = reader;
        this.allowDecimals = allowDecimals;
    }

    //Reads a single cord from the reader
    private Coordinate readCord() throws CommandSyntaxException {
        if(!reader.canRead()) throw NOT_FINISHED.createWithContext(reader);
        reader.skipWhitespace();

        boolean relative = false;
        double cord = 0D;

        //If cord starts with ~ or ^, it's relative
        if(reader.peek() == '^' || reader.peek() == '~'){
            relative = true;
            reader.skip();
        }

        //If you can still read, read a number
        if(reader.canRead() && reader.peek() != ' '){
            //If it's relative or if we're allowing decimals
            //Read a double, if not, read an int
            cord = allowDecimals || relative ? reader.readDouble() : reader.readInt();

            //If they haven't typed in a relative char, then they've not put any cords here.
        } else if(!relative) throw NOT_FINISHED.createWithContext(reader);

        return new Coordinate(relative, cord);
    }

    //Parse as 3D
    public Position3D parse3D() throws CommandSyntaxException {
        //Read 3 cords, validate x and z are inside the world
        Coordinate xCord = readCord().validateInWorld();
        Coordinate yCord = readCord();
        Coordinate zCord = readCord().validateInWorld();

        return new Position3D(xCord.relative, yCord.relative, zCord.relative, xCord.cord, yCord.cord, zCord.cord);
    }

    //Parse cords from the reader into a 2D position
    public Position2D parse2D() throws CommandSyntaxException {
        //Read two cords, validate that both are in world
        Coordinate xCord = readCord().validateInWorld();
        Coordinate zCord = readCord().validateInWorld();

        return new Position2D(xCord.relative, zCord.relative, xCord.cord, zCord.cord);
    }

    public StringReader getReader() {
        return reader;
    }

    public boolean allowDecimals() {
        return allowDecimals;
    }

    //A quick class to return two values at once in the readCord method
    private class Coordinate {
        private final boolean relative;
        private final double cord;

        private Coordinate(boolean relative, double cord) {
            this.relative = relative;
            this.cord = cord;
        }

        //Validate the given cord is not larger than the max world border.
        private Coordinate validateInWorld() throws CommandSyntaxException {
            if(cord > Level.MAX_LEVEL_SIZE || cord < -Level.MAX_LEVEL_SIZE) throw OUT_OF_BOUNDS.createWithContext(reader);
            return this;
        }
    }
}
