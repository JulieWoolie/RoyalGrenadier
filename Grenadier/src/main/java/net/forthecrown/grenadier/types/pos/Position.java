package net.forthecrown.grenadier.types.pos;

import net.forthecrown.grenadier.CommandSource;
import org.bukkit.Location;
import org.bukkit.block.Block;

public interface Position {

    /** A position which always returns the given base location. */
    Position SELF = new EmptyPosition();

    /**
     * Gets the location specified, requires Sender for any relative coordinates
     * <p>Note: if the position is a 2D position, the Y will always be 0</p>
     * @param source The sender that used this command
     * @return The parsed location
     */
    default Location getLocation(CommandSource source) {
        return apply(source.getLocation());
    }

    /**
     * Applies this position's data to the given location
     * <p>
     * Note: The base location will NOT be cloned, but will
     * instead be modified and then returned
     * @param base The base location to modify
     * @return The transformed location with this position's data applied
     */
    Location apply(Location base);

    /**
     * Gets the location specified, requires Sender for any relative coordinates
     * @param source The sender that used this command
     * @return The parsed location
     */
    default Location getBlockLocation(CommandSource source) {
        return getLocation(source).toBlockLocation();
    }

    /**
     * Gets the location specified, requires Sender for any relative coordinates
     * <p>Centered to x .5 and z .5</p>
     * @param source The sender that used this command
     * @return The parsed location
     */
    default Location getCenteredLocation(CommandSource source) {
        return getLocation(source).toCenterLocation();
    }

    /**
     * Gets the block at the specified position
     * @param source The sender that used this command
     * @return The block at the parsed location
     */
    default Block getBlock(CommandSource source) {
         return getLocation(source).getBlock();
    }

    /**
     * Tests if this position is 2 dimensional,
     * meaning only an x and z coordinate have been
     * parsed.
     * <p>
     * This isn't determined during parsing, but rather
     * by the type of {@link PositionArgument} used.
     *
     * @return True, if only an x and z were parsed, false
     *         if all 3 x, y and z were parsed.
     */
    boolean is2Dimensional();

    /**
     * Tests if this position is a 'local' position.
     * <p>
     * A local position is one which was parsed with '^'
     * coordinates, meaning the relative offset of all
     * the coordinates is aligned with the direction a
     * given base location is facing.
     * <p>
     * If this returns false, it means this position is
     * a world relative position meaning all of this
     * position's coordinates are relative to the world.
     * @return True, if this is a local position, false otherwise
     */
    boolean isLocalPosition();

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

    /**
     * Gets the X of this position, may or may not be relative depending on if {@link Position#isXRelative()} is true
     * @return The X of this position
     */
    double getX();

    /**
     * Gets the Y of this position, may or may not be relative depending on if {@link Position#isYRelative()} is true
     * @return The Y of this position
     */
    double getY();

    /**
     * Gets the Z of this position, may or may not be relative depending on if {@link Position#isZRelative()} is true
     * @return The Z of this position
     */
    double getZ();
}