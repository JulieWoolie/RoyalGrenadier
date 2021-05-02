package net.forthecrown.grenadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.forthecrown.royalgrenadier.command.CommandWrapper;
import net.forthecrown.royalgrenadier.command.WrapperConverter;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.command.VanillaCommandWrapper;

import java.util.Arrays;

public class RoyalGrenadier {

    private static CommandDispatcher<CommandSource> dispatcher;
    private static net.minecraft.server.v1_16_R3.CommandDispatcher serverDispatcher;
    private static final RoyalGrenadier INSTANCE = new RoyalGrenadier();

    public static void init(){
        dispatcher = new CommandDispatcher<>();
        serverDispatcher = ((CraftServer) Bukkit.getServer()).getHandle().getServer().getCommandDispatcher();

        dispatcher.setConsumer((context, b, i) -> context.getSource().onCommandComplete(context, b, i));
    }

    public static CommandDispatcher<CommandSource> getDispatcher() {
        return dispatcher;
    }

    public static void register(AbstractCommand builder){
        CommandWrapper wrapper = new CommandWrapper(builder);
        LiteralCommandNode<CommandSource> built = dispatcher.register(builder.getRoot());

        LiteralArgumentBuilder<CommandListenerWrapper> wrapped = new WrapperConverter(wrapper, builder, built).finish();
        LiteralCommandNode<CommandListenerWrapper> buildNms = serverDispatcher.a().register(wrapped);

        VanillaCommandWrapper bukkitWrapper = new VanillaCommandWrapper(serverDispatcher, buildNms);
        if(builder.getAliases() != null) bukkitWrapper.setAliases(Arrays.asList(builder.getAliases()));
        bukkitWrapper.setDescription(builder.getDescription());
        bukkitWrapper.setPermission(builder.getPermission());
        bukkitWrapper.setPermissionMessage(builder.getPermissionMessage());

        CommandMap map = Bukkit.getCommandMap();
        map.register(builder.getName(), builder.getPlugin().getName(), bukkitWrapper);
        bukkitWrapper.register(map);
    }
}
