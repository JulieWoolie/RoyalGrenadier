package net.forthecrown.royalgrenadier.types.block;

import lombok.Getter;
import net.forthecrown.grenadier.types.block.ParsedBlock;
import net.kyori.adventure.key.Key;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.block.CraftBlock;
import org.bukkit.craftbukkit.v1_19_R2.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R2.util.CraftNamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class ParsedBlockImpl implements ParsedBlock {

    @Getter
    private final BlockState state;

    @Getter
    private final Map<Property<?>, Comparable<?>> properties;

    @Getter
    private final CompoundTag tagCompound;
    private final ResourceLocation key;

    ParsedBlockImpl(BlockState state, Map<Property<?>, Comparable<?>> properties, CompoundTag tagCompound, ResourceLocation key) {
        this.state = state;
        this.properties = properties;
        this.tagCompound = tagCompound;
        this.key = key;
    }

    @Override
    public void place(World world, int x, int y, int z, boolean applyPhysics) {
        // Configure the state we're setting
        BlockState state = getState();

        // If we're not setting it to air, make sure state has correct shape
        if (!getMaterial().isAir()) {
            state = net.minecraft.world.level.block.Block.updateFromNeighbourShapes(
                    getState(),
                    ((CraftWorld) world).getHandle(),
                    new BlockPos(x, y, z)
            );
        }

        // Set the block
        Block b = world.getBlockAt(x, y, z);
        b.setBlockData(state.createCraftBlockData(), applyPhysics);

        // If we have NBT tags to set, set them
        if (tagCompound != null) {
            BlockEntity entity = ((CraftWorld) world).getHandle()
                    .getBlockEntity(new BlockPos(x, y, z));

            if(entity != null) {
                entity.load(tagCompound);
            }
        }
    }

    public ResourceLocation getVanillaKey() {
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
        return Objects.toString(getTagCompound(), null);
    }

    @Override
    public boolean test(Block block) {
        CraftBlock b = (CraftBlock) block;
        BlockState state = b.getNMS();

        if (!state.is(this.state.getBlock())) {
            return false;
        }

        if (!testProperties(state)) {
            return false;
        }

        if (tagCompound == null || tagCompound.isEmpty()) {
            return true;
        }

        return compareTags(b.getHandle().getBlockEntity(b.getPosition()));
    }

    @Override
    public boolean test(BlockData data) {
        CraftBlockData d = (CraftBlockData) data;
        return testProperties(d.getState());
    }

    private boolean testProperties(BlockState state) {
        for (Property<?> p: getProperties().keySet()) {
            if (state.getValue(p) != this.state.getValue(p)) {
                return false;
            }
        }

        return true;
    }

    private boolean compareTags(BlockEntity entity) {
        if(entity == null) {
            return false;
        }

        return NbtUtils.compareNbt(tagCompound, entity.saveWithFullMetadata(), true);
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