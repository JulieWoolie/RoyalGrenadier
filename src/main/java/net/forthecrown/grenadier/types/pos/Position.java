package net.forthecrown.grenadier.types.pos;

import net.forthecrown.grenadier.CommandSource;
import org.bukkit.Location;

public interface Position {
    Location getLocation(CommandSource source);

    boolean isXRelative();

    boolean isYRelative();

    boolean isZRelative();
}
