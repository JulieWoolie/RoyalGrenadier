package net.forthecrown.royalgrenadier.types.pos;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.Getter;
import net.forthecrown.grenadier.types.pos.Position;

import static net.forthecrown.royalgrenadier.types.pos.PositionArgumentImpl.*;

@Getter
class PositionParser {
    private final byte flags;
    private final StringReader reader;
    private final Coordinate[] coordinates;
    private final PosType type;

    public PositionParser(byte flags, StringReader reader) {
        this.flags = flags;
        this.reader = reader;
        coordinates = new Coordinate[LENGTH];

        // If the position we're parsing is a local type position,
        // then that should be visible instantly, as all local coordinates
        // have to be relative
        if (reader.canRead() && reader.peek() == PosType.LOCAL.getPrefixChar()) {
            type = PosType.LOCAL;
        } else {
            type = PosType.WORLD;
        }
    }

    public void parse() throws CommandSyntaxException {
        coordinates[X] = parseCord();
        coordinates[Y] = Coordinate.EMPTY;
        coordinates[Z] = parseCord();

        if (!hasFlags(FLAG_2D)) {
            coordinates[Y] = coordinates[Z];
            coordinates[Z] = parseCord();
        }
    }

    private Coordinate parseCord() throws CommandSyntaxException {
        var invalidType = type.opposite();

        reader.skipWhitespace();

        if (!reader.canRead()) {
            throw ERROR_NOT_COMPLETED.createWithContext(reader);
        }

        // --- Read if relative ---
        boolean relative;

        // If this is local, all coordinates must be
        // relative, so enforce that
        if (type == PosType.LOCAL) {
            relative = true;
            reader.expect(type.getPrefixChar());
        } else {
            // Else just check if it's relative
            char peeked = reader.peek();

            // Also make sure we're not mixing and matching
            // coordinate types
            if (peeked == invalidType.getPrefixChar()) {
                throw ERROR_MIXED.createWithContext(reader);
            }

            relative = peeked == type.getPrefixChar();

            if (relative) {
                reader.skip();
            }
        }

        // --- Read coordinate value ---
        double value = 0D;

        // If it's the end of the input or next there's a space
        // that means we've reached the end of this coordinates.
        if (!reader.canRead() || reader.peek() == ' ') {
            // Ooops, not relative, meaning invalid input
            if (!relative) {
                throw ERROR_NOT_COMPLETED.createWithContext(reader);
            }
        } else {
            // If block vector -> read integer, else -> read double
            value = hasFlags(FLAG_BLOCKPOS) ? reader.readInt() : reader.readDouble();
        }

        return new Coordinate(value, relative);
    }

    public Position get() {
        // A bit of a hack, but it works :)

        // If local, then we already know
        // all coordinates are relative, so
        // the local position uses a double[] to
        // store the coordinates instead of
        // a Coordinate[], so convert all current
        // Coordinate objects to double and return
        // the result
        if (type == PosType.LOCAL) {
            double[] cords = new double[LENGTH];
            boolean is2D = hasFlags(FLAG_2D);

            for (int i = 0; i < coordinates.length; i++) {
                cords[i] = coordinates[i].getValue();
            }

            return new LocalPosition(cords, is2D);
        }

        return new WorldPosition(coordinates);
    }

    private boolean hasFlags(int flags) {
        return (this.flags & flags) == flags;
    }
}