package net.forthecrown.royalgrenadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import lombok.Getter;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.forthecrown.royalgrenadier.command.CommandWrapper;
import net.forthecrown.royalgrenadier.command.GrenadierBukkitWrapper;
import net.forthecrown.royalgrenadier.command.WrapperTranslator;
import net.minecraft.commands.CommandSourceStack;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class RoyalGrenadier {
    /**
     * The dispatcher that handles all grenadier commands
     */
    @Getter
    private static CommandDispatcher<CommandSource> dispatcher;

    /**
     * The initialization state of grenadier, used to
     * prevent accidental double initialization
     */
    @Getter
    private static boolean initialized = false;

    /**
     * The logger Grenadier uses for errors
     */
    @Getter
    private static Logger logger;

    /**
     * The plugin that initialized grenadier
     */
    @Getter
    private static Plugin plugin;

    /**
     * Initializes the RoyalGrenadier.
     * <p>
     * If grenadier has already been initialized,
     * this method does nothing.
     * <p>
     * This will create the {@link CommandDispatcher}
     * object grenadier uses and create and register
     * an Event Listener: {@link GrenadierListener}.
     * It ensures that argument nodes the server has
     * and the argument nodes that grenadier has are
     * synced up.
     * <p>
     * To read more on grenadier -> vanilla command
     * translation, visit {@link WrapperTranslator}.
     * <p>
     * The logger object Grenadier may use will also
     * be the given plugin's logger
     *
     * @param plugin The plugin initializing Grenadier
     */
    @SuppressWarnings("deprecation") // Log4j smh
    public static void initialize(Plugin plugin) {
        if (isInitialized()) {
            return;
        }

        RoyalGrenadier.plugin = plugin;
        logger = plugin.getLog4JLogger();

        // Create dispatcher
        dispatcher = new CommandDispatcher<>();
        dispatcher.setConsumer((context, b, i) -> {
            context.getSource().onCommandComplete(context, b, i);
        });


        plugin.getServer()
                .getPluginManager()
                .registerEvents(new GrenadierListener(), plugin);

        initialized = true;
    }

    /**
     * Registers the given AbstractCommand, making it usable.
     * <p>
     * Not needed in most cases, as {@link AbstractCommand#register()} calls this.
     *
     * @param builder The command
     * @return The registered node.
     */
    public static LiteralCommandNode<CommandSource> register(AbstractCommand builder) {
        String fallBack = builder.getPlugin().getName().toLowerCase();
        CommandWrapper wrapper = new CommandWrapper(builder);

        LiteralCommandNode<CommandSource> built = dispatcher.register(builder.getCommand());
        LiteralArgumentBuilder<CommandSourceStack> wrapped = new WrapperTranslator(wrapper, builder, built).translate();
        LiteralCommandNode<CommandSourceStack> builtNms = wrapped.build();

        // Register aliases
        registerAliases(builder.getAliases(), built, dispatcher);

        GrenadierBukkitWrapper bukkitWrapper = new GrenadierBukkitWrapper(builder, wrapper, built, builtNms);

        var map = Bukkit.getCommandMap();
        map.register(fallBack, bukkitWrapper);
        return built;
    }

    static <S> void registerAliases(String[] aliases, LiteralCommandNode<S> node, CommandDispatcher<S> dispatcher) {
        if (aliases == null || aliases.length < 1) {
            return;
        }

        for (var s: aliases) {
            dispatcher.register(
                    LiteralArgumentBuilder.<S>literal(s)
                            .requires(node.getRequirement())
                            .executes(node.getCommand())
                            .redirect(node)
            );
        }
    }
}