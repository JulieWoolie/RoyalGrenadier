package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.types.EnchantArgument;
import net.forthecrown.grenadier.types.RegistryArgument;
import net.forthecrown.royalgrenadier.VanillaMappedArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;

import java.util.concurrent.CompletableFuture;

public class EnchantArgumentImpl implements EnchantArgument, VanillaMappedArgument {
    protected EnchantArgumentImpl() {}

    public static final EnchantArgumentImpl INSTANCE = new EnchantArgumentImpl();

    public static final RegistryArgument<Enchantment>
            ARGUMENT = RegistryArgument.registry(Registry.ENCHANTMENT, "Enchantment");

    @Override
    public Enchantment parse(StringReader reader) throws CommandSyntaxException {
        return ARGUMENT.parse(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ARGUMENT.listSuggestions(context, builder);
    }

    @Override
    public ArgumentType<?> getVanillaArgumentType() {
        return ResourceLocationArgument.id();
    }
}