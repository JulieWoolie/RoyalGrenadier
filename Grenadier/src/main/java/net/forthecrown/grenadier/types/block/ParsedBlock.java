package net.forthecrown.grenadier.types.block;

import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a block or block data which has been parsed through Brigadier
 */
public interface ParsedBlock extends Keyed, net.kyori.adventure.key.Keyed {
    /**
     * Gets the data of the parsed block
     * @return The block's parsed data
     */
    BlockData getData();

    /**
     * Gets the material of the parsed block
     * @return The parsed block's type
     */
    Material getMaterial();

    /**
     * Gets the parsed NBT data of the block as a string
     * @return The String NBT of the parsed block
     */
    @Nullable String getTags();

    /**
     * Tests the block against the given block
     * @param block The block to test against
     *
     * @return True, if the block share the
     *         same properties and data, false otherwise
     */
    boolean test(Block block);

    /**
     * Tests the parsed block against the given data.
     * Note that this will not compare any NBT data
     *
     * @param data The data to compare
     * @return True, if the blocks share the same
     *         material and properties
     */
    boolean test(BlockData data);

    /**
     * Tests the given block state against
     * this parsed block
     *
     * @param state The state to check
     * @return True, if the blocks share the
     *         same NBT data and properties
     */
    default boolean test(BlockState state) {
        return test(state.getBlock());
    }

    /**
     * Places the parsed block in the given world at
     * the given coordinates
     * <p>
     * This will place the block and apply any NBT
     * data the parsed result holds
     *
     * @param world The world to place in
     * @param x The block's X cord
     * @param y the block's Y cord
     * @param z the block's Z cord
     * @param applyPhysics Whether to apply physics to the set block
     */
    void place(World world, int x, int y, int z, boolean applyPhysics);

    /**
     * Places the parsed block in the given world at
     * the given coordinates
     * <p>
     * This will place the block and apply any NBT
     * data the parsed result holds
     * @see #place(World, int, int, int, boolean)
     * @param world The world to place in
     * @param x The block's X cord
     * @param y the block's Y cord
     * @param z the block's Z cord
     */
    default void place(World world, int x, int y, int z) {
        place(world, x, y, z, true);
    }

    /**
     * Places the parsed block in the given world at
     * the given coordinates
     * <p>
     * This will place the block and apply any NBT
     * data the parsed result holds
     * @see #place(World, int, int, int, boolean)
     * @param location The location to place at
     */
    default void place(Location location) {
        place(location, true);
    }

    /**
     * Places the parsed block in the given world at
     * the given coordinates
     * <p>
     * This will place the block and apply any NBT
     * data the parsed result holds
     * @see #place(World, int, int, int, boolean)
     * @param location The location to place at
     * @param applyPhysics Whether to apply physics to the set block
     */
    default void place(Location location, boolean applyPhysics) {
        place(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), applyPhysics);
    }
}