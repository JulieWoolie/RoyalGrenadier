package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.types.ParticleArgument;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.forthecrown.royalgrenadier.VanillaMappedArgument;
import net.minecraft.core.particles.ParticleOptions;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_19_R2.CraftParticle;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ParticleArgumentImpl implements ParticleArgument, VanillaMappedArgument {
    protected ParticleArgumentImpl() {}
    public static final ParticleArgumentImpl INSTANCE = new ParticleArgumentImpl();

    private final net.minecraft.commands.arguments.ParticleArgument particleArg
            = net.minecraft.commands.arguments.ParticleArgument.particle(GrenadierUtils.createBuildContext());

    @Override
    public Particle parse(StringReader reader) throws CommandSyntaxException {
        ParticleOptions nms = particleArg.parse(reader);
        return CraftParticle.toBukkit(nms);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CompletionProvider.suggestParticles(builder);
    }

    @Override
    public Collection<String> getExamples() {
        return particleArg.getExamples();
    }

    public net.minecraft.commands.arguments.ParticleArgument getVanillaArgumentType() {
        return particleArg;
    }

    @Override
    public boolean useVanillaSuggestions() {
        return true;
    }
}