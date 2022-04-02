package net.forthecrown.royalgrenadier;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.adventure.AdventureComponent;
import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.commands.FeedbackForwardingSender;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.forthecrown.grenadier.exceptions.RoyalCommandException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.translation.GlobalTranslator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.command.CraftBlockCommandSender;
import org.bukkit.craftbukkit.v1_18_R2.command.ProxiedNativeCommandSender;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class GrenadierUtils {

    public static CommandSourceStack sourceToNms(CommandSource source){
        return ((CommandSourceImpl) source).getHandle();
    }

    public static CommandSourceImpl wrap(CommandSourceStack stack, AbstractCommand command) {
        if(stack.source == net.minecraft.commands.CommandSource.NULL) {
            stack = MinecraftServer.getServer().createCommandSourceStack();
        }

        return new CommandSourceImpl(stack, command);
    }

    //Bukkit's getListener in VanillaCommandWrapper didn't have enough functionality to be as applicable
    public static CommandSourceStack senderToWrapper(CommandSender sender) {
        if(sender instanceof Entity) return ((CraftEntity) sender).getHandle().createCommandSourceStack();
        else if(sender instanceof BlockCommandSender) return ((CraftBlockCommandSender) sender).getWrapper();
        else if(sender instanceof RemoteConsoleCommandSender || sender instanceof ConsoleCommandSender) return ((CraftServer) Bukkit.getServer()).getServer().createCommandSourceStack();
        else if(sender instanceof ProxiedCommandSender) return ((ProxiedNativeCommandSender)sender).getHandle();
        else if(sender instanceof FeedbackForwardingSender serverSender) return serverSender.asVanilla();
        else return null;
    }

    //Converts a list from one type to another using the given function
    public static <T, F> List<T> convertList(Iterable<F> from, Function<F, T> function){
        List<T> res = new ArrayList<>();
        for (F f: from) res.add(function.apply(f));

        return res;
    }

    //Does the same thing as the above method but with an array
    public static <T, F> List<T> convertArray(F[] from, Function<F, T> function){
        return convertList(Arrays.asList(from), function);
    }

    //Returns a reader with the cursor at the correct position
    //Normally reading and then throwing exceptions causes the cursor to be placed at the wrong position
    //So we must correct it, but also do so with a new reader so the reader in the parse method moves forward
    public static ImmutableStringReader correctReader(ImmutableStringReader reader, int cursor){
        StringReader reader1 = new StringReader(reader.getString());
        reader1.setCursor(cursor);
        return reader1;
    }

    public static net.minecraft.network.chat.Component toVanilla(Component component) {
        if(component instanceof TranslatableComponent) {
            component = GlobalTranslator.render(component, Locale.ROOT);
        }

        return new AdventureComponent(component);
    }

    //Suggests a specific MinecraftKey collection
    public static CompletableFuture<Suggestions> suggestResource(Iterable<ResourceLocation> resources, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(resources, builder);
    }

    public static Component formatCommandException(CommandSyntaxException exception) {
        Component initialMessage = exception.getRawMessage() instanceof net.minecraft.network.chat.Component ?
                PaperAdventure.asAdventure((net.minecraft.network.chat.Component) exception.getRawMessage()) :
                Component.text(exception.getRawMessage().getString());

        initialMessage = initialMessage.style(RoyalCommandException.ERROR_MESSAGE_STYLE);

        if(exception.getInput() == null || exception.getCursor() < 1) return initialMessage;

        TextComponent.Builder builder = Component.text()
                .append(initialMessage)
                .append(Component.newline())
                .append(formatExceptionContext(exception));

        return builder.build();
    }

    public static Component formatExceptionContext(CommandSyntaxException e) {
        if (e.getInput() == null || e.getCursor() < 1) return null;

        final TextComponent.Builder builder = Component.text();
        final int cursor = Math.min(e.getInput().length(), e.getCursor());
        final int start = Math.max(0, cursor - CommandSyntaxException.CONTEXT_AMOUNT); //Either start of input or cursor - 10

        //Context too long, add dots
        if (start != 0) builder.append(Component.text("...").style(RoyalCommandException.GRAY_CONTEXT_STYLE));

        String grayContext = e.getInput().substring(start, cursor);
        String redContext = e.getInput().substring(cursor);

        builder.append(
                Component.text()
                        .clickEvent(ClickEvent.suggestCommand("/" + e.getInput())) //Clicking on the exception will put the input in chat

                        .append(Component.text(grayContext).style(RoyalCommandException.GRAY_CONTEXT_STYLE))
                        .append(Component.text(redContext).style(RoyalCommandException.RED_CONTEXT_STYLE))

                        .append(Component.translatable("command.context.here").style(RoyalCommandException.HERE_POINTER_STYLE)) //Tell them were they went wrong in life, answer is here, writing some useless ass comment for an API no one will use while failing school

                        .build()
        );

        return builder.build();
    }

    //Removes the decimal point from a string, if needed
    public static String decimal(String check, boolean allowDecimals){
        if(allowDecimals) return check;

        int index = check.indexOf('.');
        return index == -1 ? check : check.substring(0, index);
    }

    public static void suggestMatches(SuggestionsBuilder builder, String toSuggest, Message tooltip) {
        String token = builder.getRemainingLowerCase();

        if(toSuggest.startsWith(token)) builder.suggest(toSuggest, tooltip);
    }

    public static Message componentToMessage(@Nullable Component component) {
        if(component == null) return null;
        return PaperAdventure.asVanilla(component);
    }

    public static Component messageToComponent(@Nullable Message message) {
        if(message == null) return null;

        if(message instanceof net.minecraft.network.chat.Component) return PaperAdventure.asAdventure((net.minecraft.network.chat.Component) message);
        return Component.text(message.getString());
    }

    public static StringReader filterCommandInput(String input) {
        int spaceIndex = input.indexOf(' ');
        if(spaceIndex == -1) spaceIndex = input.length();

        String subStr = input.substring(0, spaceIndex);
        int seperatorIndex = subStr.indexOf(':');
        if(seperatorIndex != -1) {
            subStr = subStr.substring(seperatorIndex+1);
        }

        String filtered = subStr + input.substring(spaceIndex);

        StringReader reader = new StringReader(filtered);
        if(reader.canRead() && reader.peek() == '/') reader.skip();

        // we've been passed the 'execute' command input, gotta find the 'run' argument
        if(reader.getRemaining().startsWith("execute")) {
            String remaining = reader.getRemaining();
            int runIndex = remaining.indexOf("run");

            if(runIndex != -1) {
                reader = new StringReader(remaining.substring(runIndex + 3).trim());
            }
        }

        return reader;
    }

    public static boolean isSilent(CommandSourceStack stack) {
        Field silent = null;

        // the 'silent' field is the only boolean field in CommandSourceStack
        // at least currently, so any boolean fields we find must be the
        // silent field.
        for (Field f: stack.getClass().getDeclaredFields()) {
            if(f.getType().equals(Boolean.TYPE)) {
                silent = f;
                break;
            }
        }

        if(silent == null) {
            // This should not happen
            return false;
        }

        silent.setAccessible(true);

        try {
            return (boolean) silent.get(stack);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return false;
        }
    }
}