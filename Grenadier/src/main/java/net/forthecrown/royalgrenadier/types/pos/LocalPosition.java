package net.forthecrown.royalgrenadier.types.pos;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.types.pos.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R2.util.CraftVector;

import static net.forthecrown.royalgrenadier.types.pos.PositionArgumentImpl.*;

@RequiredArgsConstructor
class LocalPosition implements Position {
    @Getter
    private final double[] coordinates;
    private final boolean twoDimensional;

    @Override
    public Location getLocation(CommandSource source) {
        return apply(source.getAnchoredLocation());
    }

    @Override
    public Location apply(Location base) {
        var pos = CraftVector.toNMS(base.toVector());
        var rot = new Vec2(base.getYaw(), base.getPitch());

        var newPos = applyLocal(pos, rot);

        base.setX(newPos.x);
        base.setY(newPos.y);
        base.setZ(newPos.z);

        return base;
    }

    private Vec3 applyLocal(Vec3 pos, Vec2 rot) {
        // I won't even lie, I copy-pasted all of this code from
        // LocalCoordinates, aka from NMS code, in my defence,
        // I don't know trigonometry, or whatever type of math
        // this is, so uhhh... I had to
        //    - Jules <3

        var x = getX();
        var y = getY();
        var z = getZ();

        float f = Mth.cos((rot.y + 90.0F) * ((float)Math.PI / 180F));
        float g = Mth.sin((rot.y + 90.0F) * ((float)Math.PI / 180F));

        float h = Mth.cos(-rot.x * ((float)Math.PI / 180F));
        float i = Mth.sin(-rot.x * ((float)Math.PI / 180F));

        float j = Mth.cos((-rot.x + 90.0F) * ((float)Math.PI / 180F));
        float k = Mth.sin((-rot.x + 90.0F) * ((float)Math.PI / 180F));

        Vec3 vec32 = new Vec3(f * h, i, g * h);
        Vec3 vec33 = new Vec3(f * j, k, g * j);
        Vec3 vec34 = vec32.cross(vec33).scale(-1.0D);

        double d = vec32.x * x + vec33.x * y + vec34.x * z;
        double e = vec32.y * x + vec33.y * y + vec34.y * z;
        double l = vec32.z * x + vec33.z * y + vec34.z * z;

        return new Vec3(
                pos.x + d,
                is2Dimensional() ? pos.y : pos.y + e,
                pos.z + l
        );
    }

    @Override
    public boolean isLocalPosition() {
        return true;
    }

    @Override
    public boolean is2Dimensional() {
        return twoDimensional;
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
        return coordinates[X];
    }

    @Override
    public double getY() {
        return coordinates[Y];
    }

    @Override
    public double getZ() {
        return coordinates[Z];
    }
}