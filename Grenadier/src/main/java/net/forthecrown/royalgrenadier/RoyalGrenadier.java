package net.forthecrown.royalgrenadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.forthecrown.grenadier.CmdUtil;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.forthecrown.royalgrenadier.command.CommandWrapper;
import net.forthecrown.royalgrenadier.command.GrenadierBukkitWrapper;
import net.forthecrown.royalgrenadier.command.WrapperConverter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class RoyalGrenadier {
    private static CommandDispatcher<CommandSource> dispatcher;
    private static CommandDispatcher<CommandSourceStack> serverDispatcher;
    private static Commands serverCommands;
    private static boolean initialized = false;

    private static Logger logger;
    private static Plugin plugin;

    /**
     * Gets the dispatcher for the RoyalGrenadier
     * @return The dispatcher for the RoyalGrenadier
     */
    public static CommandDispatcher<CommandSource> getDispatcher() {
        return dispatcher;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    /**
     * Registers the given AbstractCommand, making it useable. Not needed in most cases, as {@link AbstractCommand#register()} calls this.
     * @param builder The command
     */
    public static LiteralCommandNode<CommandSource> register(AbstractCommand builder) {
        String fallBack = builder.getPlugin().getName().toLowerCase();

        CommandWrapper wrapper = new CommandWrapper(builder);

        // There might be a command with this name already
        // registered, so remove it lol. Last come, first served
        dispatcher.getRoot().removeCommand(builder.getName());
        serverDispatcher.getRoot().removeCommand(builder.getName());

        LiteralCommandNode<CommandSource> built = dispatcher.register(builder.getCommand());
        LiteralArgumentBuilder<CommandSourceStack> wrapped = new WrapperConverter(wrapper, builder, built).finish();
        LiteralCommandNode<CommandSourceStack> builtNms = serverCommands.getDispatcher().register(wrapped);

        // Apply builder's parameters to bukkitWrapper, so aliases, permissions, description,
        // all that
        GrenadierBukkitWrapper bukkitWrapper = new GrenadierBukkitWrapper(builder, wrapper, built, builtNms);

        SimpleCommandMap map = (SimpleCommandMap) Bukkit.getCommandMap();
        Map<String, Command> cmds = map.getKnownCommands();

        Set<String> allLabels = new HashSet<>(bukkitWrapper.getAliases());
        allLabels.add(builder.getName());

        for (String s: allLabels) {
            String label = s.toLowerCase(Locale.ROOT);

            boolean alias = !label.equalsIgnoreCase(builder.getName());

            registerLabel(fallBack + ':' + label, cmds, bukkitWrapper, alias, built, builtNms);
            registerLabel(label, cmds, bukkitWrapper, alias, built, builtNms);
        }

        bukkitWrapper.register(map);
        return built;
    }

    static void registerLabel(String l,
                                       Map<String, Command> map,
                                       GrenadierBukkitWrapper wrapper,
                                       boolean alias,
                                       LiteralCommandNode<CommandSource> built,
                                       LiteralCommandNode<CommandSourceStack> builtNms
     ) {
         map.put(l, wrapper);

         if(!alias) return;

         dispatcher.getRoot().removeCommand(l);
         LiteralArgumentBuilder<CommandSource> node = CmdUtil.literal(l)
                 .requires(built.getRequirement())
                 .executes(built.getCommand());

         for (CommandNode n: built.getChildren()) {
             node.then(n);
         }

         dispatcher.register(node);

         serverDispatcher.getRoot().removeCommand(l);
         serverDispatcher.register(
                 Commands.literal(l)
                         .requires(builtNms.getRequirement())
                         .redirect(builtNms)
         );
     }

    public static boolean isInitialized() {
        return initialized;
    }

    @SuppressWarnings("deprecation") // Log4j smh
    public static void initialize(Plugin plugin) {
        if(isInitialized()) return;

        RoyalGrenadier.plugin = plugin;
        logger = plugin.getLog4JLogger();
        dispatcher = new CommandDispatcher<>();
        serverCommands = ((CraftServer) Bukkit.getServer()).getServer().vanillaCommandDispatcher;
        serverDispatcher = serverCommands.getDispatcher();

        dispatcher.setConsumer((context, b, i) -> context.getSource().onCommandComplete(context, b, i));

        plugin.getServer().getPluginManager()
                .registerEvents(new GrenadierListener(), plugin);

        initialized = true;
    }
}