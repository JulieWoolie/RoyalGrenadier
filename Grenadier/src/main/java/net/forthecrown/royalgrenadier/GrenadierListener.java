package net.forthecrown.royalgrenadier;

import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.forthecrown.royalgrenadier.command.GrenadierBukkitWrapper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

class GrenadierListener implements Listener {
    // This prevents Bukkit from removing the vanilla
    // grenadier nodes
    @EventHandler(ignoreCancelled = true)
    public void onCommandRegistered(CommandRegisteredEvent<CommandSourceStack> event) {
        if (!(event.getCommand() instanceof GrenadierBukkitWrapper wrapper)) return;

        LiteralArgumentBuilder<CommandSourceStack> lit = Commands.literal(event.getCommandLabel())
                .executes(wrapper.getVanillaNode().getCommand())
                .requires(wrapper.getVanillaNode().getRequirement());

        for (var n: wrapper.getVanillaNode().getChildren()) {
            lit.then(n);
        }

        event.setLiteral(lit.build());
        Bukkit.getCommandMap().getKnownCommands().put(event.getCommandLabel(), wrapper);
    }
}