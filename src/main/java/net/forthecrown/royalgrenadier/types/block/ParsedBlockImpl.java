package net.forthecrown.royalgrenadier.types.block;

import net.forthecrown.grenadier.types.block.ParsedBlock;
import net.minecraft.server.v1_16_R3.ArgumentTileLocation;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public class ParsedBlockImpl implements ParsedBlock {

    private final ArgumentTileLocation block;
    ParsedBlockImpl(ArgumentTileLocation block){
        this.block = block;
    }

    public ArgumentTileLocation getBlock() {
        return block;
    }

    @Override
    public BlockData getData() {
        return block.a().createCraftBlockData();
    }

    @Override
    public Material getMaterial() {
        return block.a().getBukkitMaterial();
    }
}
