package net.forthecrown.royalgrenadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.forthecrown.royalgrenadier.arguments.RoyalArgumentsImpl;
import net.forthecrown.royalgrenadier.command.CommandWrapper;
import net.forthecrown.royalgrenadier.command.WrapperConverter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_18_R1.command.VanillaCommandWrapper;

import java.util.ArrayList;
import java.util.Arrays;

public class RoyalGrenadier {
    private static CommandDispatcher<CommandSource> dispatcher;
    private static Commands serverCommands;
    private static boolean initialized = false;

    /**
     * Gets the dispatcher for the RoyalGrenadier
     * @return The dispatcher for the RoyalGrenadier
     */
    public static CommandDispatcher<CommandSource> getDispatcher() {
        return dispatcher;
    }

    /**
     * Registers the given AbstractCommand, making it useable. Not needed in most cases, as {@link AbstractCommand#register()} calls this.
     * @param builder The command
     */
    public static void register(AbstractCommand builder){
        CommandWrapper wrapper = new CommandWrapper(builder);
        LiteralCommandNode<CommandSource> built = dispatcher.register(builder.getCommand());

        LiteralArgumentBuilder<CommandSourceStack> wrapped = new WrapperConverter(wrapper, builder, built).finish();
        LiteralCommandNode<CommandSourceStack> builtNms = serverCommands.getDispatcher().register(wrapped);

        VanillaCommandWrapper bukkitWrapper = new VanillaCommandWrapper(serverCommands, builtNms);
        if(builder.getAliases() != null) bukkitWrapper.setAliases(Arrays.asList(builder.getAliases()));
        else bukkitWrapper.setAliases(new ArrayList<>());

        if(builder.getDescription() != null) bukkitWrapper.setDescription(builder.getDescription());
        else bukkitWrapper.setDescription("");

        if(builder.getPermission() != null) bukkitWrapper.setPermission(builder.getPermission().getName());
        else bukkitWrapper.setPermission(null);

        bukkitWrapper.permissionMessage(builder.permissionMessage());

        CommandMap map = Bukkit.getCommandMap();
        map.register(builder.getName(), builder.getPlugin().getName(), bukkitWrapper);
        //bukkitWrapper.register(map);
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void initialize() {
        if(isInitialized()) return;

        dispatcher = new CommandDispatcher<>();
        serverCommands = DedicatedServer.getServer().vanillaCommandDispatcher;

        dispatcher.setConsumer((context, b, i) -> context.getSource().onCommandComplete(context, b, i));

        RoyalArgumentsImpl.init();

        initialized = true;
    }
}
