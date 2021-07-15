package net.forthecrown.royalgrenadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.forthecrown.royalgrenadier.arguments.RoyalArgumentsImpl;
import net.forthecrown.royalgrenadier.command.CommandWrapper;
import net.forthecrown.royalgrenadier.command.WrapperConverter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_17_R1.command.VanillaCommandWrapper;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
    public static Logger LOGGER;
    private static CommandDispatcher<CommandSource> dispatcher;
    private static net.minecraft.commands.Commands serverCommands;

    /**
     * Gets the dispatcher for the RoyalGrenadier
     * @return The dispatcher for the RoyalGrenadier
     */
    public static CommandDispatcher<CommandSource> getDispatcher() {
        return dispatcher;
    }

    /**
     * Registers the given AbstractCommand, making it useable. Not needed in most cases, as AbstractCommand's register() method calls this.
     * @param builder The command
     */
    public static void register(AbstractCommand builder){
        CommandWrapper wrapper = new CommandWrapper(builder);
        LiteralCommandNode<CommandSource> built = dispatcher.register(builder.getCommand());

        LiteralArgumentBuilder<CommandSourceStack> wrapped = new WrapperConverter(wrapper, builder, built).finish();
        LiteralCommandNode<CommandSourceStack> builtNms = serverCommands.getDispatcher().register(wrapped);

        VanillaCommandWrapper bukkitWrapper = new VanillaCommandWrapper(serverCommands, builtNms);
        if(builder.getAliases() != null) bukkitWrapper.setAliases(Arrays.asList(builder.getAliases()));

        if(builder.getDescription() != null) bukkitWrapper.setDescription(builder.getDescription());
        else bukkitWrapper.setDescription("");

        if(builder.getPermission() != null) bukkitWrapper.setPermission(builder.getPermission().getName());
        else bukkitWrapper.setPermission(null);

        bukkitWrapper.setPermissionMessage(builder.getPermissionMessage());

        CommandMap map = Bukkit.getCommandMap();
        map.register(builder.getName(), builder.getPlugin().getName(), bukkitWrapper);
        bukkitWrapper.register(map);
    }

    //:I I didn't wanna have this be a plugin, but I also couldn't
    //have a jar sitting around in the plugins folder lol
    @Override
    public void onLoad() {
        LOGGER = getLogger();

        dispatcher = new CommandDispatcher<>();
        serverCommands = DedicatedServer.getServer().vanillaCommandDispatcher;

        dispatcher.setConsumer((context, b, i) -> context.getSource().onCommandComplete(context, b, i));

        RoyalArgumentsImpl.init();
        CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = true;

        new TestCommand(this);
    }
}
