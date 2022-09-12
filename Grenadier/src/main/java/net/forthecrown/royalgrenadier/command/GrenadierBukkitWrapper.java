package net.forthecrown.royalgrenadier.command;

import com.google.common.base.Joiner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.tree.LiteralCommandNode;
import lombok.Getter;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.forthecrown.royalgrenadier.RoyalGrenadier;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.craftbukkit.v1_19_R1.command.VanillaCommandWrapper;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public class GrenadierBukkitWrapper extends Command implements PluginIdentifiableCommand {
    private final AbstractCommand command;
    private final CommandWrapper wrapper;
    private final LiteralCommandNode<CommandSource> grenadierNode;
    private final LiteralCommandNode<CommandSourceStack> vanillaNode;

    public GrenadierBukkitWrapper(AbstractCommand command,
                                  CommandWrapper wrapper,
                                  LiteralCommandNode<CommandSource> grenadierNode,
                                  LiteralCommandNode<CommandSourceStack> vanillaNode
    ) {
        super(command.getName());

        this.command = command;
        this.wrapper = wrapper;
        this.grenadierNode = grenadierNode;
        this.vanillaNode = vanillaNode;
    }

    @Override
    public @Nullable String getPermission() {
        return command.getPerm();
    }

    @Override
    public @Nullable Component permissionMessage() {
        return command.permissionMessage();
    }

    @Override
    public @NotNull String getName() {
        return command.getName();
    }

    @Override
    public @NotNull List<String> getAliases() {
        if (command.getAliases() == null || command.getAliases().length <= 0) {
            return Collections.EMPTY_LIST;
        }

        List<String> aliases = new ArrayList<>(Arrays.asList(command.getAliases()));
        aliases.remove(getName());

        return aliases;
    }

    @Override
    public @NotNull String getDescription() {
        return command.getDescription() == null ? "" : command.getDescription();
    }

    @Override
    public @NotNull Command setAliases(@NotNull List<String> aliases) {
        command.setAliases(aliases.toArray(String[]::new));
        return this;
    }

    @Override
    public @NotNull Command setDescription(@NotNull String description) {
        command.setDescription(description);
        return this;
    }

    @Override
    public void permissionMessage(@Nullable Component permissionMessage) {
        super.permissionMessage(permissionMessage);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        wrapper.run(
                VanillaCommandWrapper.getListener(sender),
                input(commandLabel, args)
        );

        return true;
    }

    @Override
    public boolean testPermissionSilent(@NotNull CommandSender target) {
        return command.test(CommandSource.of(target, command));
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        CommandSource source = CommandSource.of(sender, command);
        String input = input(command.getName(), args);

        CommandDispatcher<CommandSource> dispatcher = RoyalGrenadier.getDispatcher();
        ParseResults<CommandSource> results = RoyalGrenadier.getDispatcher().parse(input, source);

        List<String> result = new ArrayList<>();
        dispatcher.getCompletionSuggestions(results).thenAccept(suggestions -> {
            suggestions.getList().forEach(suggestion -> result.add(suggestion.getText()));
        });

        return result;
    }

    public String input(String label, String[] args) {
        if (args == null || args.length < 1) {
            return label;
        }

        return label + " " + Joiner.on(' ').join(args);
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return command.getPlugin();
    }
}