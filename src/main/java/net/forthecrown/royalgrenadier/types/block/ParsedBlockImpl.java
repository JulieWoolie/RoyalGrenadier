package net.forthecrown.royalgrenadier.types.block;

import net.forthecrown.grenadier.types.block.ParsedBlock;
import net.kyori.adventure.key.Key;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

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

    public CompoundTag getTags() {
        return tags;
    }

    public Set<Property<?>> getProperties() {
        return properties;
    }

    public ResourceLocation getVanillaKey(){
        return key;
    }

    @Override
    public BlockData getData() {
        return state.createCraftBlockData();
    }

    @Override
    public Material getMaterial() {
        return getData().getMaterial();
    }

    @Override
    public @NotNull Key key() {
        return getKey();
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return new NamespacedKey(key.getNamespace(), key.getPath());
    }
}
