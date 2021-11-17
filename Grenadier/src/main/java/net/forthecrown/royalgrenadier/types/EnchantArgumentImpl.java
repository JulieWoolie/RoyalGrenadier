package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.exceptions.TranslatableExceptionType;
import net.forthecrown.grenadier.types.EnchantArgument;
import net.forthecrown.grenadier.types.KeyArgument;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.util.concurrent.CompletableFuture;

public class EnchantArgumentImpl implements EnchantArgument {

    public static final TranslatableExceptionType UNKNOWN_ENCHANTMENT = new TranslatableExceptionType("enchantment.unknown");
    protected EnchantArgumentImpl() {}
    public static final EnchantArgumentImpl INSTANCE = new EnchantArgumentImpl();

    @Override
    public Enchantment parse(StringReader reader) throws CommandSyntaxException {
        NamespacedKey key = KeyArgument.minecraft().parse(reader);

        //Get the enchantment from the key
        Enchantment enchantment = Enchantment.getByKey(key);
        if(enchantment == null) throw UNKNOWN_ENCHANTMENT.createWithContext(reader, Component.text(key.toString()));

        return enchantment;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CompletionProvider.suggestEnchantments(builder);
    }
}
