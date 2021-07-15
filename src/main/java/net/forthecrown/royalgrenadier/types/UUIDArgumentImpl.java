package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.forthecrown.grenadier.types.UUIDArgument;
import net.minecraft.commands.arguments.UuidArgument;

import java.util.Collection;
import java.util.UUID;

public class UUIDArgumentImpl implements UUIDArgument {
    protected UUIDArgumentImpl() {}
    public static final UUIDArgumentImpl INSTANCE = new UUIDArgumentImpl();
    private final UuidArgument idArg = UuidArgument.uuid();

    @Override
    public UUID parse(StringReader reader) throws CommandSyntaxException {
        return idArg.parse(reader);
    }

    @Override
    public Collection<String> getExamples() {
        return idArg.getExamples();
    }

    public UuidArgument getHandle() {
        return idArg;
    }
}
