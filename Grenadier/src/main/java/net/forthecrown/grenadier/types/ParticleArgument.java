package net.forthecrown.grenadier.types;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.types.ParticleArgumentImpl;
import org.bukkit.Particle;

/**
 * Represents an argument which will be parsed into a particle
 */
public interface ParticleArgument extends ArgumentType<Particle> {
    static ParticleArgument particle(){
        return ParticleArgumentImpl.INSTANCE;
    }

    static Particle getParticle(CommandContext<CommandSource> c, String argument){
        return c.getArgument(argument, Particle.class);
    }
}
