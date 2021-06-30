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

    /**
     * Gets the location specified, requires Sender for any relative coordinates
     * @param source The sender that used this command
     * @return The parsed location
     */
    Location getBlockLocation(CommandSource source);

    /**
     * Gets the location specified, requires Sender for any relative coordinates
     * <p>Centered to x .5 and z .5</p>
     * @param source The sender that used this command
     * @return The parsed location
     */
    Location getCenteredLocation(CommandSource source);

    /**
     * Gets the block at the specified position
     * @param source The sender that used this command
     * @return The block at the parsed location
     */
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
