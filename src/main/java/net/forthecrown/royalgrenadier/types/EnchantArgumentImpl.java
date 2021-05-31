package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.exceptions.TranslatableExceptionType;
import net.forthecrown.grenadier.types.EnchantArgument;
import net.kyori.adventure.text.Component;
import net.minecraft.server.v1_16_R3.ArgumentEnchantment;
import net.minecraft.server.v1_16_R3.MinecraftKey;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class EnchantArgumentImpl implements EnchantArgument {

    public static final TranslatableExceptionType UNKNOWN_ENCHANTMENT = new TranslatableExceptionType("enchantment.unknown");
    protected EnchantArgumentImpl() {}
    public static final EnchantArgumentImpl INSTANCE = new EnchantArgumentImpl();

    @Override
    public Enchantment parse(StringReader reader) throws CommandSyntaxException {
        //ResourceLocation#read(StringReader reader)
        MinecraftKey key = MinecraftKey.a(reader);

        //Get the enchantment from the key
        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(key.getKey()));
        if(enchantment == null) throw UNKNOWN_ENCHANTMENT.createWithContext(reader, Component.text(key.toString()));

        return enchantment;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CompletionProvider.suggestEnchantments(builder);
    }

    @Override
    public Collection<String> getExamples() {
        return ArgumentEnchantment.a().getExamples();
    }
}
