package net.forthecrown.royalgrenadier.types.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.types.item.ItemArgument;
import net.forthecrown.grenadier.types.item.ParsedItemStack;
import net.minecraft.server.v1_16_R3.ArgumentItemStack;
import net.minecraft.server.v1_16_R3.ArgumentParserItemStack;
import net.minecraft.server.v1_16_R3.ArgumentPredicateItemStack;

import java.util.concurrent.CompletableFuture;

public class ItemArgumentImpl implements ItemArgument {
    protected ItemArgumentImpl() {}
    public static final ItemArgumentImpl INSTANCE = new ItemArgumentImpl();

    @Override
    public ParsedItemStack parse(StringReader reader) throws CommandSyntaxException {
        ArgumentParserItemStack parser = new ArgumentParserItemStack(reader, false).h();
        return new ParsedItemImpl(new ArgumentPredicateItemStack(parser.b(), parser.c()));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ArgumentItemStack.a().listSuggestions(context, builder);
    }
}
