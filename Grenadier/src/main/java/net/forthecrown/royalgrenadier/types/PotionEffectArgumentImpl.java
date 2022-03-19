package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.types.PotionEffectArgument;
import net.minecraft.commands.arguments.MobEffectArgument;
import net.minecraft.world.effect.MobEffect;
import org.bukkit.craftbukkit.v1_18_R2.potion.CraftPotionEffectType;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.CompletableFuture;

public class PotionEffectArgumentImpl implements PotionEffectArgument {
    public static final PotionEffectArgumentImpl INSTANCE = new PotionEffectArgumentImpl();
    protected PotionEffectArgumentImpl() {}

    private final MobEffectArgument handle = MobEffectArgument.effect();

    @Override
    public PotionEffectType parse(StringReader reader) throws CommandSyntaxException {
        MobEffect effect = handle.parse(reader);

        return new CraftPotionEffectType(effect);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CompletionProvider.suggestEffects(builder);
    }

    public MobEffectArgument getHandle() {
        return handle;
    }
}
