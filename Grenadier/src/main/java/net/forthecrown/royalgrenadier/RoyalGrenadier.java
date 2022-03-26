package net.forthecrown.royalgrenadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.forthecrown.grenadier.CmdUtil;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.forthecrown.royalgrenadier.arguments.RoyalArgumentsImpl;
import net.forthecrown.royalgrenadier.command.CommandWrapper;
import net.forthecrown.royalgrenadier.command.WrapperConverter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_18_R2.command.VanillaCommandWrapper;

import java.util.*;

public class RoyalGrenadier {
    private static CommandDispatcher<CommandSource> dispatcher;
    private static Commands serverCommands;
    private static boolean initialized = false;
    private static Logger logger;

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

    /**
     * Registers the given AbstractCommand, making it useable. Not needed in most cases, as {@link AbstractCommand#register()} calls this.
     * @param builder The command
     */
    public static LiteralCommandNode<CommandSource> register(AbstractCommand builder) {
        String fallBack = builder.getPlugin().getName().toLowerCase();

        CommandWrapper wrapper = new CommandWrapper(builder);
        LiteralCommandNode<CommandSource> built = dispatcher.register(builder.getCommand());

        LiteralArgumentBuilder<CommandSourceStack> wrapped = new WrapperConverter(wrapper, builder, built).finish();
        LiteralCommandNode<CommandSourceStack> builtNms = serverCommands.getDispatcher().register(wrapped);

        // Apply builder's parameters to bukkitWrapper, so aliases, permissions, description,
        // all that
        VanillaCommandWrapper bukkitWrapper = new VanillaCommandWrapper(serverCommands, builtNms);
        List<String> aliases = builder.getAliases() == null ? new ObjectArrayList<>() : new ObjectArrayList<>(builder.getAliases());
        aliases.remove(builder.getName());
        bukkitWrapper.setAliases(aliases);

        if(builder.getDescription() != null) bukkitWrapper.setDescription(builder.getDescription());
        else bukkitWrapper.setDescription("");

        if(builder.getPermission() != null) bukkitWrapper.setPermission(builder.getPermission().getName());
        else bukkitWrapper.setPermission(null);

        bukkitWrapper.permissionMessage(builder.permissionMessage());

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

     private static void registerLabel(String l,
                                       Map<String, Command> map,
                                       VanillaCommandWrapper wrapper,
                                       boolean alias,
                                       LiteralCommandNode<CommandSource> built,
                                       LiteralCommandNode<CommandSourceStack> builtNms
     ) {
         map.put(l, wrapper);

         if(!alias) return;

         dispatcher.getRoot().removeCommand(l);
         dispatcher.register(
                 CmdUtil.literal(l)
                         .requires(built.getRequirement())
                         .redirect(built)
         );

         serverCommands.getDispatcher().getRoot().removeCommand(l);

         serverCommands.getDispatcher().register(
                 Commands.literal(l)
                         .requires(builtNms.getRequirement())
                         .redirect(builtNms)
         );
     }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void initialize(Logger logger) {
        if(isInitialized()) return;

        RoyalGrenadier.logger = logger;
        dispatcher = new CommandDispatcher<>();
        serverCommands = DedicatedServer.getServer().vanillaCommandDispatcher;

        dispatcher.setConsumer((context, b, i) -> context.getSource().onCommandComplete(context, b, i));

        RoyalArgumentsImpl.init();

        initialized = true;
    }
}