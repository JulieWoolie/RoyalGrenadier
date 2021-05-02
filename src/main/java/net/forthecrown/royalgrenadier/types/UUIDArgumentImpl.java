package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.forthecrown.grenadier.types.UUIDArgument;
import net.minecraft.server.v1_16_R3.ArgumentUUID;

import java.util.UUID;

public class UUIDArgumentImpl implements UUIDArgument {
    protected UUIDArgumentImpl() {}
    public static final UUIDArgumentImpl INSTANCE = new UUIDArgumentImpl();

    @Override
    public UUID parse(StringReader reader) throws CommandSyntaxException {
        return ArgumentUUID.a().parse(reader);
    }
}
