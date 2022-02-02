package net.forthecrown.grenadier.types.block;

import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a block or block data which has been parsed through Brigadier
 */
public interface ParsedBlock extends Keyed, net.kyori.adventure.key.Keyed {
    BlockData getData();
    Material getMaterial();

    /**
     * Gets the parsed NBT data of the block as a string
     * @return The String NBT of the parsed block
     */
    @Nullable String getTags();

    /**
     * Places the parsed block in the given world at the given coordinates
     * <p> This will place the block and apply any NBT data the parsed result holds </p>
     *
     * @param world The world to place in
     * @param x The block's X cord
     * @param y the block's Y cord
     * @param z the block's Z cord
     * @param applyPhysics Whether to apply physics to the set block
     */
    void place(World world, int x, int y, int z, boolean applyPhysics);

    /**
     * @see ParsedBlock#place(World, int, int, int, boolean)
     * @param world The world to place in
     * @param x The block's X cord
     * @param y the block's Y cord
     * @param z the block's Z cord
     */
    default void place(World world, int x, int y, int z) {
        place(world, x, y, z, true);
    }

    /**
     * @see ParsedBlock#place(World, int, int, int, boolean)
     * @param location The location to place at
     */
    default void place(Location location) {
        place(location, true);
    }

    /**
     * @see ParsedBlock#place(World, int, int, int, boolean)
     * @param location The location to place at
     * @param applyPhysics Whether to apply physics to the set block
     */
    default void place(Location location, boolean applyPhysics) {
        place(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), applyPhysics);
    }
}
