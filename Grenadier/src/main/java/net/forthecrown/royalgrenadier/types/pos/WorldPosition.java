package net.forthecrown.royalgrenadier.types.pos;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.forthecrown.grenadier.types.pos.Position;
import org.bukkit.Location;

import static net.forthecrown.royalgrenadier.types.pos.PositionArgumentImpl.*;

@RequiredArgsConstructor
class WorldPosition implements Position {
    // Why did I use an array?
    // I don't know, I wrote this at 3AM
    @Getter
    private final Coordinate[] coordinates;

    @Override
    public Location apply(Location base) {
        base.setX(coordinates[X].apply(base.getX()));
        base.setY(coordinates[Y].apply(base.getY()));
        base.setZ(coordinates[Z].apply(base.getZ()));

        return base;
    }

    @Override
    public boolean isLocalPosition() {
        return false;
    }

    @Override
    public boolean is2Dimensional() {
        return coordinates[Y] == Coordinate.EMPTY;
    }

    @Override
    public boolean isXRelative() {
        return coordinates[X].isRelative();
    }

    @Override
    public boolean isYRelative() {
        return coordinates[Y].isRelative();
    }

    @Override
    public boolean isZRelative() {
        return coordinates[Z].isRelative();
    }

    @Override
    public double getX() {
        return coordinates[X].getValue();
    }

    @Override
    public double getY() {
        return coordinates[Y].getValue();
    }

    @Override
    public double getZ() {
        return coordinates[Z].getValue();
    }
}