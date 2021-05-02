package net.forthecrown.grenadier.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.RoyalGrenadier;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

public abstract class AbstractCommand implements Predicate<CommandSource> {

    private final String name;
    private final BrigadierCommand root;
    private final Plugin plugin;

    protected String[] aliases;
    protected String permission;
    protected String permissionMessage;
    protected String description;

    private boolean registered = false;

    protected AbstractCommand(@NotNull String name, @NotNull Plugin plugin){
        this.name = name;
        this.root = new BrigadierCommand(name, this);
        this.plugin = plugin;
    }

    protected abstract void createCommand(BrigadierCommand command);

    public final void register(){
        if(registered) return;
        createCommand(root);

        RoyalGrenadier.register(this);
        registered = true;
    }

    public boolean isRegistered(){
        return registered;
    }

    protected LiteralArgumentBuilder<CommandSource> argument(String name){
        return LiteralArgumentBuilder.literal(name);
    }

    protected <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, ArgumentType<T> type){
        return RequiredArgumentBuilder.argument(name, type);
    }

    protected SuggestionProvider<CommandSource> suggestMatching(String... strings){
        return suggestMatching(Arrays.asList(strings));
    }

    protected SuggestionProvider<CommandSource> suggestMatching(Collection<String> strings){
        return (c, b) -> {
            String token = b.getRemaining().toLowerCase();

            for (String s: strings){
                if(s.toLowerCase().startsWith(token)) b.suggest(s);
            }

            return b.buildFuture();
        };
    }

    protected SuggestionProvider<CommandSource> suggestMatching(Map<String, Message> suggestions){
        return (c, b) -> {
            String token = b.getRemaining().toLowerCase();

            for (Map.Entry<String, Message> entry: suggestions.entrySet()){
                if(entry.getKey().toLowerCase().startsWith(token)) b.suggest(entry.getKey(), entry.getValue());
            }

            return b.buildFuture();
        };
    }

    @Override
    public boolean test(CommandSource source) {
        return testPermissionSilent(source.asBukkit());
    }

    public boolean testPermissionSilent(CommandSender source){
        if(getPermission() == null || getPermission().isBlank()) return true;
        return source.hasPermission(getPermission());
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public String[] getAliases() {
        return aliases;
    }

    public void setAliases(String... aliases) {
        this.aliases = aliases;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getPermissionMessage() {
        return permissionMessage;
    }

    public void setPermissionMessage(String permissionMessage) {
        this.permissionMessage = permissionMessage;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BrigadierCommand getRoot() {
        return root;
    }
}
