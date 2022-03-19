package net.forthecrown.grenadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.forthecrown.royalgrenadier.types.UUIDArgumentImpl;

import java.util.UUID;

public interface UUIDArgument extends ArgumentType<UUID> {

    static UUIDArgument uuid(){
        return UUIDArgumentImpl.INSTANCE;
    }

    static UUID getUUID(CommandContext<?> c, String argument){
        return c.getArgument(argument, UUID.class);
    }
}
