package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.adventure.PaperAdventure;
import net.forthecrown.grenadier.types.ComponentArgument;
import net.kyori.adventure.text.Component;

import java.util.Collection;

public class ComponentArgumentImpl implements ComponentArgument {
    protected ComponentArgumentImpl() {}
    public static final ComponentArgumentImpl INSTANCE = new ComponentArgumentImpl();
    private final net.minecraft.commands.arguments.ComponentArgument compArg = net.minecraft.commands.arguments.ComponentArgument.textComponent();

    @Override
    public Component parse(StringReader reader) throws CommandSyntaxException {
        return PaperAdventure.asAdventure(compArg.parse(reader));
    }

    @Override
    public Collection<String> getExamples() {
        return compArg.getExamples();
    }
}
