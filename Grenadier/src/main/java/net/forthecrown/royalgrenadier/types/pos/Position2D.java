package net.forthecrown.royalgrenadier.types.pos;

import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.types.pos.Position;
import org.bukkit.Location;

public class Position2D implements Position {

    private final boolean xRelative;
    private final boolean zRelative;

    private final double x;
    private final double z;

    public Position2D(boolean xRelative, boolean zRelative, double x, double z) {
        this.xRelative = xRelative;
        this.zRelative = zRelative;
        this.x = x;
        this.z = z;
    }

    @Override
    public Location getLocation(CommandSource source) {
        Location sourceLoc = source.getLocation();

        return new Location(
                sourceLoc.getWorld(),

                isXRelative() ? sourceLoc.getX() + x : x,
                0,
                isZRelative() ? sourceLoc.getZ() + z : z,

                sourceLoc.getYaw(),
                sourceLoc.getPitch()
        );
    }

    @Override
    public boolean isXRelative() {
        return xRelative;
    }

    @Override
    public boolean isYRelative() {
        return false;
    }

    @Override
    public boolean isZRelative() {
        return zRelative;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return 0;
    }

    @Override
    public double getZ() {
        return z;
    }
}
