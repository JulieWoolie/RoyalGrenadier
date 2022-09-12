package net.forthecrown.royalgrenadier;

import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.forthecrown.royalgrenadier.command.GrenadierBukkitWrapper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Ensures the vanilla argument nodes are synced
 * up to their grenadier counterparts.
 * <p>
 * This listener ensures that the colors of the characters
 * typed into chat for commands for grenadier commands are
 * correct and that the chat is able to accurately reflect
 * when errors are thrown.
 * <p>
 * Basically this makes chat colors cool
 *
 * @see net.forthecrown.royalgrenadier.command.WrapperTranslator
 */
class GrenadierListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onCommandRegistered(CommandRegisteredEvent<CommandSourceStack> event) {
        if (!(event.getCommand() instanceof GrenadierBukkitWrapper wrapper)) {
            return;
        }

        var literal = Commands.literal(event.getCommandLabel());
        copyNode(literal, wrapper.getVanillaNode());

        var built = literal.build();

        event.setLiteral(built);
        addToServerDispatcher(built);
    }

    private void addToServerDispatcher(LiteralCommandNode<CommandSourceStack> node) {
        var vanilla = DedicatedServer.getServer().vanillaCommandDispatcher;
        var root = vanilla.getDispatcher().getRoot();

        root.removeCommand(node.getName());
        root.addChild(node);
    }

    private void copyNode(LiteralArgumentBuilder<CommandSourceStack> target,
                          LiteralCommandNode<CommandSourceStack> source
    ) {
        target.requires(source.getRequirement());
        target.executes(source.getCommand());

        var children = source.getChildren();

        if (!children.isEmpty()) {
            for (var c: children) {
                target.then(c);
            }
        } else {
            target.forward(
                    source.getRedirect(),
                    source.getRedirectModifier(),
                    source.isFork()
            );
        }
    }
}