package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.types.PotionEffectArgument;
import net.forthecrown.grenadier.types.RegistryArgument;
import net.forthecrown.royalgrenadier.VanillaMappedArgument;
import net.minecraft.commands.arguments.MobEffectArgument;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.CompletableFuture;

public class PotionEffectArgumentImpl implements PotionEffectArgument, VanillaMappedArgument {
    protected PotionEffectArgumentImpl() {}

    public static final PotionEffectArgumentImpl INSTANCE = new PotionEffectArgumentImpl();
    private static final DynamicCommandExceptionType UNKNOWN = MobEffectArgument.ERROR_UNKNOWN_EFFECT;

    private static final RegistryArgument<PotionEffectType> ARGUMENT = RegistryArgument.registry(
            Registry.POTION_EFFECT_TYPE, UNKNOWN
    );

    private final MobEffectArgument handle = MobEffectArgument.effect();

    @Override
    public PotionEffectType parse(StringReader reader) throws CommandSyntaxException {
        return ARGUMENT.parse(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ARGUMENT.listSuggestions(context, builder);
    }

    public MobEffectArgument getVanillaArgumentType() {
        return handle;
    }

    @Override
    public boolean useVanillaSuggestions() {
        return true;
    }
}