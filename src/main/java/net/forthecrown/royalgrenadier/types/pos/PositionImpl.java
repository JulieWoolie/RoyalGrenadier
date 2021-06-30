package net.forthecrown.royalgrenadier.types.pos;

import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.types.pos.Position;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class PositionImpl implements Position {

    private final Coordinates pos;
    PositionImpl(Coordinates vecPos){
        this.pos = vecPos;
    }

    public Coordinates getPos() {
        return pos;
    }

    @Override
    public Location getLocation(CommandSource source){
        CommandSourceStack stack = GrenadierUtils.sourceToNms(source);

        float yaw = stack.getRotation().y;
        float pitch = stack.getRotation().x;

        Vec3 pos = this.pos.getPosition(stack);
        return new Location(source.getWorld(), pos.x, pos.y, pos.z, yaw, pitch);
    }

    @Override
    public Location getBlockLocation(CommandSource source){
        CommandSourceStack stack = GrenadierUtils.sourceToNms(source);
        BlockPos blockPos = pos.getBlockPos(stack);
        return new Location(source.getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    @Override
    public Location getCenteredLocation(CommandSource source){
        return getLocation(source).toCenterLocation();
    }

    @Override
    public Block getBlock(CommandSource source){
        return getBlockLocation(source).getBlock();
    }

    @Override
    public boolean isXRelative(){
        return pos.isXRelative();
    }

    @Override
    public boolean isYRelative(){
        return pos.isYRelative();
    }

    @Override
    public boolean isZRelative(){
        return pos.isZRelative();
    }
}
