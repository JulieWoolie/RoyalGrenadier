package net.forthecrown.grenadier.types.pos;

import org.bukkit.Location;

class EmptyPosition implements Position {
    EmptyPosition() {}

    @Override
    public Location apply(Location base) {
        return base;
    }

    @Override
    public boolean is2Dimensional() {
        return false;
    }

    @Override
    public boolean isLocalPosition() {
        return false;
    }

    @Override
    public boolean isXRelative() {
        return true;
    }

    @Override
    public boolean isYRelative() {
        return true;
    }

    @Override
    public boolean isZRelative() {
        return true;
    }

    @Override
    public double getX() {
        return 0;
    }

    @Override
    public double getY() {
        return 0;
    }

    @Override
    public double getZ() {
        return 0;
    }
}