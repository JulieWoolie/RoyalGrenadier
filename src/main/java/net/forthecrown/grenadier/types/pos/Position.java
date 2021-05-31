package net.forthecrown.grenadier.types.pos;

import net.forthecrown.grenadier.CommandSource;
import org.bukkit.Location;
import org.bukkit.block.Block;

public interface Position {

    /**
     * Gets the location specified, requires Sender for any relative coordinates
     * @param source The sender that used this command
     * @return The parsed location
     */
    Location getLocation(CommandSource source);

    Location getBlockLocation(CommandSource source);

    Location getCenteredLocation(CommandSource source);

    Block getBlock(CommandSource source);

    /**
     * Returns whether the x cord is relative
     * @return Whether the X cord is relative '~' or '^'
     */
    boolean isXRelative();

    /**
     * Returns whether the y cord is relative
     * @return Whether the Y cord is relative '~' or '^'
     */
    boolean isYRelative();

    /**
     * Returns whether the z cord is relative
     * @return Whether the Z cord is relative '~' or '^'
     */
    boolean isZRelative();
}
