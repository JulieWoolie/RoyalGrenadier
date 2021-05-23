package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.adventure.PaperAdventure;
import net.forthecrown.grenadier.types.ComponentArgument;
import net.kyori.adventure.text.Component;
import net.minecraft.server.v1_16_R3.ArgumentChatComponent;

import java.util.Collection;

public class ComponentArgumentImpl implements ComponentArgument {
    protected ComponentArgumentImpl() {}
    public static final ComponentArgumentImpl INSTANCE = new ComponentArgumentImpl();

    @Override
    public Component parse(StringReader reader) throws CommandSyntaxException {
        return PaperAdventure.asAdventure(ArgumentChatComponent.a().parse(reader));
    }

    @Override
    public Collection<String> getExamples() {
        return ArgumentChatComponent.a().getExamples();
    }
}
