package net.forthecrown.royalgrenadier.types.block;

import net.forthecrown.grenadier.types.block.ParsedBlock;
import net.kyori.adventure.key.Key;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftNamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ParsedBlockImpl implements ParsedBlock {

    private final BlockState state;
    private final Set<Property<?>> properties;
    private final CompoundTag tags;
    private final ResourceLocation key;

    ParsedBlockImpl(BlockState state, Set<Property<?>> properties, CompoundTag tags, ResourceLocation key) {
        this.state = state;
        this.properties = properties;
        this.tags = tags;
        this.key = key;
    }

    public BlockState getState() {
        return state;
    }

    public @Nullable CompoundTag getTagCompound() {
        return tags;
    }

    @Override
    public void place(World world, int x, int y, int z, boolean applyPhysics) {
        // Configure the state we're setting
        BlockState state = getState();
        if(!getMaterial().isAir()) { // If we're not setting it to air, make sure state has correct shape
            state = net.minecraft.world.level.block.Block.updateFromNeighbourShapes(getState(), ((CraftWorld) world).getHandle(), new BlockPos(x, y, z));
        }

        // Set the block
        Block b = world.getBlockAt(x, y, z);
        b.setBlockData(state.createCraftBlockData(), applyPhysics);

        // If we have NBT tags to set, set them
        if(tags != null) {
            BlockEntity entity = ((CraftWorld) world).getHandle().getBlockEntity(new BlockPos(x, y, z));
            if(entity != null) entity.load(tags);
        }
    }

    public Set<Property<?>> getProperties() {
        return properties;
    }

    public ResourceLocation getVanillaKey(){
        return key;
    }

    @Override
    public BlockData getData() {
        return getState().createCraftBlockData();
    }

    @Override
    public Material getMaterial() {
        return getData().getMaterial();
    }

    @Override
    public @Nullable String getTags() {
        return tags == null ? null : tags.toString();
    }

    @Override
    public @NotNull Key key() {
        return getKey();
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return CraftNamespacedKey.fromMinecraft(getVanillaKey());
    }
}
