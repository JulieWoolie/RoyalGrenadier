package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.forthecrown.grenadier.types.ParticleArgument;
import net.minecraft.server.v1_16_R3.ArgumentParticle;
import net.minecraft.server.v1_16_R3.IRegistry;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_16_R3.CraftParticle;

import java.util.concurrent.CompletableFuture;

public class ParticleArgumentImpl implements ParticleArgument {
    protected ParticleArgumentImpl() {}
    public static final ParticleArgumentImpl INSTANCE = new ParticleArgumentImpl();

    @Override
    public Particle parse(StringReader reader) throws CommandSyntaxException {
        net.minecraft.server.v1_16_R3.Particle<?> nms = ArgumentParticle.b(reader).getParticle();
        return CraftParticle.toBukkit(nms);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return GrenadierUtils.suggestResource(IRegistry.PARTICLE_TYPE.keySet(), builder);
    }
}
