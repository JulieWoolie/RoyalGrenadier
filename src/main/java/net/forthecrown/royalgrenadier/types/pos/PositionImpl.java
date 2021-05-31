package net.forthecrown.royalgrenadier.types.pos;

import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.forthecrown.grenadier.types.pos.Position;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.IVectorPosition;
import net.minecraft.server.v1_16_R3.Vec3D;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock;

public class PositionImpl implements Position {

    private final IVectorPosition pos;
    PositionImpl(IVectorPosition vecPos){
        this.pos = vecPos;
    }

    public IVectorPosition getPos() {
        return pos;
    }

    @Override
    public Location getLocation(CommandSource source){
        Vec3D blockPos = pos.a(GrenadierUtils.sourceToNms(source));
        return new Location(source.getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    @Override
    public Location getBlockLocation(CommandSource source){
        return getLocation(source).toBlockLocation();
    }

    @Override
    public Location getCenteredLocation(CommandSource source){
        return getLocation(source).toCenterLocation();
    }

    @Override
    public Block getBlock(CommandSource source){
        BlockPosition blockPosition = pos.c(GrenadierUtils.sourceToNms(source));
        return new CraftBlock(((CraftWorld) source.getWorld()).getHandle(), blockPosition);
    }

    @Override
    public boolean isXRelative(){
        return pos.a();
    }

    @Override
    public boolean isYRelative(){
        return pos.b();
    }

    @Override
    public boolean isZRelative(){
        return pos.c();
    }
}
