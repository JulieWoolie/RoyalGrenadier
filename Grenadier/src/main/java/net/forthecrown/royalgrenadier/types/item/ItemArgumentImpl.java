package net.forthecrown.royalgrenadier.types.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.forthecrown.grenadier.types.item.ItemArgument;
import net.forthecrown.grenadier.types.item.ParsedItemStack;
import net.forthecrown.royalgrenadier.VanillaMappedArgument;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.item.Item;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ItemArgumentImpl implements ItemArgument, VanillaMappedArgument {
    protected ItemArgumentImpl() {}
    public static final ItemArgumentImpl INSTANCE = new ItemArgumentImpl();
    private final net.minecraft.commands.arguments.item.ItemArgument handle = net.minecraft.commands.arguments.item.ItemArgument.item(
            new CommandBuildContext(DedicatedServer.getServer().registryHolder)
    );

    @Override
    public ParsedItemStack parse(StringReader reader, boolean allowNBT) throws CommandSyntaxException {
        ItemParser.ItemResult parser = ItemParser.parseForItem(HolderLookup.forRegistry(Registry.ITEM), reader);
        Holder<Item> item = parser.item();
        CompoundTag tag = parser.nbt();

        return new ParsedItemImpl(
                new ItemInput(item, tag),
                tag
        );
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return handle.listSuggestions(context, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return handle.getExamples();
    }

    public net.minecraft.commands.arguments.item.ItemArgument getVanillaArgumentType() {
        return handle;
    }

    @Override
    public boolean useVanillaSuggestions() {
        return true;
    }
}