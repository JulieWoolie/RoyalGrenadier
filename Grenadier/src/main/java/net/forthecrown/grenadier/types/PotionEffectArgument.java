package net.forthecrown.grenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.royalgrenadier.types.PotionEffectArgumentImpl;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.CompletableFuture;

public interface PotionEffectArgument extends ArgumentType<PotionEffectType> {

    static PotionEffectArgument effect() {
        return PotionEffectArgumentImpl.INSTANCE;
    }

    static PotionEffectType getEffect(CommandContext<CommandSource> context, String argument) {
        return context.getArgument(argument, PotionEffectType.class);
    }

    @Override
    PotionEffectType parse(StringReader reader) throws CommandSyntaxException;

    @Override
    <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder);
}
