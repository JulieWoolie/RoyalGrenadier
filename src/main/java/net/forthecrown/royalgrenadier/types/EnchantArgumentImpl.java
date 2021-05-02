package net.forthecrown.royalgrenadier.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.forthecrown.grenadier.types.EnchantArgument;
import net.minecraft.server.v1_16_R3.IRegistry;
import net.minecraft.server.v1_16_R3.MinecraftKey;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.util.concurrent.CompletableFuture;

public class EnchantArgumentImpl implements EnchantArgument {

    public static final DynamicCommandExceptionType UNKNOWN_ENCHANTMENT = new DynamicCommandExceptionType(obj -> () -> "Unknown enchantment: " + obj.toString());
    protected EnchantArgumentImpl() {}
    public static final EnchantArgumentImpl INSTANCE = new EnchantArgumentImpl();

    @Override
    public Enchantment parse(StringReader reader) throws CommandSyntaxException {
        //ResourceLocation#read(StringReader reader)
        MinecraftKey key = MinecraftKey.a(reader);

        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(key.getKey()));
        if(enchantment == null) throw UNKNOWN_ENCHANTMENT.createWithContext(reader, key);

        return enchantment;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return GrenadierUtils.suggestResource(IRegistry.ENCHANTMENT.keySet(), builder);
    }
}
