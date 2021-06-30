package net.forthecrown.royalgrenadier.types.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.types.item.ItemArgument;
import net.forthecrown.grenadier.types.item.ParsedItemStack;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ItemArgumentImpl implements ItemArgument {
    protected ItemArgumentImpl() {}
    public static final ItemArgumentImpl INSTANCE = new ItemArgumentImpl();
    private final net.minecraft.commands.arguments.item.ItemArgument handle = net.minecraft.commands.arguments.item.ItemArgument.item();

    @Override
    public ParsedItemStack parse(StringReader reader) throws CommandSyntaxException {
        return new ParsedItemImpl(handle.parse(reader));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return handle.listSuggestions(context, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return handle.getExamples();
    }
}
