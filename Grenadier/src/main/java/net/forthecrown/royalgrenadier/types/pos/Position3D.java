package net.forthecrown.royalgrenadier.types.pos;

import net.forthecrown.grenadier.types.pos.Position;
import org.bukkit.Location;

public class Position3D implements Position {

    private final boolean xRelative;
    private final boolean yRelative;
    private final boolean zRelative;

    private final double x;
    private final double y;
    private final double z;

    public Position3D(boolean xRelative, boolean yRelative, boolean zRelative, double x, double y, double z) {
        this.xRelative = xRelative;
        this.yRelative = yRelative;
        this.zRelative = zRelative;

        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Location apply(Location sourceLoc) {
        return new Location(
                sourceLoc.getWorld(),

                isXRelative() ? sourceLoc.getX() + x : x,
                isYRelative() ? sourceLoc.getY() + y : y,
                isZRelative() ? sourceLoc.getZ() + z : z,

                sourceLoc.getYaw(),
                sourceLoc.getPitch()
        );
    }

    @Override
    public boolean isXRelative(){
        return xRelative;
    }

    @Override
    public boolean isYRelative(){
        return yRelative;
    }

    @Override
    public boolean isZRelative(){
        return zRelative;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getZ() {
        return z;
    }
}