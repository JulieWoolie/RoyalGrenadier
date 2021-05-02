package net.forthecrown.grenadier.types.block;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public interface ParsedBlock {
    BlockData getData();
    Material getMaterial();
}
